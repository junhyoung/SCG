package com.example.gateway.entity;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TestService {

//    @Autowired
//    private TestRepository testRepository;


//    public List<Test> findByClientId(String clientId) {
//        List<Test> res = testRepository.findByClientId(clientId);
//        log.info("findByClientId > {} ", res.toString());
//        return res;
//    }

    @Autowired
    private TestMapper testMapper;


    @Cacheable(value = "test", key = "#clientId", cacheManager = "contentCacheManager")
    public List<String> findByClientId(String clientId) {
        return testMapper.findByClientId(clientId);
    }

    @CacheEvict(value = "test", key = "#clientId", cacheManager = "contentCacheManager")
    public void insert(String clientId, String uri) {
        log.info("insert info >> clientID: {}, uri: {}", clientId, uri);
        testMapper.insert(clientId, uri);
    }


    public List<String> extractUrisFromTest(List<Test> testList) {

        List<String> res = new ArrayList<>();

        for (Test t: testList) {
            res.add(t.getUri());
        }


        return res;
    }
}