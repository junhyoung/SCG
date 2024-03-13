package com.example.service1.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Getter
@Setter
@RefreshScope
@Service
@ConfigurationProperties("com.jiseong")
public class Service1Config {

    private String configTest;
    private String commonTest;

}
