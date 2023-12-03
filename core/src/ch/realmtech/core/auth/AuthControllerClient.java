package ch.realmtech.core.auth;

import ch.realmtech.core.game.netty.RealmTechClientConnexionHandler;
import ch.realmtech.server.auth.AuthRequest;
import ch.realmtech.server.packet.serverPacket.DemandeDeConnexionJoueurPacket;

public class AuthControllerClient {
    private final String UN_REGISTERED_USERNAME = "anonymous";
    private final AuthRequest authRequest;
    private String username;
    private String password;

    public AuthControllerClient(AuthRequest authRequest) {
        this.authRequest = authRequest;
        username = UN_REGISTERED_USERNAME;
    }

    public void verifyLogin(String username, String password) throws Exception {
        authRequest.verifyLogin(username, password);
        this.username = username;
        this.password = password;
    }

    public void sendAuthAndJoinServer(RealmTechClientConnexionHandler clientConnexionHandler) throws Exception {
        if (!username.equals(UN_REGISTERED_USERNAME)) {
            authRequest.createAccessToken(username, password);
        }
        clientConnexionHandler.sendAndFlushPacketToServer(new DemandeDeConnexionJoueurPacket(username));
    }
}
