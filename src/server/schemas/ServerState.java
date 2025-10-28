package server.schemas;

import java.util.Arrays;

public class ServerState {
    public final Player[] players;
    public final boolean started;
    public final Version version;

    public ServerState(Player[] players, boolean started, Version version) {
        this.players = players;
        this.started = started;
        this.version = version;
    }

    @Override
    public String toString() {
        return "ServerState{" +
                "players=" + Arrays.toString(players) +
                ", started=" + started +
                ", version=" + version +
                '}';
    }
}
