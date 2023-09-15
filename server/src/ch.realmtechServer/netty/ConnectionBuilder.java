package ch.realmtechServer.netty;

public final class ConnectionBuilder {
    private String host = "localhost";
    private int port = ServerNetty.PREFERRED_PORT;
    private String saveName;

    /**
     * default "localhost"
     */
    public ConnectionBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public String getHost() {
        return host;
    }

    /**
     * default 25533.
     * La valeur doit être > 1024 et < 65565
     */
    public ConnectionBuilder setPort(int port) throws IllegalArgumentException {
        if (!(port > 1024 && port < 65565))
            throw new IllegalArgumentException("Le port doit être plus > 1024 et 65565. Il était de : " + port);
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }

    public String getSaveName() {
        return saveName;
    }

    public ConnectionBuilder setSaveName(String saveName) {
        this.saveName = saveName;
        return this;
    }
}
