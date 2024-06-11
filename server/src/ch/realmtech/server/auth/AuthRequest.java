package ch.realmtech.server.auth;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.mod.options.server.AuthServerBaseUrlServerOptionEntry;
import ch.realmtech.server.mod.options.server.VerifyAccessTokenUrnOptionEntry;
import ch.realmtech.server.mod.options.server.VerifyTokenOptionEntry;
import ch.realmtech.server.registry.RegistryUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class AuthRequest {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ServerContext serverContext;
    private final VerifyTokenOptionEntry verifyToken;
    private final AuthServerBaseUrlServerOptionEntry authServerBaseUrlServerOptionEntry;
    private final VerifyAccessTokenUrnOptionEntry verifyAccessTokenUrnOptionEntry;

    public AuthRequest(ServerContext serverContext) {
        this.serverContext = serverContext;
        verifyToken = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), VerifyTokenOptionEntry.class);
        authServerBaseUrlServerOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), AuthServerBaseUrlServerOptionEntry.class);
        verifyAccessTokenUrnOptionEntry = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), VerifyAccessTokenUrnOptionEntry.class);
    }

    /**
     * Verify access token of this username.
     * <a href="https://github.com/FabienChatton/RealmTech-auth?tab=readme-ov-file#diagramme-pour-v%C3%A9rification-de-lauthenticit%C3%A9">
     * See digramme for auth protocole</a>
     *
     * @param username The username to verify the token
     * @return The String UUID of the authenticated player
     * @throws FailedRequest        If the username fail to authenticate
     * @throws IOException          Fail to send the request
     * @throws InterruptedException Interrupted during the request send
     */
    public String verifyAccessToken(String username) throws IOException, InterruptedException, FailedRequest {
        if (!verifyToken.getValue()) return UUID.randomUUID().toString();
        HttpRequest verifyCodeRequest = HttpRequest.newBuilder()
                .uri(URI.create(authServerBaseUrlServerOptionEntry.getValue() + "/" + verifyAccessTokenUrnOptionEntry.getValue()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "username" + "=" + username
                ))
                .build();
        HttpResponse<String> response = httpClient.send(verifyCodeRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new FailedRequest(response.statusCode(), response.body());
        return response.body();
    }
}
