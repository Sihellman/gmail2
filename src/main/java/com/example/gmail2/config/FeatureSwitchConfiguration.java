package com.example.gmail2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties("featureswitch")
@Configuration
public class FeatureSwitchConfiguration {
    private boolean printIp;
    private boolean emailUp;
}