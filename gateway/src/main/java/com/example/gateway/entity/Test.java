package com.example.gateway.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;


@Data
//@Entity
@ToString
public class Test {

    private String clientId;
    private String uri;

}
