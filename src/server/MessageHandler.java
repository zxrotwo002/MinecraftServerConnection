package server;

import com.google.gson.JsonObject;

public interface MessageHandler {
    void handleMessage(JsonObject object);
}
