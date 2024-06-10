package ch.realmtech.server.netty;

import ch.realmtech.server.level.worldGeneration.SeedGenerator;
import ch.realmtech.server.packet.clientPacket.ClientExecute;
import ch.realmtech.server.registry.Registry;

import java.util.Random;

public final class ConnexionConfig {
    private final String host;
    private final int port;
    private final String saveName;
    private final Boolean notVerifyAccessToken;
    private final long seed;
    private final String rootPath;
    private final ClientExecute clientExecute;
    private final Registry<?> rootRegistry;

    private ConnexionConfig(String host, int port, String saveName, Boolean notVerifyAccessToken, long seed, String rootPath, ClientExecute clientExecute, Registry<?> rootRegistry) {
        this.host = host;
        this.port = port;
        this.saveName = saveName;
        this.notVerifyAccessToken = notVerifyAccessToken;
        this.seed = seed;
        this.rootPath = rootPath;
        this.clientExecute = clientExecute;
        this.rootRegistry = rootRegistry;
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

    public Boolean isNotVerifyAccessToken() {
        return notVerifyAccessToken;
    }

    public long getSeed() {
        return seed;
    }

    public String getRootPath() {
        return rootPath;
    }

    public ClientExecute getClientExecute() {
        return clientExecute;
    }

    public Registry<?> getRootRegistry() {
        return rootRegistry;
    }

    public static ConnexionConfigBuilder builder() {
        return new ConnexionConfigBuilder();
    }

    public static class ConnexionConfigBuilder {
        private String host = "localhost";
        private int port = ServerNetty.PREFERRED_PORT;
        private String saveName = "default";
        private Boolean notVerifyAccessToken = null;
        private long seed = SeedGenerator.randomSeed();
        private String rootPath = "";
        private ClientExecute clientExecute = null;
        private Registry<?> rootRegistry = null;

        public ConnexionConfigBuilder setHost(String host) {
            this.host = host;
            return this;
        }

        public ConnexionConfigBuilder setPort(int port, boolean needPortAvailable) throws IllegalArgumentException {
            if (!(port > 1024 && port < 65565))
                throw new IllegalArgumentException("The port number must be > 1024 and < 65565. It was : " + port);

            int availablePort = port;
            byte limite = 0;
            while (needPortAvailable && !ServerNetty.isPortAvailable(availablePort) && ++limite < 10) {
                availablePort = new Random().nextInt(1024, 65565);
            }

            this.port = availablePort;
            return this;
        }

        public ConnexionConfigBuilder setSaveName(String saveName) {
            this.saveName = saveName;
            return this;
        }

        public ConnexionConfigBuilder setNotVerifyAccessToken(Boolean notVerifyAccessToken) {
            this.notVerifyAccessToken = notVerifyAccessToken;
            return this;
        }

        public ConnexionConfigBuilder setSeed(long seed) {
            this.seed = seed;
            return this;
        }

        public ConnexionConfigBuilder setRootPath(String rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public ConnexionConfigBuilder setClientExecute(ClientExecute clientExecute) {
            this.clientExecute = clientExecute;
            return this;
        }

        public ConnexionConfigBuilder setRootRegistry(Registry<?> rootRegistry) {
            this.rootRegistry = rootRegistry;
            return this;
        }

        public ConnexionConfig build() {
            return new ConnexionConfig(host, port, saveName, notVerifyAccessToken, seed, rootPath, clientExecute, rootRegistry);
        }
    }
}
