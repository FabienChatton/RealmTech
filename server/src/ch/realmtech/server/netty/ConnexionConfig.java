package ch.realmtech.server.netty;

import ch.realmtech.server.level.worldGeneration.SeedGenerator;

import java.util.Random;

public final class ConnexionConfig {
    private final String host;
    private final int port;
    private final String saveName;
    private final boolean verifyAccessToken;
    private final long seed;

    private ConnexionConfig(String host, int port, String saveName, boolean verifyAccessToken, long seed) {
        this.host = host;
        this.port = port;
        this.saveName = saveName;
        this.verifyAccessToken = verifyAccessToken;
        this.seed = seed;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getSaveName() {
        return saveName;
    }

    public boolean isVerifyAccessToken() {
        return verifyAccessToken;
    }

    public long getSeed() {
        return seed;
    }

    public static ConnexionConfigBuilder builder() {
        return new ConnexionConfigBuilder();
    }

    public static class ConnexionConfigBuilder {
        private String host = "localhost";
        private int port = ServerNetty.PREFERRED_PORT;
        private String saveName = "default";
        private boolean verifyAccessToken = true;
        private long seed = SeedGenerator.randomSeed();

        public ConnexionConfigBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public ConnexionConfigBuilder setPort(int port) throws IllegalArgumentException {
            if (!(port > 1024 && port < 65565))
                throw new IllegalArgumentException("The port number must be > 1024 and < 65565. It was : " + port);

            int availablePort = port;
            byte limite = 0;
            while (!ServerNetty.isPortAvailable(availablePort) || ++limite < 10) {
                availablePort = new Random().nextInt(1024, 65565);
            }

            this.port = availablePort;
            return this;
        }

        public ConnexionConfigBuilder setSaveName(String saveName) {
            this.saveName = saveName;
            return this;
        }

        public ConnexionConfigBuilder setVerifyAccessToken(boolean verifyAccessToken) {
            this.verifyAccessToken = verifyAccessToken;
            return this;
        }

        public ConnexionConfigBuilder setSeed(long seed) {
            this.seed = seed;
            return this;
        }

        public ConnexionConfig build() {
            return new ConnexionConfig(host, port, saveName, verifyAccessToken, seed);
        }
    }
}
