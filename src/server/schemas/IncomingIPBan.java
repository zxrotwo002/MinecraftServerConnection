package server.schemas;

public class IncomingIPBan {
    public final String ip;
    public final Player player;
    public final String reason;
    public final String source;
    public final String expires;

    public IncomingIPBan(String ip, Player player, String reason, String source, String expires) {
        this.ip = ip;
        this.player = player;
        this.reason = reason;
        this.source = source;
        this.expires = expires;
    }
}
