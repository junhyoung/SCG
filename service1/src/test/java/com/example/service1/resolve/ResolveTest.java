package com.example.service1.resolve;

import com.example.service1.resolver.Resolver;
import com.example.service1.resolver.ResolverFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ResolveTest {

    @Autowired
    ResolverFactory resolverFactory;

    @Test
    public void resolveTest() {

        String type = "decryptResolver";

        Resolver resolver = resolverFactory.getResolver(type);

        String result = resolver.resolve("test");

        System.out.println("RES >> " + result);

    }

}
