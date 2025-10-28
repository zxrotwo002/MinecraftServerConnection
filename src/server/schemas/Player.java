package server.schemas;

import java.util.Objects;

public class Player {
    public final String name;
    public final String id;

    public Player(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name) || Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
