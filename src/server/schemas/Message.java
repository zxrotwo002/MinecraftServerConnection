package server.schemas;

public class Message {
    public final String translatable;
    public final String[] translatableParams;
    public final String literal;

    public Message(String translatable, String[] translatableParams, String literal) {
        this.translatable = translatable;
        this.translatableParams = translatableParams;
        this.literal = literal;
    }
}
