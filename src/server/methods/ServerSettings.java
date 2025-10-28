package server.methods;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.MessageHandler;
import server.Server;
import server.enums.Difficulty;
import server.enums.Gamemode;

import java.util.Map;
import java.util.concurrent.*;

public class ServerSettings implements MessageHandler {
    private final String method = "minecraft:serversettings";
    //id-range 8000-8999
    private final int idBase = 8000;

    private final int timeout = 10;

    private final Map<Integer, CompletableFuture<JsonObject>> pendingRequests = new ConcurrentHashMap<>();

    // --- Private fields to cache last known values ---
    private boolean autosave;
    private Difficulty difficulty;
    private boolean enforceAllowlist;
    private boolean useAllowlist;
    private int maxPlayers;
    private int pauseWhenEmptySeconds;
    private int playerIdleTimeout;
    private boolean allowFlight;
    private String motd;
    private int spawnProtectionRadius;
    private boolean forceGamemode;
    private Gamemode gamemode;
    private int viewDistance;
    private int simulationDistance;
    private boolean acceptTransfers;
    private int statusHeartbeatInterval;
    private int operatorUserPermissionLevel;
    private boolean hideOnlinePlayers;
    private boolean statusReplies;
    private int entityBroadcastRange;


    Server server;

    public ServerSettings(Server server) {
        this.server = server;
    }

    // 8010 - Autosave
    public boolean isAutosave() {
        int id = 10;
        String method = getSubmethod("autosave");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            autosave = response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return autosave;
    }

    // 8011
    public void setAutosave(boolean enable) {
        JsonObject params = new JsonObject();
        int id = 11;
        String method = getSubmethod("autosave", "set");
        params.addProperty("enable", enable);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.autosave = response.get("result").getAsBoolean();
            if (this.autosave != enable) System.err.println("Failed setting autosave");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8020 - Difficulty
    public Difficulty getDifficulty() {
        int id = 20;
        String method = getSubmethod("difficulty");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            difficulty = Difficulty.valueOf(response.get("result").getAsString().toUpperCase());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return difficulty;
    }

    // 8021
    public void setDifficulty(Difficulty difficulty) {
        JsonObject params = new JsonObject();
        int id = 21;
        String method = getSubmethod("difficulty", "set");
        params.addProperty("difficulty", difficulty.name().toLowerCase());
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.difficulty = Difficulty.valueOf(response.get("result").getAsString().toUpperCase());
            if (this.difficulty != difficulty) System.err.println("Failed setting difficulty");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8030 - Enforce Allowlist
    public boolean isEnforceAllowlist() {
        int id = 30;
        String method = getSubmethod("enforce_allowlist");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            enforceAllowlist = response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return enforceAllowlist;
    }

    // 8031
    public void setEnforceAllowlist(boolean enforce) {
        JsonObject params = new JsonObject();
        int id = 31;
        String method = getSubmethod("enforce_allowlist", "set");
        params.addProperty("enforce", enforce);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.enforceAllowlist = response.get("result").getAsBoolean();
            if (this.enforceAllowlist != enforce) System.err.println("Failed setting enforce allowlist");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8040 - Use Allowlist
    public boolean isUseAllowlist() {
        int id = 40;
        String method = getSubmethod("use_allowlist");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            useAllowlist = response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return useAllowlist;
    }

    // 8041
    public void setUseAllowlist(boolean use) {
        JsonObject params = new JsonObject();
        int id = 41;
        String method = getSubmethod("use_allowlist", "set");
        params.addProperty("use", use);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.useAllowlist = response.get("result").getAsBoolean();
            if (this.useAllowlist != use) System.err.println("Failed setting use allowlist");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8050 - Max Players
    public int getMaxPlayers() {
        int id = 50;
        String method = getSubmethod("max_players");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            maxPlayers = response.get("result").getAsInt();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return maxPlayers;
    }

    // 8051
    public void setMaxPlayers(int max) {
        JsonObject params = new JsonObject();
        int id = 51;
        String method = getSubmethod("max_players", "set");
        params.addProperty("max", max);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.maxPlayers = response.get("result").getAsInt();
            if (this.maxPlayers != max) System.err.println("Failed setting max players");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8060 - Pause When Empty
    public int getPauseWhenEmptySeconds() {
        int id = 60;
        String method = getSubmethod("pause_when_empty_seconds");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            pauseWhenEmptySeconds = response.get("result").getAsInt();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return pauseWhenEmptySeconds;
    }

    // 8061
    public void setPauseWhenEmptySeconds(int seconds) {
        JsonObject params = new JsonObject();
        int id = 61;
        String method = getSubmethod("pause_when_empty_seconds", "set");
        params.addProperty("seconds", seconds);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.pauseWhenEmptySeconds = response.get("result").getAsInt();
            if (this.pauseWhenEmptySeconds != seconds) System.err.println("Failed setting pause when empty");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8070 - Player Idle Timeout
    public int getPlayerIdleTimeout() {
        int id = 70;
        String method = getSubmethod("player_idle_timeout");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            playerIdleTimeout = response.get("result").getAsInt();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return playerIdleTimeout;
    }

    // 8071
    public void setPlayerIdleTimeout(int seconds) {
        JsonObject params = new JsonObject();
        int id = 71;
        String method = getSubmethod("player_idle_timeout", "set");
        params.addProperty("seconds", seconds);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.playerIdleTimeout = response.get("result").getAsInt();
            if (this.playerIdleTimeout != seconds) System.err.println("Failed setting player idle timeout");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8080 - Allow Flight
    public boolean isAllowFlight() {
        int id = 80;
        String method = getSubmethod("allow_flight");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            allowFlight = response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return allowFlight;
    }

    // 8081
    public void setAllowFlight(boolean allowed) {
        JsonObject params = new JsonObject();
        int id = 81;
        String method = getSubmethod("allow_flight", "set");
        params.addProperty("allowed", allowed);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.allowFlight = response.get("result").getAsBoolean();
            if (this.allowFlight != allowed) System.err.println("Failed setting allow flight");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8090 - MOTD
    public String getMotd() {
        int id = 90;
        String method = getSubmethod("motd");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            motd = response.get("result").getAsString();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return motd;
    }

    // 8091
    public void setMotd(String message) {
        JsonObject params = new JsonObject();
        int id = 91;
        String method = getSubmethod("motd", "set");
        params.addProperty("message", message);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.motd = response.get("result").getAsString();
            if (!this.motd.equals(message)) System.err.println("Failed setting motd");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8100 - Spawn Protection Radius
    public int getSpawnProtectionRadius() {
        int id = 100;
        String method = getSubmethod("spawn_protection_radius");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            spawnProtectionRadius = response.get("result").getAsInt();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return spawnProtectionRadius;
    }

    // 8101
    public void setSpawnProtectionRadius(int radius) {
        JsonObject params = new JsonObject();
        int id = 101;
        String method = getSubmethod("spawn_protection_radius", "set");
        params.addProperty("radius", radius);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.spawnProtectionRadius = response.get("result").getAsInt();
            if (this.spawnProtectionRadius != radius) System.err.println("Failed setting spawn protection radius");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8110 - Force Gamemode
    public boolean isForceGamemode() {
        int id = 110;
        String method = getSubmethod("force_game_mode");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            forceGamemode = response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return forceGamemode;
    }

    // 8111
    public void setForceGamemode(boolean force) {
        JsonObject params = new JsonObject();
        int id = 111;
        String method = getSubmethod("force_game_mode", "set");
        params.addProperty("force", force);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.forceGamemode = response.get("result").getAsBoolean();
            if (this.forceGamemode != force) System.err.println("Failed setting force gamemode");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8120 - Gamemode
    public Gamemode getGamemode() {
        int id = 120;
        String method = getSubmethod("game_mode");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            gamemode = Gamemode.valueOf(response.get("result").getAsString().toUpperCase());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return gamemode;
    }

    // 8121
    public void setGamemode(Gamemode gamemode) {
        JsonObject params = new JsonObject();
        int id = 121;
        String method = getSubmethod("game_mode", "set");
        params.addProperty("mode", gamemode.name().toLowerCase());
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.gamemode = Gamemode.valueOf(response.get("result").getAsString().toUpperCase());
            if (this.gamemode != gamemode) System.err.println("Failed setting gamemode");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8130 - View Distance (EXISTING)
    public int getViewDistance() {
        int id = 130;
        String method = getSubmethod("view_distance");
        CompletableFuture<JsonObject> responseFuture = sendMessage(id, method);
        try {
            JsonObject response = responseFuture.get(timeout, TimeUnit.SECONDS);
            viewDistance = response.get("result").getAsInt();
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } finally {
            pendingRequests.remove(id);
        }
        return viewDistance;
    }

    // 8131 - View Distance Set (EXISTING)
    public void setViewDistance(int viewDistance) {
        JsonObject params = new JsonObject();
        int id = 131;
        String method = getSubmethod("view_distance", "set");
        params.addProperty("distance", viewDistance);
        CompletableFuture<JsonObject> responseFuture = sendMessage(id, method, params);
        try {
            JsonObject response = responseFuture.get(timeout, TimeUnit.SECONDS);
            int result = response.get("result").getAsInt();
            if (result == viewDistance) this.viewDistance = result;
            else System.err.println("Failed setting view distance");
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8140 - Simulation Distance (EXISTING)
    public int getSimulationDistance() {
        int id = 140;
        String method = getSubmethod("simulation_distance");
        CompletableFuture<JsonObject> responseFuture = sendMessage(id, method);
        try {
            JsonObject response = responseFuture.get(timeout, TimeUnit.SECONDS);
            simulationDistance = response.get("result").getAsInt();
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } finally {
            pendingRequests.remove(id);
        }
        return simulationDistance;
    }

    // 8141 - Simulation Distance Set (EXISTING)
    public void setSimulationDistance(int simulationDistance) {
        JsonObject params = new JsonObject();
        int id = 141;
        String method = getSubmethod("simulation_distance", "set");
        params.addProperty("distance", simulationDistance);
        CompletableFuture<JsonObject> responseFuture = sendMessage(id, method, params);
        try {
            JsonObject response = responseFuture.get(timeout, TimeUnit.SECONDS);
            int result = response.get("result").getAsInt();
            if (result == simulationDistance) this.simulationDistance = result;
            else System.err.println("Failed setting simulation distance");
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8150 - Accept Transfers
    public boolean isAcceptTransfers() {
        int id = 150;
        String method = getSubmethod("accept_transfers");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            acceptTransfers = response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return acceptTransfers;
    }

    // 8151
    public void setAcceptTransfers(boolean accept) {
        JsonObject params = new JsonObject();
        int id = 151;
        String method = getSubmethod("accept_transfers", "set");
        params.addProperty("accept", accept);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.acceptTransfers = response.get("result").getAsBoolean();
            if (this.acceptTransfers != accept) System.err.println("Failed setting accept transfers");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8160 - Status Heartbeat Interval (EXISTING)
    public int getStatusHeartbeatInterval() {
        int id = 160;
        String method = getSubmethod("status_heartbeat_interval");
        CompletableFuture<JsonObject> responseFuture = sendMessage(id, method);
        try {
            JsonObject response = responseFuture.get(timeout, TimeUnit.SECONDS);
            statusHeartbeatInterval = response.get("result").getAsInt();
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } finally {
            pendingRequests.remove(id);
        }
        return statusHeartbeatInterval;
    }

    // 8161 - Status Heartbeat Interval Set (EXISTING)
    public void setStatusHeartbeatInterval(int statusHeartbeatInterval) {
        JsonObject params = new JsonObject();
        int id = 161;
        String method = getSubmethod("status_heartbeat_interval", "set");
        params.addProperty("seconds", statusHeartbeatInterval);
        CompletableFuture<JsonObject> responseFuture = sendMessage(id, method, params);
        try {
            JsonObject response = responseFuture.get(timeout, TimeUnit.SECONDS);
            int result = response.get("result").getAsInt();
            if (result == statusHeartbeatInterval) this.statusHeartbeatInterval = result;
            else System.err.println("Failed setting status heartbeat interval");
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8170 - Operator User Permission Level
    public int getOperatorUserPermissionLevel() {
        int id = 170;
        String method = getSubmethod("operator_user_permission_level");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            operatorUserPermissionLevel = response.get("result").getAsInt();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return operatorUserPermissionLevel;
    }

    // 8171
    public void setOperatorUserPermissionLevel(int level) {
        JsonObject params = new JsonObject();
        int id = 171;
        String method = getSubmethod("operator_user_permission_level", "set");
        params.addProperty("level", level);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.operatorUserPermissionLevel = response.get("result").getAsInt();
            if (this.operatorUserPermissionLevel != level) System.err.println("Failed setting operator permission level");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8180 - Hide Online Players
    public boolean isHideOnlinePlayers() {
        int id = 180;
        String method = getSubmethod("hide_online_players");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            hideOnlinePlayers = response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return hideOnlinePlayers;
    }

    // 8181
    public void setHideOnlinePlayers(boolean hide) {
        JsonObject params = new JsonObject();
        int id = 181;
        String method = getSubmethod("hide_online_players", "set");
        params.addProperty("hide", hide);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.hideOnlinePlayers = response.get("result").getAsBoolean();
            if (this.hideOnlinePlayers != hide) System.err.println("Failed setting hide online players");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8190 - Status Replies
    public boolean isStatusReplies() {
        int id = 190;
        String method = getSubmethod("status_replies");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            statusReplies = response.get("result").getAsBoolean();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return statusReplies;
    }

    // 8191
    public void setStatusReplies(boolean enable) {
        JsonObject params = new JsonObject();
        int id = 191;
        String method = getSubmethod("status_replies", "set");
        params.addProperty("enable", enable);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.statusReplies = response.get("result").getAsBoolean();
            if (this.statusReplies != enable) System.err.println("Failed setting status replies");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }

    // 8200 - Entity Broadcast Range
    public int getEntityBroadcastRange() {
        int id = 200;
        String method = getSubmethod("entity_broadcast_range");
        CompletableFuture<JsonObject> future = sendMessage(id, method);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            entityBroadcastRange = response.get("result").getAsInt();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
        return entityBroadcastRange;
    }

    // 8201
    public void setEntityBroadcastRange(int percentage_points) {
        JsonObject params = new JsonObject();
        int id = 201;
        String method = getSubmethod("entity_broadcast_range", "set");
        params.addProperty("percentage_points", percentage_points);
        CompletableFuture<JsonObject> future = sendMessage(id, method, params);
        try {
            JsonObject response = future.get(timeout, TimeUnit.SECONDS);
            this.entityBroadcastRange = response.get("result").getAsInt();
            if (this.entityBroadcastRange != percentage_points) System.err.println("Failed setting entity broadcast range");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error while waiting for response: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("Timed out waiting for response with ID: " + (idBase + id));
        } finally {
            pendingRequests.remove(id);
        }
    }


    // --- Internal Methods ---

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

        // No notifications defined for serversettings
        System.out.println(object);
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
}