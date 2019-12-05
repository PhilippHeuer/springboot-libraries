package com.github.philippheuer.springboot.auth.jwt;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.philippheuer.common.exceptions.DownstreamServiceNotAvailable;
import com.github.philippheuer.springboot.auth.jwt.domain.JWTAuthenticationToken;
import com.github.philippheuer.springboot.auth.jwt.util.HttpClientUrlJwkProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT Authorization Filter
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger("JwtAuthorizationFilter");

    // Header Name
    public static final String TOKEN_HEADER = "Authorization";

    // Header Value Prefix
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Auth Issuer
     */
    private String issuer;

    /**
     * Allowed Audiences
     */
    private List<String> audiences;

    /**
     * JWKS Url
     */
    private String jwksUrl;

    /**
     * Keep Certs for a maximum 8 hours if they aren't used
     */
    public static Cache<String, Jwk> jwksKeys = Caffeine.newBuilder()
        .expireAfterAccess(4, TimeUnit.HOURS)
        .expireAfterWrite(1, TimeUnit.DAYS)
        .build();

    /**
     * Constructor
     *
     * @param authenticationManager AuthenticationManager
     */
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, Map<String, Object> extraConfiguration) {
        super(authenticationManager);

        // parse extraConfiguration
        if (extraConfiguration.containsKey("issuer")) {
            issuer = extraConfiguration.get("issuer").toString();
        }
        if (extraConfiguration.containsKey("audiences")) {
            audiences = (List<String>) extraConfiguration.get("audiences");
        }
        if (extraConfiguration.containsKey("jwks_uri")) {
            jwksUrl = extraConfiguration.get("jwks_uri").toString();
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        var authentication = getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        var token = request.getHeader(TOKEN_HEADER);
        if (StringUtils.isNotEmpty(token) && token.toUpperCase().startsWith(TOKEN_PREFIX.toUpperCase())) {
            try {
                String tokenValue = token.substring(TOKEN_PREFIX.length());
                DecodedJWT jwt = JWT.decode(tokenValue);

                // check issuer against configuration
                if (!issuer.equalsIgnoreCase(jwt.getIssuer())) {
                    log.warn("Received invalid access token! Issuer doesn't match expected issuer. [Got: {}, Expected: {}]", jwt.getIssuer(), issuer);
                    return null;
                }
                log.debug("Verifying token by [{}] against keyId [{}]", jwt.getIssuer(), jwt.getKeyId());

                if (jwksKeys.getIfPresent(jwt.getKeyId()) == null) {
                    log.debug("KeyID [{}] is not known yet, loading from jwks uri ...", jwt.getKeyId());

                    // ensure we loaded the key to verify the jwt
                    loadKeys(issuer, jwksUrl);
                }

                // cert verification algorithm
                Algorithm algorithm;
                // - rsa
                if (jwt.getAlgorithm().equalsIgnoreCase("RS256")) {
                    log.debug("Looking for key with id [{}]", jwt.getKeyId());
                    algorithm = Algorithm.RSA256((RSAPublicKey) jwksKeys.getIfPresent(jwt.getKeyId()).getPublicKey(), null);
                } else {
                    log.warn("Received invalid access token! Signing algorithm [" + jwt.getAlgorithm() + "] is not supported.");
                    return null;
                }

                // verification
                JWTVerifier verifier = JWT.require(algorithm)
                        // issuer
                        .withIssuer(issuer)
                        .withAudience(audiences.toArray(new String[0]))
                        // build verifier
                        .build();

                // verify
                verifier.verify(jwt);

                // user information
                var username = jwt.getClaim("preferred_username").asString();
                if (StringUtils.isNotEmpty(username)) {
                    return new JWTAuthenticationToken(username, jwt);
                }
            } catch (AlgorithmMismatchException ex) {
                log.warn("Received invalid access token! The algorithm stated in the token's header is not equal to the required one.");
            } catch (SignatureVerificationException ex) {
                log.warn("Received invalid access token! The token signature is invalid.");
            } catch (TokenExpiredException ex) {
                log.warn("Received invalid access token! The token is expired.");
            } catch (InvalidClaimException ex) {
                log.warn("Received invalid access token! The claim contained a different value than the expected one.");
            } catch (JwkException ex) {
                log.warn("Received invalid access token! {}", ex.getMessage());
            } catch (JWTVerificationException ex) {
                log.warn("Received invalid access token! {}", ex.getMessage());
            }
        }

        return null;
    }

    /**
     * Loads the key to verify a token by a issuer
     *
     * @param issuer Issuer URI
     * @param url JWKS URI
     */
    private void loadKeys(String issuer, String url) {
        try {
            UrlJwkProvider provider = new HttpClientUrlJwkProvider(new URL(url), 5000, 5000);
            provider.getAll().forEach(key -> {
                log.debug("Loaded key [{}] from {}", key.getId(), issuer);
                jwksKeys.put(key.getId(), key);
            });
        } catch (Exception ex) {
            throw new DownstreamServiceNotAvailable("AUTH-1004", String.format("Can't load certificates from sso server, the sso server may not be available! [%s]", ex.getMessage()));
        }
    }
}
