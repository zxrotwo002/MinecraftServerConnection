package server.schemas;

import java.util.Objects;

public class IPBan {
    public final String ip;
    public final String reason;
    public final String source;
    public final String expires;

    public IPBan(String ip, String reason, String source, String expires) {
        this.ip = ip;
        this.reason = reason;
        this.source = source;
        this.expires = expires;
    }

    @Override
    public String toString() {
        return ip + ": " + reason + " by " + source;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IPBan ipBan = (IPBan) o;
        return Objects.equals(ip, ipBan.ip) && Objects.equals(reason, ipBan.reason) && Objects.equals(source, ipBan.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, reason, source);
    }
}
