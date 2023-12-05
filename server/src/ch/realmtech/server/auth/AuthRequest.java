package ch.realmtech.server.auth;

import ch.realmtech.server.ServerContext;

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
        if (!serverContext.getOptionServer().verifyAccessToken.get()) return UUID.randomUUID().toString();
        HttpRequest verifyCodeRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverContext.getOptionServer().getAuthServerBaseUrl() + "/" + serverContext.getOptionServer().getVerifyAccessTokenUrn()))
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
