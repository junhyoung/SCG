package com.example.service1.resolver;

import org.springframework.stereotype.Component;

@Component("encryptResolver")
public class EncryptResolver implements Resolver {
    @Override
    public String resolve(String input) {
        // 암호화 로직 구현
        return "encrypted_" + input;
    }
}
