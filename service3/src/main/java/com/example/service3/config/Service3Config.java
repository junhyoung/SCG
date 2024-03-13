package com.example.service3.config;

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
public class Service3Config {

    private String configTest;

}
