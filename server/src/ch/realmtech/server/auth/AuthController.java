package ch.realmtech.server.auth;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthController {
    private final static String BASE_URL = "http://localhost/RealmTech/auth/";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String hashPassword(String password) throws IOException, InterruptedException, FailedRequest {
        HttpRequest hashPasswordRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL).resolve("hashPassword.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "password" + "=" + password
                ))
                .build();
        HttpResponse<String> response = httpClient.send(hashPasswordRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new FailedRequest(response.statusCode(), response.body());
        return response.body();
    }

    public String generateCodeLocal(String username, String passwordHash) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("sha256");
        byte[] codeBytes = digest.digest((username + passwordHash).getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : codeBytes) {
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    public String generateCodeAuth(String username, String passwordHash) throws IOException, InterruptedException, FailedRequest {
        HttpRequest generateCodeRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL).resolve("generateCode.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                        "username" + "=" + username + "&" +
                        "passwordHash" + "=" + passwordHash
                ))
                .build();
        HttpResponse<String> response = httpClient.send(generateCodeRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new FailedRequest(response.statusCode(), response.body());
        return response.body();
    }

    public boolean verifyCode(String username, String code) throws IOException, InterruptedException, FailedRequest {
        HttpRequest verifyCodeRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL).resolve("verifyCode.php"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "username" + "=" + username + "&" +
                          "code" + "=" + code
                ))
                .build();
        HttpResponse<String> response = httpClient.send(verifyCodeRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new FailedRequest(response.statusCode(), response.body());
        return true;
    }
}
