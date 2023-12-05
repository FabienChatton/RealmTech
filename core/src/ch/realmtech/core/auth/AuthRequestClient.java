package ch.realmtech.core.auth;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.auth.FailedRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthRequestClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final RealmTech context;
    public AuthRequestClient(RealmTech context) {
        this.context = context;
    }

    public void createAccessToken(String username, String password) throws IOException, InterruptedException, FailedRequest {
        HttpRequest hashPasswordRequest = HttpRequest.newBuilder()
                .uri(URI.create(context.getOption().getAuthServerBaseUrl() + "/" + context.getOption().getCreateAccessTokenUrn()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "password" + "=" + password + "&" +
                                "username" + "=" + username
                ))
                .build();
        HttpResponse<String> response = httpClient.send(hashPasswordRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new FailedRequest(response.statusCode(), response.body());
    }

    public void verifyLogin(String username, String password) throws FailedRequest, IOException, InterruptedException {
        HttpRequest hashPasswordRequest = HttpRequest.newBuilder()
                .uri(URI.create(context.getOption().getAuthServerBaseUrl() + "/" + context.getOption().getVerifyLoginUrn()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "password" + "=" + password + "&" +
                                "username" + "=" + username
                ))
                .build();
        HttpResponse<String> response = httpClient.send(hashPasswordRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new FailedRequest(response.statusCode(), response.body());
    }
}
