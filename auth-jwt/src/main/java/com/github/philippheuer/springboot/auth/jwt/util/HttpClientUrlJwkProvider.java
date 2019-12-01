package com.github.philippheuer.springboot.auth.jwt.util;

import com.auth0.jwk.SigningKeyNotFoundException;
import com.auth0.jwk.UrlJwkProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.ProxySelector;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class HttpClientUrlJwkProvider extends UrlJwkProvider {

    private final HttpClient httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    private final URL url;

    private final Integer connectTimeout;

    private final Integer readTimeout;

    private final ObjectReader reader;

    public HttpClientUrlJwkProvider(URL url, Integer connectTimeout, Integer readTimeout) {
        super(url, connectTimeout, readTimeout);
        this.url = url;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.reader = new ObjectMapper().readerFor(Map.class);
    }

    private Map<String, Object> getJwks() throws SigningKeyNotFoundException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(url.toURI())
                .timeout(Duration.ofSeconds(connectTimeout))
                .header("Accept", "application/json")
                .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            return reader.readValue(response.body());
        } catch (Exception e) {
            throw new SigningKeyNotFoundException("Cannot obtain jwks from url " + url.toString(), e);
        }
    }

}
