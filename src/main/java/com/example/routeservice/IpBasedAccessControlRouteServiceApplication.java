package com.example.routeservice;

import com.example.routeservice.control.AccessControlList;
import com.example.routeservice.control.InMemoryIpBasedAccessControl;
import com.example.routeservice.control.IpBasedAccessControl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IpBasedAccessControlRouteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpBasedAccessControlRouteServiceApplication.class, args);
    }

    @Bean
    public IpBasedAccessControl ipBasedAccessControl(AccessControlList acl) {
        return new InMemoryIpBasedAccessControl(acl.getCidr());
    }
}
