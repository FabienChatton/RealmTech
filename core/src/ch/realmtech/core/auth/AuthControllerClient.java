package ch.realmtech.core.auth;

import ch.realmtech.core.game.netty.RealmTechClientConnexionHandler;
import ch.realmtech.server.packet.serverPacket.DemandeDeConnexionJoueurPacket;

public class AuthControllerClient {
    private final String UN_REGISTERED_USERNAME = "anonymous";
    private final AuthRequestClient authRequestClient;
    private String username;
    private String password;

    public AuthControllerClient(AuthRequestClient authRequestClient) {
        this.authRequestClient = authRequestClient;
        username = UN_REGISTERED_USERNAME;
    }

    public void verifyLogin(String username, String password) throws Exception {
        authRequestClient.verifyLogin(username, password);
        this.username = username;
        this.password = password;
    }

    public void sendAuthAndJoinServer(RealmTechClientConnexionHandler clientConnexionHandler) throws Exception {
        if (!username.equals(UN_REGISTERED_USERNAME)) {
            authRequestClient.createAccessToken(username, password);
        }
        clientConnexionHandler.sendAndFlushPacketToServer(new DemandeDeConnexionJoueurPacket(username));
    }
}
