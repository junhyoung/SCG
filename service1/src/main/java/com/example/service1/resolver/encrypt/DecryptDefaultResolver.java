package com.example.service1.resolver.encrypt;

import com.example.service1.resolver.Resolver;
import com.example.service1.util.SecurityUtil;
import org.springframework.stereotype.Component;

@Component("DecryptDefaultResolver")
public class DecryptDefaultResolver extends Resolver {


    @Override
    public Object resolve(Object value) {
        return null;
    }

    @Override
    public Object resolve(Object input, Object secureKey) {

        return SecurityUtil.decrypt((String)input, (String)secureKey);
    }
}
