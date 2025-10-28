package server.schemas;

import java.util.Objects;

public class Operator {
    public final int permissionLevel;
    public final boolean bypassesPlayerLimit;
    public final Player player;

    public Operator(int permissionLevel, boolean bypassesPlayerLimit, Player player) {
        this.permissionLevel = permissionLevel;
        this.bypassesPlayerLimit = bypassesPlayerLimit;
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Operator operator = (Operator) o;
        return permissionLevel == operator.permissionLevel && bypassesPlayerLimit == operator.bypassesPlayerLimit && Objects.equals(player, operator.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionLevel, bypassesPlayerLimit, player);
    }

    @Override
    public String toString() {
        return player + " - Level " + permissionLevel + (bypassesPlayerLimit ? " - can" : " - can't") + " bypass player limit";
    }
}
