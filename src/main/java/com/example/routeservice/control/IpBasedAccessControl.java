package com.example.routeservice.control;

public interface IpBasedAccessControl {
    boolean isAllowed(String target, String address);
}
