package server.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.MessageHandler;
import server.Server;
import server.schemas.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Allowlist implements MessageHandler {
    private final String method = "minecraft:allowlist";
    //id-range 1000-1999
    private final int idBase = 1000;

    private final int timeout = 10;

    private final Map<Integer, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();

    //1010
    private Player[] allowlist;

    server.Server server;
    public Allowlist(Server server) {
        this.server = server;
    }

    //1010
    public Player[] getAllowlist() {
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

    //1020
    public boolean setAllowlist(Player[] players) {
        JsonObject params = new JsonObject();

        int id = 20;
        String method = getSubmethod("set");
        params.add("players",server.gson.toJsonTree(players));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return Arrays.equals(convertJsonObject(response), players);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return false;
    }

    //1030
    public boolean addPlayers(Player[] players) {
        JsonObject params = new JsonObject();

        int id = 30;
        String method = getSubmethod("add");
        params.add("add",server.gson.toJsonTree(players));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);

            Player[] players1 = convertJsonObject(response);
            List<Player> playerList = new ArrayList<>(Arrays.asList(players1));
            for (Player player : players) {
                if (playerList.stream().noneMatch(player::equals)) return false;
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

    //1040
    public boolean removePlayers(Player[] players) {
        JsonObject params = new JsonObject();

        int id = 40;
        String method = getSubmethod("remove");
        params.add("remove",server.gson.toJsonTree(players));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);

            Player[] players1 = convertJsonObject(response);
            List<Player> playerList = new ArrayList<>(Arrays.asList(players1));
            for (Player player : players) {
                if (playerList.stream().anyMatch(player1 -> player1.equals(player))) return false;
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

    //1050
    public boolean clearAllowlist() {
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

    private Player[] convertJsonObject(JsonObject response) {
        Player[] allowlist = new Player[response.get("result").getAsJsonArray().size()];
        AtomicInteger i = new AtomicInteger(0);
        response.get("result").getAsJsonArray().forEach(jsonElement -> {
            allowlist[i.getAndIncrement()] = server.gson.fromJson(jsonElement,Player.class);
        });
        return allowlist;
    }
}
