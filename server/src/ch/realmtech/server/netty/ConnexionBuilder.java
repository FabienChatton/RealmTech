package ch.realmtech.server.netty;

public final class ConnexionBuilder {
    private String host = "localhost";
    private int port = ServerNetty.PREFERRED_PORT;
    private String saveName = "default";
    private boolean verifyAccessToken = true;

    /**
     * default "localhost"
     */
    public ConnexionBuilder setHost(String host) {
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
    public ConnexionBuilder setPort(int port) throws IllegalArgumentException {
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

    public ConnexionBuilder setSaveName(String saveName) {
        this.saveName = saveName;
        return this;
    }

    public ConnexionBuilder setVerifyAccessToken(boolean verifyAccessToken) {
        this.verifyAccessToken = verifyAccessToken;
        return this;
    }

    public boolean isVerifyAccessToken() {
        return verifyAccessToken;
    }
}
