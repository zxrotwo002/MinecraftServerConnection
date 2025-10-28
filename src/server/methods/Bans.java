package server.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.MessageHandler;
import server.Server;
import server.schemas.Player;
import server.schemas.UserBan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Bans implements MessageHandler {
    private final String method = "minecraft:bans";
    //id-range 2000-2999
    private final int idBase = 2000;

    private final int timeout = 10;

    private final Map<Integer, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();

    //2010
    private UserBan[] banlist;

    server.Server server;
    public Bans(Server server) {
        this.server = server;
    }

    //2010
    public UserBan[] getBanlist() {
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

    //2020
    public boolean setBanlist(UserBan[] banlist) {
        JsonObject params = new JsonObject();

        int id = 20;
        String method = getSubmethod("set");
        params.add("bans",server.gson.toJsonTree(banlist));

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

    //2030
    public boolean addPlayers(UserBan[] players) {
        JsonObject params = new JsonObject();

        int id = 30;
        String method = getSubmethod("add");
        params.add("add",server.gson.toJsonTree(players));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);

            UserBan[] bans = convertJsonObject(response);
            List<UserBan> banList = new ArrayList<>(Arrays.asList(bans));
            for (UserBan ban : players) {
                if (banList.stream().noneMatch(ban::equals)) return false;
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

    //2040
    public boolean removePlayers(Player[] players) {
        JsonObject params = new JsonObject();

        int id = 40;
        String method = getSubmethod("remove");
        params.add("remove",server.gson.toJsonTree(players));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);

            UserBan[] bans = convertJsonObject(response);
            List<UserBan> banList = new ArrayList<>(Arrays.asList(bans));
            for (Player player : players) {
                if (banList.stream().anyMatch(userBan -> userBan.player.equals(player))) return false;
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

    //2050
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

    private UserBan[] convertJsonObject(JsonObject response) {
        UserBan[] bans = new UserBan[response.get("result").getAsJsonArray().size()];
        AtomicInteger i = new AtomicInteger(0);
        response.get("result").getAsJsonArray().forEach(jsonElement -> {
            bans[i.getAndIncrement()] = server.gson.fromJson(jsonElement,UserBan.class);
        });
        return bans;
    }
}
