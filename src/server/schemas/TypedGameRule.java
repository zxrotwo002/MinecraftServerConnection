package server.schemas;

public class TypedGameRule {
    public final String type;
    public final String key;
    public final String value;

    public TypedGameRule(String key, String value, String type) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "TypedGameRule{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
