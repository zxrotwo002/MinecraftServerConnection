package server.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.MessageHandler;
import server.Server;
import server.schemas.KickPlayer;
import server.schemas.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Players implements MessageHandler {
    private final String method = "minecraft:players";
    //id-range 6000-6999
    private final int idBase = 6000;

    private final int timeout = 10;

    private final Map<Integer, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();

    //6010
    private Player[] players;

    server.Server server;

    public Players(Server server) {
        this.server = server;
    }

    //6010
    public Player[] getPlayers() {
        int id = 10;
        String method = this.method;

        CompletableFuture<JsonObject> future = sendMessage(id, method);

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

    //6020
    public boolean kickPlayers(KickPlayer[] kickPlayers) {
        JsonObject params = new JsonObject();

        int id = 20;
        String method = getSubmethod("kick");
        params.add("kick", server.gson.toJsonTree(kickPlayers));

        CompletableFuture<JsonObject> future = sendMessage(id, method, params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);

            List<Player> playerList = new ArrayList<>(Arrays.asList(convertJsonObject(response)));
            for (KickPlayer kickPlayer : kickPlayers) {
                if (playerList.stream().noneMatch(kickPlayer.player::equals)) return false;
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
        Player[] players1 = new Player[response.get("result").getAsJsonArray().size()];
        AtomicInteger i = new AtomicInteger(0);
        response.get("result").getAsJsonArray().forEach(jsonElement -> {
            players1[i.getAndIncrement()] = server.gson.fromJson(jsonElement, Player.class);
        });
        return players1;
    }
}
