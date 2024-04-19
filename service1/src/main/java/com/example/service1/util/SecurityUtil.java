package com.example.service1.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityUtil {

    public static String getEncryptKey(String clientSecret, String timestamp) {
        log.info("Encrypt KEY > {}", timestamp + clientSecret);
        return timestamp + clientSecret;
    }
}
