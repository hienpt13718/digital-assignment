package com.pth.digital_assignment.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private String expiration;

    public Date getExpirationFromNow() {
        var expirationTime = Duration.parse(expiration);
        return Date.from(Instant.now().plus(expirationTime));
    }
}
