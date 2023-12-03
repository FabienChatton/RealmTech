package ch.realmtech.core.option;

import com.badlogic.gdx.Gdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

public class PersisteCredential {
    private final static Logger logger = LoggerFactory.getLogger(PersisteCredential.class);
    private final static String algorithm = "AES/CBC/PKCS5Padding";
    private static File realmTechExternalFs = null;
    private static File realmTechCredentialsFiles = null;
    private static File realmTechCredentialsKey = null;
    private static IvParameterSpec iv = new IvParameterSpec(new byte[]{9, 6, 0, 6, 1, 3, 7, 2, 1, 0, 3, 6, 8, 7, 9, 1 });

    static {
        try {
            realmTechExternalFs = Gdx.files.external("RealmTech-externalFs").file();
            if (!realmTechExternalFs.exists()) {
                Files.createDirectories(realmTechExternalFs.toPath());
            }
            realmTechCredentialsFiles = new File(realmTechExternalFs.toURI().resolve("RealmTechCredentials"));
            realmTechCredentialsKey = new File(realmTechExternalFs.toURI().resolve("RealmTechCredentialsKey"));
            if (!realmTechCredentialsFiles.exists()) {
                Files.createFile(realmTechCredentialsFiles.toPath());
            }
            if (!realmTechCredentialsKey.exists()) {
                Files.createFile(realmTechCredentialsKey.toPath());
            }
        } catch (Exception e) {
            logger.error("Can not crate RealmTech-externalFs");
        }
    }
    public static void persisteCredential(String username, String password) {
        try {
            String credentials = username + "\0" + password;

            SecretKey key = generateKey(256);

            String cipherText = encrypt(algorithm, credentials, key, iv);

            try (FileOutputStream fos = new FileOutputStream(realmTechCredentialsFiles))  {
                fos.write(cipherText.getBytes(StandardCharsets.UTF_8));
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(realmTechCredentialsKey))) {
                oos.writeObject(key);
            }

        } catch (Exception e) {
            logger.info("Can not save credentials. {}", e.getMessage());
        }
    }

    public static Optional<String[]> loadCredential() {
        try {
            SecretKey key;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(realmTechCredentialsKey))) {
                key = (SecretKey) ois.readObject();
            }

            String cipherText;
            try (FileInputStream fis = new FileInputStream(realmTechCredentialsFiles)) {
                cipherText = new String(fis.readAllBytes());
            }

            String plainText = decrypt(algorithm, cipherText, key, iv);
            String[] split = plainText.split("\0");
            String username = split[0];
            String password = split[1];
            return Optional.of(new String[]{username, password});
        } catch (Exception e) {
            logger.info("Can not load credentials. {}" , e.getMessage());
            return Optional.empty();
        }

    }

    private static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

    private static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }
}
