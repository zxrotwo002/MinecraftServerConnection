package server.schemas;

public class SystemMessage {
    public final Player[] receivingPlayers;
    public final boolean overlay;
    public final Message message;

    public SystemMessage(Player[] receivingPlayers, boolean overlay, Message message) {
        this.receivingPlayers = receivingPlayers;
        this.overlay = overlay;
        this.message = message;
    }
}
