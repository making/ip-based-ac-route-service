package com.example.routeservice.control;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "acl")
public class AccessControlList {
    private Map<String, List<String>> cidr = new HashMap<>();

    public Map<String, List<String>> getCidr() {
        return cidr;
    }

    public void setCidr(Map<String, List<String>> cidr) {
        this.cidr = cidr;
    }
}
