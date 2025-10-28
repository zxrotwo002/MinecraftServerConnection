package server.schemas;

import java.util.Objects;

public class UserBan {
    public final String reason;
    public final String expires;
    public final String source;
    public final Player player;

    public UserBan(String reason, String expires, String source, Player player) {
        this.reason = reason;
        this.expires = expires;
        this.source = source;
        this.player = player;
    }

    @Override
    public String toString() {
        return player + ": " + reason + " by " + source;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserBan userBan = (UserBan) o;
        return Objects.equals(reason, userBan.reason) && Objects.equals(source, userBan.source) && Objects.equals(player, userBan.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reason, source, player);
    }
}
