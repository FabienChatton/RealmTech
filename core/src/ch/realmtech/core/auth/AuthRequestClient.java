package ch.realmtech.core.auth;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.auth.FailedRequest;
import ch.realmtech.server.mod.options.client.AuthServerBaseUrlClientOptionEntry;
import ch.realmtech.server.mod.options.client.CreateAccessTokenUrnOptionEntry;
import ch.realmtech.server.mod.options.client.VerifyLoginUrn;
import ch.realmtech.server.registry.RegistryUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthRequestClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final AuthServerBaseUrlClientOptionEntry authServerBaseUrl;
    private final CreateAccessTokenUrnOptionEntry createAccessTokenUrn;
    private final VerifyLoginUrn verifyLoginUrn;
    public AuthRequestClient(RealmTech context) {
        authServerBaseUrl = RegistryUtils.findEntryOrThrow(context.getRootRegistry(), AuthServerBaseUrlClientOptionEntry.class);
        createAccessTokenUrn = RegistryUtils.findEntryOrThrow(context.getRootRegistry(), CreateAccessTokenUrnOptionEntry.class);
        verifyLoginUrn = RegistryUtils.findEntryOrThrow(context.getRootRegistry(), VerifyLoginUrn.class);
    }

    public void createAccessToken(String username, String password) throws IOException, InterruptedException, FailedRequest {
        HttpRequest hashPasswordRequest = HttpRequest.newBuilder()
                .uri(URI.create(authServerBaseUrl.getValue() + "/" + createAccessTokenUrn.getValue()))
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
                .uri(URI.create(authServerBaseUrl.getValue() + "/" + verifyLoginUrn.getValue()))
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
