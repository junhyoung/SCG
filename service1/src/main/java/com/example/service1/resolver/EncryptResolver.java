package com.example.service1.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.BufferUnderflowException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

@Component("encryptResolver")
@Slf4j
public class EncryptResolver extends Resolver {
    @Override
    public Object resolve(Object value) {
        return null;
    }

    @Override
    public Object resolve(Object input, Object secureKey) {
        return encrypt((String)input, (String)secureKey);
    }

    public String encrypt(String plainStr, String secureKey) {
        // SecureKey : (Timestamp + Client_Secret) 32자리

        try {
            if(secureKey.length() < 32) {
                secureKey = String.format("%32s", secureKey, 32, "0");
            }
            else {
                secureKey = secureKey.substring(0, 32);
            }
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            SecretKeyFactory factory = null;
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(secureKey.toCharArray(), bytes, 70000, 256);
            SecretKey secretKey = null;
            secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] ivBytes = new byte[0];
            ivBytes = ((IvParameterSpec)params.getParameterSpec(IvParameterSpec.class)).getIV();
            byte[] encryptedTextBytes = new byte[0];
            encryptedTextBytes = cipher.doFinal(plainStr.getBytes(StandardCharsets.UTF_8));

            byte[] buffer = new byte[bytes.length + ivBytes.length + encryptedTextBytes.length];

            System.arraycopy(bytes, 0, buffer, 0, bytes.length);
            System.arraycopy(ivBytes, 0, buffer, bytes.length, ivBytes.length);
            System.arraycopy(encryptedTextBytes, 0, buffer, bytes.length + ivBytes.length, encryptedTextBytes.length);
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBuffer = encoder.encode(buffer);
            return new String(encodedBuffer);

        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException |
                 NoSuchPaddingException | InvalidParameterSpecException | InvalidKeyException |
                 BufferUnderflowException e) {
            log.error(e.getMessage());
            return plainStr;
        }


    }
}
