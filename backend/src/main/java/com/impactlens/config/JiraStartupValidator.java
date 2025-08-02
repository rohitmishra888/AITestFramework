package com.impactlens.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class JiraStartupValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(JiraStartupValidator.class);
    
    @Autowired
    private JiraConfig jiraConfig;
    
    @Autowired
    private RestTemplate jiraRestTemplate;
    
    @EventListener(ApplicationReadyEvent.class)
    public void validateJiraConnection() {
        logger.info("Validating Jira configuration and connectivity...");
        
        try {
            // Check configuration
            if (!jiraConfig.isValidConfiguration()) {
                logger.error("❌ Jira configuration is invalid. Please check application.yml");
                logger.error("   Base URL: {}", jiraConfig.getJiraBaseUrl());
                logger.error("   Username: {}", jiraConfig.getJiraUsername());
                logger.error("   API Token: {}", jiraConfig.getJiraApiToken() != null ? "***" : "null");
                return;
            }
            
            // Test connection to Jira
            String testUrl = jiraConfig.getFormattedBaseUrl() + "rest/api/3/myself";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jiraConfig.getBasicAuthHeader());
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json");
            headers.set("User-Agent", "ImpactLens/1.0");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = jiraRestTemplate.exchange(
                testUrl, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Jira connection successful!");
                logger.info("   Base URL: {}", jiraConfig.getFormattedBaseUrl());
                logger.info("   Username: {}", jiraConfig.getJiraUsername());
            } else {
                logger.error("❌ Jira connection failed with status: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("❌ Jira connection test failed: {}", e.getMessage());
            if (e.getMessage().contains("401")) {
                logger.error("   Authentication failed. Please check username and API token.");
            } else if (e.getMessage().contains("404")) {
                logger.error("   Jira URL not found. Please check the base URL.");
            } else if (e.getMessage().contains("403")) {
                logger.error("   Access denied. Please check permissions.");
            } else if (e.getMessage().contains("Connection refused")) {
                logger.error("   Cannot connect to Jira. Please check network connectivity.");
            }
        }
    }
} 