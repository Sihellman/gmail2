package com.example.gmail2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("mail.external")
public class ExternalMailConfiguration {
    private String ip;
    private String url;
    private String key;
}