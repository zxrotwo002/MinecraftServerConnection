package server.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.MessageHandler;
import server.schemas.ServerState;
import server.schemas.SystemMessage;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements MessageHandler {
    private final String method = "minecraft:server";
    //id-range 7000-7999
    private final int idBase = 7000;

    private final int timeout = 10;

    private final Map<Integer, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();

    //7010
    private ServerState status;
    //7040
    private boolean systemMessage;

    server.Server server;
    public Server(server.Server server) {
        this.server = server;
    }


    //7010
    public ServerState getStatus() {
        int id = 10;
        String method = getSubmethod("status");

        CompletableFuture<JsonObject> future = sendMessage(id,method);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return server.gson.fromJson(response.get("result").getAsJsonObject(),ServerState.class);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return null;
    }

    //7020
    public boolean save(boolean flush) {
        JsonObject params = new JsonObject();
        int id = 20;
        String method = getSubmethod("save");
        params.addProperty("flush", flush);

        CompletableFuture<JsonObject> future = sendMessage(id, method, params);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return false;
    }

    //7030
    public boolean stop() {
        int id = 30;
        String method = getSubmethod("stop");

        CompletableFuture<JsonObject> future = sendMessage(id, method);

        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return false;
    }

    //7040
    public boolean sendMessage(SystemMessage message) {
        JsonObject params = new JsonObject();

        int id = 40;
        String method = getSubmethod("system_message");
        params.add("message",server.gson.toJsonTree(message));

        CompletableFuture<JsonObject> responseFuture = sendMessage(id,method,params);

        try {
            JsonObject response = responseFuture.get(timeout, TimeUnit.SECONDS);
            pendingRequests.remove(id);
            return response.get("result").getAsBoolean();
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
            //11 - status heartbeat
            case 11 -> {
                setStatus(server.gson.fromJson(object.get("params").getAsJsonArray().get(0),ServerState.class));
            }
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

    private void setStatus(ServerState status) {
        System.out.println(status);
    }
}