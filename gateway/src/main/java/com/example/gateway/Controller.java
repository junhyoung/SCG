package com.example.gateway;

import com.example.gateway.entity.Test;
import com.example.gateway.entity.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private TestService testService;

    /*
        Redis 캐시 무효화 테스트용 RDB insert
     */
    @PostMapping("/insert")
    public Test insert(@RequestBody Test test) {
        testService.insert(test.getClientId(), test.getUri());
        return test;
    }

}
