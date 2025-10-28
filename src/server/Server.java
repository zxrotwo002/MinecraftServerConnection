package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import server.methods.*;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class Server implements WebsocketClientEndpoint.MessageHandler {
    public Gson gson = new Gson();
    private WebsocketClientEndpoint endpoint;
    public Allowlist allowlist;
    public Bans bans;
    public Gamerules gamerules;
    public IPBans ipBans;
    public Operators operators;
    public Players players;
    public server.methods.Server server;
    public ServerSettings serverSettings;

    private volatile boolean isReconnecting = false;
    private static volatile boolean isShowingPopup = false;


    public Server() {
        connect();
        allowlist = new Allowlist(this);
        bans = new Bans(this);
        gamerules = new Gamerules(this);
        ipBans = new IPBans(this);
        operators = new Operators(this);
        players = new Players(this);
        server = new server.methods.Server(this);
        serverSettings = new ServerSettings(this);
    }

    /**
     * Sends a message, automatically handling reconnection if the connection is closed.
     * @param s The JSON string message to send.
     */
    public void sendMessage(String s) {
        try {
            if (endpoint == null && !isReconnecting) {
                System.err.println("Endpoint is null. Triggering reconnect...");
                handleReconnect();
                return;
            } else if (isReconnecting) {
                System.err.println("Reconnect in progress. Message skipped.");
                return;
            }

            endpoint.sendMessage(s);

        } catch (IllegalStateException e) {
            System.err.println("Connection lost (session closed): " + e.getMessage());
            if (!isReconnecting) {
                handleReconnect();
            }

        } catch (Exception e) {
            System.err.println("An unexpected error occurred during sendMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Synchronized method to ensure only one thread can trigger a reconnect.
     * Now includes a 10-second timeout to reset the lock if connection fails.
     */
    private synchronized void handleReconnect() {
        if (isReconnecting) {
            return;
        }
        isReconnecting = true;
        new Thread(() -> {
            try {
                Thread.sleep(10000); // 10-second timeout
            } catch (InterruptedException e) {
            }

            if (isReconnecting) {
                System.err.println("Reconnect attempt timed out (10s). Resetting lock.");
                isReconnecting = false;
            }
        }).start();
        System.err.println("Attempting to reconnect...");

        connect();

        if (endpoint == null) {
            System.err.println("Reconnect failed (could not create endpoint).");
            isReconnecting = false;
            return;
        }


        if (!isShowingPopup) {
            isShowingPopup = true;
            System.err.println("Reconnect initiated. User must retry their action.");
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(Main.getInstance(),
                        "Connection was lost and a reconnect was initiated.\nPlease try your action again.",
                        "Connection Lost",
                        JOptionPane.WARNING_MESSAGE);
                isShowingPopup = false;
            });
        }
    }


    private void connect() {
        WebsocketClientEndpoint clientEndPoint = null;
        try {
            clientEndPoint = new WebsocketClientEndpoint(new URI(Main.URL), Main.SECRET);
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        endpoint = clientEndPoint;
        if (clientEndPoint == null) return;
        clientEndPoint.setMessageHandler(this);
    }

    @Override
    public void handleMessage(String s) {
        if (isReconnecting) {
            isReconnecting = false;
            System.out.println("Connection re-established successfully.");
        }

        JsonObject object = gson.fromJson(s, JsonObject.class);
        JsonElement id = object.get("id");
        if (id == null) {
            if (Main.getInstance() != null) Main.getInstance().refreshLists();
            switch (object.get("method").getAsString()) {
                case "minecraft:notification/server/status" -> {
                    object.addProperty("id",7011);
                    server.handleMessage(object);
                }
                default -> {
                    System.out.print("Notification: ");
                    System.out.println(s);
                }
            }
            return;
        }
        int id1 = id.getAsInt();
        if (id1 < 1000) {
            //0-999 gets handled here
            System.out.println(s);
            return;
        }
        if (id1 < 2000) {
            //1000-1999 Allowlist
            allowlist.handleMessage(object);
            return;
        }
        if (id1 < 3000) {
            //2000-2999 Bans
            bans.handleMessage(object);
        }
        if (id1 < 4000) {
            //3000-3999 Gamerules
            gamerules.handleMessage(object);
            return;
        }
        if (id1 < 5000) {
            //4000-4999 IP Bans
            ipBans.handleMessage(object);
            return;
        }
        if (id1 < 6000) {
            //5000-5999 Operators
            operators.handleMessage(object);
            return;
        }
        if (id1 < 7000) {
            //6000-6999 Players
            players.handleMessage(object);
            return;
        }
        if (id1 < 8000) {
            //7000-7999 Server
            server.handleMessage(object);
            return;
        }
        if (id1 < 9000) {
            //8000-8999 Server Settings
            serverSettings.handleMessage(object);
            return;
        }
        System.out.println("Unknown ID");
        System.out.println(s);
    }
}