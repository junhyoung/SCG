package com.example.service1.resolver;

import org.springframework.stereotype.Component;

@Component("maskingResolverName")
public class MaskingResolverName implements Resolver {
    @Override
    public String resolve(String input) {
        // 마스킹 로직 구현
        return "masked_" + input;
    }
}
