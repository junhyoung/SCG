package com.example.service1.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ResolverFactory {
    private final Map<String, Resolver> resolverMap;

    @Autowired
    public ResolverFactory(Map<String, Resolver> resolvers) {
        this.resolverMap = resolvers;
    }

    public Resolver getResolver(String key) {
        return resolverMap.get(key);
    }
}

