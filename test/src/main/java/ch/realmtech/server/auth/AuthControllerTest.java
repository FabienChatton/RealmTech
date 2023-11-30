package ch.realmtech.server.auth;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerTest {
    private final AuthController authController = new AuthController();
    
    @Test
    void hashPasswordTest() throws IOException, InterruptedException, FailedRequest {
        String hashPassword = authController.hashPassword("123123");
        assertEquals("MoyRTOs8nxA0E", hashPassword);
    }

    @Test
    void generateCodeLocalTest() throws NoSuchAlgorithmException {
        String code = authController.generateCodeLocal("amongus", "MoyRTOs8nxA0E");
        assertEquals("0db9ff8aa84b5d7bc342e5e8321daf5756a7c73cb7a7b6f1846ef868a45fc3dd", code);
    }

    @Test
    void generateCodeAuthTest() throws IOException, FailedRequest, InterruptedException {
        String code = authController.generateCodeAuth("amongus", "MoyRTOs8nxA0E");
        assertEquals("0db9ff8aa84b5d7bc342e5e8321daf5756a7c73cb7a7b6f1846ef868a45fc3dd", code);
    }

    @Test
    void verifyCodeSuccessTest() throws IOException, InterruptedException, FailedRequest {
        boolean valide = authController.verifyCode("amongus", "0db9ff8aa84b5d7bc342e5e8321daf5756a7c73cb7a7b6f1846ef868a45fc3dd");
        assertTrue(valide);
    }

    @Test
    void verifyCodeFalseUsernameTest() {
        assertThrows(FailedRequest.class, () -> authController.verifyCode("amonguse", "0db9ff8aa84b5d7bc342e5e8321daf5756a7c73cb7a7b6f1846ef868a45fc3dd"));
    }

    @Test
    void verifyCodeFalseCodeTest() {
        assertThrows(FailedRequest.class, () -> authController.verifyCode("amongus", "9db9ff8aa84b5d7bc342e5e8321daf5756a7c73cb7a7b6f1846ef868a45fc3dd"));
    }
}
