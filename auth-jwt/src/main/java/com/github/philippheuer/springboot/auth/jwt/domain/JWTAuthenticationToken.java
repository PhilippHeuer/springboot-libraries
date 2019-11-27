package com.github.philippheuer.springboot.auth.jwt.domain;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode(callSuper=false)
@Getter
public class JWTAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final Logger log = LoggerFactory.getLogger("JWTAuthenticationToken");

    private String userId;

    private String userName;

    private String firstName;

    private String lastName;

    private String email;


    public JWTAuthenticationToken(Object principal, DecodedJWT jwt) {
        super(principal, null, getAuthoritiesFromJwt(jwt));

        this.userId = jwt.getClaim("preferred_username").asString();
        this.userName = jwt.getClaim("name").asString();
        this.firstName = jwt.getClaim("given_name").asString();
        this.lastName = jwt.getClaim("family_name").asString();
        this.email = jwt.getClaim("email").asString();
    }

    private static List<SimpleGrantedAuthority> getAuthoritiesFromJwt(DecodedJWT jwt) {
        List<String> userRoles = new ArrayList<>();
        Map<String, Object> resourceMap = jwt.getClaim("resource_access").asMap();
        resourceMap.forEach((resourceName, resource) -> {
            log.debug("Parsing resource_access - {}", resourceName);
            Map<String, List<String>> roleMap = (Map<String, List<String>>) resource;
            if (roleMap.containsKey("roles")) {
                roleMap.get("roles").forEach(role -> {
                    log.debug("Adding role {} from {}!", role, resourceName);
                    userRoles.add(role);
                });
            } else {
                log.debug("No roles in resource {}!", resourceName);
            }
        });
        var authorities = userRoles.stream()
            .map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toList());

        return authorities;
    }

}
