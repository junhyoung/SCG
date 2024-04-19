package com.example.service1.apis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiContext {

    private String clientId;
    private String clientSecret;
    private String clientIp;
    private String uri;
    private String timestamp;
    private String itfId;

    private String txApiTranId;
    private String txApiType;
    private String txOrgIp;
    private String txOrgCd;
    private String txScope;
    private String txToken;
}
