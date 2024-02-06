package ch.realmtech.core.auth;

import ch.realmtech.core.game.netty.RealmTechClientConnexionHandler;
import ch.realmtech.server.packet.serverPacket.DemandeDeConnexionJoueurPacket;

public class AuthControllerClient {
    private final AuthRequestClient authRequestClient;
    private String username;
    private String password;

    public AuthControllerClient(AuthRequestClient authRequestClient) {
        this.authRequestClient = authRequestClient;
    }

    public void verifyLogin(String username, String password) throws Exception {
        authRequestClient.verifyLogin(username, password);
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void sendAuthAndJoinServer(RealmTechClientConnexionHandler clientConnexionHandler, boolean createAccessToken) throws Exception {
        if (createAccessToken) {
            authRequestClient.createAccessToken(username, password);
        }
        clientConnexionHandler.sendAndFlushPacketToServer(new DemandeDeConnexionJoueurPacket(username));
    }
}
