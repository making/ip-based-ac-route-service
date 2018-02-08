package com.example.routeservice;

import com.example.routeservice.control.IpBasedAccessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;


@RestController
public class RouteServiceController {
    static final String FORWARDED_URL = "X-CF-Forwarded-Url";
    static final String PROXY_METADATA = "X-CF-Proxy-Metadata";
    static final String PROXY_SIGNATURE = "X-CF-Proxy-Signature";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestOperations restOperations;
    private final IpBasedAccessControl accessControl;

    public RouteServiceController(RestOperations restOperations, IpBasedAccessControl accessControl) {
        this.restOperations = restOperations;
        this.accessControl = accessControl;
    }

    @RequestMapping(path = "{target}", headers = {FORWARDED_URL, PROXY_METADATA, PROXY_SIGNATURE})
    ResponseEntity<?> service(@PathVariable String target, RequestEntity<byte[]> incoming, @Value("#{request.remoteAddr}") String remoteAddr) {
        this.logger.info(">> {}: Incoming Request: {}", target, incoming);

        String clientIp = xForwardedFor(incoming.getHeaders()).orElse(remoteAddr);
        if (!this.accessControl.isAllowed(target, clientIp)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", clientIp + " is not allowed to access " + target + "."));
        }

        RequestEntity<?> outgoing = getOutgoingRequest(incoming);
        this.logger.info("<< {}: Outgoing Request: {}", target, outgoing);
        return this.restOperations.exchange(outgoing, byte[].class);
    }

    Optional<String> xForwardedFor(HttpHeaders headers) {
        if (headers.containsKey("X-Forwarded-For")) {
            String values = headers.getFirst("X-Forwarded-For");
            return Optional.of(values.split(",")[0].trim());
        }
        return Optional.empty();
    }

    private static RequestEntity<?> getOutgoingRequest(RequestEntity<?> incoming) {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(incoming.getHeaders());
        URI uri = headers.remove(FORWARDED_URL).stream()
                .findFirst()
                .map(URI::create)
                .orElseThrow(() -> new IllegalStateException(String.format("No %s header present", FORWARDED_URL)));
        return new RequestEntity<>(incoming.getBody(), headers, incoming.getMethod(), uri);
    }
}
