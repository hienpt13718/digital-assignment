package com.pth.digital_assignment.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    private JwtProperties jwt;
}
