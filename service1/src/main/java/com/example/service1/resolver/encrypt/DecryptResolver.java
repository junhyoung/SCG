package com.example.service1.resolver.encrypt;

import com.example.service1.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Component("DecryptResolver")
@Slf4j
public class DecryptResolver extends Resolver {

    @Override
    public Object resolve(Object value) {
        return null;
    }

    @Override
    public Object resolve(Object input, Object secureKey) {
        return decrypt((String)input, (String)secureKey);
    }

    public String decrypt(String encStr, String secureKey) {
        try {

            // SecureKey : (Timestamp + Client_Secret) 32자리
            if (secureKey.length() < 32) {
                secureKey = String.format("%32s", secureKey, 32, "0");
            } else {
                secureKey = secureKey.substring(0, 32);
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ByteBuffer buffer = ByteBuffer.wrap(Base64.decode(encStr));
            byte[] saltBytes = new byte[20];
            buffer.get(saltBytes, 0, saltBytes.length);
            byte[] ivBytes = new byte[cipher.getBlockSize()];
            buffer.get(ivBytes, 0, ivBytes.length);
            byte[] encryoptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes.length];
            buffer.get(encryoptedTextBytes);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(secureKey.toCharArray(), saltBytes, 70000, 256);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher.init(2, secret, new IvParameterSpec(ivBytes));
            byte[] decryptedTextBytes = cipher.doFinal(encryoptedTextBytes);
            return new String(decryptedTextBytes, "UTF-8");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeySpecException |
                 BadPaddingException | InvalidKeyException | BufferUnderflowException | DecoderException e) {
            log.error(e.getMessage());
            return encStr;
        }
    }
}

