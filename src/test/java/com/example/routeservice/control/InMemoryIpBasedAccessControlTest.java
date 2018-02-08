package com.example.routeservice.control;

import com.example.routeservice.control.InMemoryIpBasedAccessControl;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryIpBasedAccessControlTest {

    @Test
    public void isAllowed() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("foo", Arrays.asList("192.168.0.0/16"));
        InMemoryIpBasedAccessControl accessControl = new InMemoryIpBasedAccessControl(map);

        assertThat(accessControl.isAllowed("foo", "192.168.2.1")).isTrue();
        assertThat(accessControl.isAllowed("foo", "192.168.0.0")).isTrue();
        assertThat(accessControl.isAllowed("foo", "192.168.255.255")).isTrue();
        assertThat(accessControl.isAllowed("foo", "192.169.2.1")).isFalse();
        assertThat(accessControl.isAllowed("bar", "192.168.2.1")).isFalse();
    }

    @Test
    public void isAllowedMultiple() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("foo", Arrays.asList("192.168.0.0/16", "192.169.0.0/16"));
        InMemoryIpBasedAccessControl accessControl = new InMemoryIpBasedAccessControl(map);

        assertThat(accessControl.isAllowed("foo", "192.168.2.1")).isTrue();
        assertThat(accessControl.isAllowed("foo", "192.168.0.0")).isTrue();
        assertThat(accessControl.isAllowed("foo", "192.168.255.255")).isTrue();
        assertThat(accessControl.isAllowed("foo", "192.169.2.1")).isTrue();
        assertThat(accessControl.isAllowed("bar", "192.168.2.1")).isFalse();
    }
}