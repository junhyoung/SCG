package com.example.service1.resolver.encrypt;


import com.example.service1.resolver.Resolver;
import com.example.service1.util.SecurityUtil;
import org.springframework.stereotype.Component;

@Component("EncryptDefaultResolver")
public class EncryptDefaultResolver extends Resolver {

    @Override
    public Object resolve(Object value) {
        return null;
    }

    @Override
    public Object resolve(Object input, Object secureKey) {

        return SecurityUtil.encrypt((String)input, (String)secureKey);
    }
}

