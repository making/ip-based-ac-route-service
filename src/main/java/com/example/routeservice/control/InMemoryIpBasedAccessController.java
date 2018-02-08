package com.example.routeservice.control;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class InMemoryIpBasedAccessController {
    private final InMemoryIpBasedAccessControl accessControl;

    public InMemoryIpBasedAccessController(InMemoryIpBasedAccessControl accessControl) {
        this.accessControl = accessControl;
    }

    @GetMapping("acl")
    public Map<String, List<String>> getAcls() {
        return this.accessControl.getAllAllowedCidrs();
    }

    @GetMapping("acl/{target}")
    public List<String> getAcl(@PathVariable String target) {
        return this.accessControl.getAllowedCidrs(target);
    }

    @PutMapping("acl/{target}")
    public List<String> putAcl(@PathVariable String target, @RequestBody List<String> ciders) {
        this.accessControl.addedAllowedCidrs(target, ciders);
        return ciders;
    }

    @DeleteMapping("acl/{target}")
    public void deleteAcl(@PathVariable String target) {
        this.accessControl.deleteAllowedCidr(target);
    }

}
