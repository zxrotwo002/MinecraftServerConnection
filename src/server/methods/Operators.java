package server.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.MessageHandler;
import server.Server;
import server.schemas.Operator;
import server.schemas.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Operators implements MessageHandler {
    private final String method = "minecraft:operators";
    //id-range 5000-5999
    private final int idBase = 5000;

    private final int timeout = 10;

    private final Map<Integer, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();

    //5010
    private Operator[] operators;

    server.Server server;
    public Operators(Server server) {
        this.server = server;
    }

    //5010
    public Operator[] getOperators() {
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

    //5020
    public boolean setOperators(Operator[] operators) {
        JsonObject params = new JsonObject();

        int id = 20;
        String method = getSubmethod("set");
        params.add("operators",server.gson.toJsonTree(operators));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return Arrays.equals(convertJsonObject(response), operators);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return false;
    }

    //5030
    public boolean addOperators(Operator[] operators) {
        JsonObject params = new JsonObject();

        int id = 30;
        String method = getSubmethod("add");
        params.add("add",server.gson.toJsonTree(operators));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);

            List<Operator> operatorList = new ArrayList<>(Arrays.asList(convertJsonObject(response)));
            for (Operator operator : operators) {
                if (operatorList.stream().noneMatch(operator::equals)) return false;
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

    //5040
    public boolean removePlayers(Player[] players) {
        JsonObject params = new JsonObject();

        int id = 40;
        String method = getSubmethod("remove");
        params.add("remove",server.gson.toJsonTree(players));

        CompletableFuture<JsonObject> future = sendMessage(id,method,params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);

            List<Operator> operatorList = new ArrayList<>(Arrays.asList(convertJsonObject(response)));
            for (Player player : players) {
                if (operatorList.stream().anyMatch(operator -> operator.player.equals(player))) return false;
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

    //5050
    public boolean clearOperators() {
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

    private Operator[] convertJsonObject(JsonObject response) {
        Operator[] operators1 = new Operator[response.get("result").getAsJsonArray().size()];
        AtomicInteger i = new AtomicInteger(0);
        response.get("result").getAsJsonArray().forEach(jsonElement -> {
            operators1[i.getAndIncrement()] = server.gson.fromJson(jsonElement,Operator.class);
        });
        return operators1;
    }
}
