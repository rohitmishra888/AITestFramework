package com.impactlens.config;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JiraConfig {

    @Value("${jira.base-url}")
    private String jiraBaseUrl;

    @Value("${jira.username}")
    private String jiraUsername;

    @Value("${jira.api-token}")
    private String jiraApiToken;

    @Value("${jira.connection-timeout:30000}")
    private int connectionTimeout;

    @Value("${jira.read-timeout:30000}")
    private int readTimeout;

    @Bean
    public RestTemplate jiraRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        
        return new RestTemplate(factory);
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    public String getJiraBaseUrl() {
        return jiraBaseUrl;
    }

    public String getJiraUsername() {
        return jiraUsername;
    }

    public String getJiraApiToken() {
        return jiraApiToken;
    }

    public String getBasicAuthHeader() {
        String credentials = jiraUsername + ":" + jiraApiToken;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }
    
    public boolean isValidConfiguration() {
        return jiraBaseUrl != null && !jiraBaseUrl.trim().isEmpty() &&
               jiraUsername != null && !jiraUsername.trim().isEmpty() &&
               jiraApiToken != null && !jiraApiToken.trim().isEmpty();
    }
    
    public String getFormattedBaseUrl() {
        String url = jiraBaseUrl.trim();
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }
} 