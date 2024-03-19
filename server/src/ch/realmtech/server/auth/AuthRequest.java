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
    public AuthRequest(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public String verifyAccessToken(String username) throws IOException, InterruptedException, FailedRequest {
        VerifyTokenOptionEntry verifyToken = RegistryUtils.findEntry(serverContext.getRootRegistry(), VerifyTokenOptionEntry.class)
                .orElseThrow(() -> new RuntimeException(VerifyTokenOptionEntry.class + " not found in root registry"));

        AuthServerBaseUrlServerOptionEntry authServerBaseUrlServerOptionEntry = RegistryUtils.findEntry(serverContext.getRootRegistry(), AuthServerBaseUrlServerOptionEntry.class)
                .orElseThrow(() -> new RuntimeException(AuthServerBaseUrlServerOptionEntry.class + " not found in root registry"));

        VerifyAccessTokenUrnOptionEntry verifyAccessTokenUrnOptionEntry = RegistryUtils.findEntry(serverContext.getRootRegistry(), VerifyAccessTokenUrnOptionEntry.class)
                .orElseThrow(() -> new RuntimeException(VerifyAccessTokenUrnOptionEntry.class + " not found in root registry"));

        if (!verifyToken.getValue()) return UUID.randomUUID().toString();
        HttpRequest verifyCodeRequest = HttpRequest.newBuilder()
                .uri(URI.create(authServerBaseUrlServerOptionEntry.getValue() + "/" + verifyAccessTokenUrnOptionEntry))
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
