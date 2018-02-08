package com.example.routeservice.control;

import org.apache.commons.net.util.SubnetUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIpBasedAccessControl implements IpBasedAccessControl {
    private final ConcurrentHashMap<String, List<String>> cidrMap = new ConcurrentHashMap<>();

    public InMemoryIpBasedAccessControl(Map<String, List<String>> cidrMap) {
        Assert.notNull(cidrMap, "cidrMap must not be null");
        this.cidrMap.putAll(cidrMap);
    }

    @Override
    public boolean isAllowed(String target, String address) {
        List<String> subnetInfos = this.cidrMap.get(target);
        if (subnetInfos == null) {
            return false;
        }
        return subnetInfos.stream()
                .map(cidr -> {
                    SubnetUtils utils = new SubnetUtils(cidr);
                    utils.setInclusiveHostCount(true);
                    return utils.getInfo();
                })
                .anyMatch(subnetInfo -> subnetInfo.isInRange(address));
    }

    public void addedAllowedCidrs(String target, List<String> cidrs) {
        this.cidrMap.put(target, cidrs);
    }

    public List<String> getAllowedCidrs(String target) {
        return this.cidrMap.getOrDefault(target, new ArrayList<>());
    }

    public Map<String, List<String>> getAllAllowedCidrs() {
        return Collections.unmodifiableMap(this.cidrMap);
    }

    public void deleteAllowedCidr(String target) {
        this.cidrMap.remove(target);
    }
}
