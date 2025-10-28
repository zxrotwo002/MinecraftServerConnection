package server;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.websocket.*;
import javax.websocket.MessageHandler;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */

public class WebsocketClientEndpoint extends Endpoint implements MessageHandler.Whole<String> {

    Session userSession = null;
    private MessageHandler messageHandler;

    public WebsocketClientEndpoint(URI endpointURI, String token) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator() {
                @Override
                public void beforeRequest(Map<String, List<String>> headers) {
                    headers.put("Authorization", Collections.singletonList("Bearer " + token));
                }
            };
            ClientEndpointConfig clientConfig = ClientEndpointConfig.Builder.create()
                    .configurator(configurator)
                    .build();
            container.connectToServer(this, clientConfig, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("opening websocket");
        this.userSession = session;
        session.addMessageHandler(this);
    }

    public void sendMessage(String s) {
        userSession.getAsyncRemote().sendText(s);
    }

    @Override
    public void onMessage(String s) {
        messageHandler.handleMessage(s);
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public interface MessageHandler {
        void handleMessage(String s);
    }
}