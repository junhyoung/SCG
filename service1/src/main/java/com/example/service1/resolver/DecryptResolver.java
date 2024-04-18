package com.example.service1.resolver;

import org.springframework.stereotype.Component;

@Component("decryptResolver")
public class DecryptResolver implements Resolver {
    @Override
    public String resolve(String input) {
        // 복호화 로직 구현
        return "decrypted_" + input;
    }
}

