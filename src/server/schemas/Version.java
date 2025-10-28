package server.schemas;

public class Version {
    public final int protocol;
    public final String name;

    public Version(int protocol, String name) {
        this.protocol = protocol;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Version{" +
                "protocol=" + protocol +
                ", name='" + name + '\'' +
                '}';
    }
}
