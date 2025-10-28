package server.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.MessageHandler;
import server.Server;
import server.schemas.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IPBans implements MessageHandler {
    private final String method = "minecraft:ip_bans";
    //id-range 4000-4999
    private final int idBase = 4000;

    private final int timeout = 10;

    private final Map<Integer, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();

    //4010
    private IPBan[] ipBans;

    server.Server server;
    public IPBans(Server server) {
        this.server = server;
    }

    //4010
    public IPBan[] getIpBans() {
        int id = 10;
        String method = this.method;

        CompletableFuture<JsonObject> future = sendMessage(id,method);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return convertJsonObject(response);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }

        return null;
    }

    //4020
    public boolean setBanlist(IPBan[] banlist) {
        JsonObject params = new JsonObject();

        int id = 20;
        String method = getSubmethod("set");
        params.add("banlist",server.gson.toJsonTree(banlist));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return Arrays.equals(convertJsonObject(response), banlist);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return false;
    }

    //4030
    public boolean addPlayers(IncomingIPBan[] players) {
        JsonObject params = new JsonObject();

        int id = 30;
        String method = getSubmethod("add");
        params.add("add",server.gson.toJsonTree(players));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            IPBan[] bans = convertJsonObject(response);
            List<IPBan> banList = new ArrayList<>(Arrays.asList(bans));
            for (IncomingIPBan ban : players) {
                if (banList.stream().noneMatch(ipBan ->
                        (ipBan.reason == null || ipBan.reason.equals(ban.reason)) &&
                        (ipBan.source == null || ipBan.source.equals(ban.source)) &&
                        (ipBan.expires == null || ipBan.expires.equals(ban.expires)))) return false;
            }
            return true;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return false;
    }

    //4040
    public boolean removeIPs(String[] ips) {
        JsonObject params = new JsonObject();

        int id = 40;
        String method = getSubmethod("remove");
        params.add("ip",server.gson.toJsonTree(ips));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);

            IPBan[] bans = convertJsonObject(response);
            List<IPBan> ipBanList = new ArrayList<>(Arrays.asList(bans));
            for (String ip : ips) {
                if (ipBanList.stream().anyMatch(ipBan -> ipBan.ip.equals(ip))) return false;
            }
            return true;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return false;
    }

    //4050
    public boolean clearBanlist() {
        int id = 50;
        String method = getSubmethod("clear");

        CompletableFuture<JsonObject> future = sendMessage(id,method);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return convertJsonObject(response).length == 0;
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return false;
    }

    private String getSubmethod(String... strings) {
        StringBuilder sb = new StringBuilder(method);
        for (String s : strings) {
            sb.append("/").append(s);
        }
        return sb.toString();
    }

    @Override
    public void handleMessage(JsonObject object) {
        int id = object.get("id").getAsInt() - idBase;

        CompletableFuture<JsonObject> pendingFuture = pendingRequests.remove(id);
        if (pendingFuture != null) {
            pendingFuture.complete(object);
            return;
        }

        switch (id) {
        }
    }

    private CompletableFuture<JsonObject> sendMessage(int id, String method) {
        JsonObject object = new JsonObject();
        object.addProperty("id", (idBase + id));
        object.addProperty("method", method);
        CompletableFuture<JsonObject> responseFuture = new CompletableFuture<>();
        pendingRequests.put(id, responseFuture);

        server.sendMessage(server.gson.toJson(object));

        return responseFuture;
    }

    private CompletableFuture<JsonObject> sendMessage(int id, String method, JsonElement params) {
        JsonObject object = new JsonObject();
        object.addProperty("id", (idBase + id));
        object.addProperty("method", method);
        object.add("params", params);
        CompletableFuture<JsonObject> responseFuture = new CompletableFuture<>();
        pendingRequests.put(id, responseFuture);

        server.sendMessage(server.gson.toJson(object));

        return responseFuture;
    }

    private IPBan[] convertJsonObject(JsonObject response) {
        IPBan[] bans = new IPBan[response.get("result").getAsJsonArray().size()];
        AtomicInteger i = new AtomicInteger(0);
        response.get("result").getAsJsonArray().forEach(jsonElement -> {
            bans[i.getAndIncrement()] = server.gson.fromJson(jsonElement,IPBan.class);
        });
        return bans;
    }
}
