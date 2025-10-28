package server.schemas;

public class UntypedGameRule {
    public final String value;
    public final String key;

    public UntypedGameRule(String key, String value) {
        this.value = value;
        this.key = key;
    }

    @Override
    public String toString() {
        return "UntypedGameRule{" +
                "value='" + value + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
