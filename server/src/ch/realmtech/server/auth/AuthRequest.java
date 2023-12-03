package ch.realmtech.server.auth;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class AuthRequest {
    private final static String BASE_URL = "http://localhost/RealmTech/auth/";
    private final static boolean VERIFY_ACCESS_TOKEN = true;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String createAccessToken(String username, String password) throws IOException, InterruptedException, FailedRequest {
        HttpRequest hashPasswordRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL).resolve("createAccessToken.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "password" + "=" + password + "&" +
                        "username" + "=" + username
                ))
                .build();
        HttpResponse<String> response = httpClient.send(hashPasswordRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new FailedRequest(response.statusCode(), response.body());
        return response.body();
    }

    public String verifyAccessToken(String username) throws IOException, InterruptedException, FailedRequest {
        if (!VERIFY_ACCESS_TOKEN) return UUID.randomUUID().toString();
        HttpRequest verifyCodeRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL).resolve("verifyAccessToken.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "username" + "=" + username
                ))
                .build();
        HttpResponse<String> response = httpClient.send(verifyCodeRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new FailedRequest(response.statusCode(), response.body());
        return response.body();
    }

    public void verifyLogin(String username, String password) throws FailedRequest, IOException, InterruptedException {
        HttpRequest hashPasswordRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL).resolve("verifyPassword.php"))
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
