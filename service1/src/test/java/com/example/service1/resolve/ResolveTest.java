package com.example.service1.resolve;

import com.example.service1.resolver.DecryptResolver;
import com.example.service1.resolver.EncryptResolver;
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
    public void 암복호화_ResolverTest() {

        String decType = "decryptResolver";
        String encType = "encryptResolver";
        String plainStr = "이지성";

        Resolver resolver = resolverFactory.getResolver(encType);

        String key = "암호화 키";

        String encResult = (String) resolver.resolve(plainStr, key);

        System.out.println("ENC RES >> " + encResult);

        resolver = resolverFactory.getResolver(decType);

        String decResult = (String) resolver.resolve(encResult, key);

        System.out.println("DEC RES >> " + decResult);


    }

    @Test
    void 암호화() {
        EncryptResolver resolver = new EncryptResolver();

        String plainStr = "이지성";
        String key = "암호화 키";

        System.out.println(resolver.encrypt(plainStr, key));
    }

    @Test
    void 복호화() {
        DecryptResolver resolver = new DecryptResolver();

        // 1udEv9J3tVDqQqhNZiiwwL8dVVQWTjn+8eX+43itBfaGYtTHvpBQ6+0oWFM3B/Sr2MjUQA==
        // 8YgVAqotIxCHFS3vZDc5RcENlkx2cw/+W+kjLZn97gdLk3SVmtKo5EnnTYolNVgYXHABSg==

        String encStr = "IkK69Zh/chevTuLuVKVu1b+6o5Oq0qse7+txsHmbU2uYEuneUB+l2B7f4C71fgaF/53z8w==";
        String key = "암호화 키";

        System.out.println(resolver.decrypt(encStr, key));
    }



}
