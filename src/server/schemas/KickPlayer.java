package server.schemas;

public class KickPlayer {
    public final Player player;
    public final Message message;

    public KickPlayer(Player player, Message message) {
        this.player = player;
        this.message = message;
    }
}
