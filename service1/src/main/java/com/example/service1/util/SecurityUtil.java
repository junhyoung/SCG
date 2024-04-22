package com.example.service1.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class SecurityUtil {


    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 32; // 32 bytes = 256 bits
    private static final int IV_SIZE = 16; // 16 bytes = 128 bits

    public static String getEncryptKey(String clientSecret, String timestamp) {
        log.info("Encrypt KEY > {}", timestamp + clientSecret);
        String secureKey = timestamp + clientSecret;
        if(secureKey.length() < 32) {
            secureKey = String.format("%32s", secureKey, 32, "0");
        }
        else {
            secureKey = secureKey.substring(0, 32);
        }
        return secureKey;
    }

    // Helper method to create a secret key spec from a given key string
    private static SecretKeySpec getKeySpec(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length > KEY_SIZE) {
            byte[] trimmedBytes = new byte[KEY_SIZE];
            System.arraycopy(keyBytes, 0, trimmedBytes, 0, KEY_SIZE);
            keyBytes = trimmedBytes;
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Helper method to generate an IV
    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    // Encrypts the given plaintext using AES/CBC/PKCS5Padding
    public static String encrypt(String input, String key) {
        try {
            SecretKeySpec keySpec = getKeySpec(key);
            IvParameterSpec ivParameterSpec = generateIv();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            byte[] ivAndEncrypted = new byte[ivParameterSpec.getIV().length + encrypted.length];
            System.arraycopy(ivParameterSpec.getIV(), 0, ivAndEncrypted, 0, ivParameterSpec.getIV().length);
            System.arraycopy(encrypted, 0, ivAndEncrypted, ivParameterSpec.getIV().length, encrypted.length);
            return Base64.getEncoder().encodeToString(ivAndEncrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String input, String key) {
        try {
            byte[] ivAndEncrypted = Base64.getDecoder().decode(input);
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(ivAndEncrypted, 0, iv, 0, IV_SIZE);
            byte[] encrypted = new byte[ivAndEncrypted.length - IV_SIZE];
            System.arraycopy(ivAndEncrypted, IV_SIZE, encrypted, 0, encrypted.length);

            SecretKeySpec keySpec = SecurityUtil.getKeySpec(key);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }


    public static void main(String[] args) {
        try {
            String clientSecret = "exampleKeyexampleKeyxampleKey";// Long key to demonstrate trimming
            String timestamp = "202404221517234";
            String key = SecurityUtil.getEncryptKey(clientSecret, timestamp);

            String originalText = "Hello, world!";
            String encrypted = encrypt(originalText, key);
            String decrypted = decrypt("N+LjltQXKFs8lKUAUsHrL3dAfEPEh6kt5f3aYpR4lk9CDctmMcRE5dWzD2PG0VMh", "20240419173600000jiseongtestsecret");

            System.out.println("Original: " + originalText);
            System.out.println("Encrypted: " + encrypted);
            System.out.println("Decrypted: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
