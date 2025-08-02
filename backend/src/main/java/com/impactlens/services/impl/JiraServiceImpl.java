package com.impactlens.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.impactlens.config.JiraConfig;
import com.impactlens.dto.JiraApiResponse;
import com.impactlens.entities.JiraTicket;
import com.impactlens.services.JiraService;

@Service
public class JiraServiceImpl implements JiraService {
    
    private static final Logger logger = LoggerFactory.getLogger(JiraServiceImpl.class);
    private static final DateTimeFormatter JIRA_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    @Autowired
    private JiraConfig jiraConfig;
    
    @Autowired
    private RestTemplate jiraRestTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public JiraTicket fetchTicket(String ticketKey) {
        logger.info("Fetching ticket from Jira: {}", ticketKey);
        
        // Validate configuration first
        if (!jiraConfig.isValidConfiguration()) {
            logger.error("Invalid Jira configuration. Please check base URL, username, and API token.");
            return null;
        }
        
        try {
            // Use the formatted base URL
            String baseUrl = jiraConfig.getFormattedBaseUrl();
            
            String url = baseUrl + "rest/api/3/issue/" + ticketKey;
            logger.debug("Making request to Jira URL: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jiraConfig.getBasicAuthHeader());
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json");
            headers.set("User-Agent", "ImpactLens/1.0");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // First, try to get the raw response to debug
            ResponseEntity<String> rawResponse = jiraRestTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);
            
            logger.debug("Jira response status: {}", rawResponse.getStatusCode());
            logger.debug("Jira response headers: {}", rawResponse.getHeaders());
            logger.debug("Jira response body (first 500 chars): {}", 
                rawResponse.getBody() != null ? rawResponse.getBody().substring(0, Math.min(500, rawResponse.getBody().length())) : "null");
            
            // Check if response is JSON
            if (rawResponse.getHeaders().getContentType() != null && 
                rawResponse.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) {
                
                // Parse the JSON response
                JiraApiResponse.Issue issue = objectMapper.readValue(rawResponse.getBody(), JiraApiResponse.Issue.class);
                if (issue != null) {
                    return convertToJiraTicket(issue);
                }
            } else {
                logger.error("Jira returned non-JSON response. Content-Type: {}, Response: {}", 
                    rawResponse.getHeaders().getContentType(), rawResponse.getBody());
                throw new RuntimeException("Jira API returned non-JSON response. Check authentication and URL.");
            }
            
        } catch (Exception e) {
            logger.error("Error fetching ticket {} from Jira: {}", ticketKey, e.getMessage(), e);
            if (e.getMessage().contains("401")) {
                logger.error("Authentication failed. Please check Jira credentials.");
            } else if (e.getMessage().contains("404")) {
                logger.error("Ticket {} not found in Jira.", ticketKey);
            } else if (e.getMessage().contains("403")) {
                logger.error("Access denied. Please check Jira permissions.");
            }
        }
        
        // Return null if ticket not found or error occurred
        return null;
    }
    
    @Override
    public JiraTicket updateTicket(String ticketKey) {
        logger.info("Updating ticket from Jira: {}", ticketKey);
        return fetchTicket(ticketKey);
    }
    
    @Override
    public Iterable<JiraTicket> searchTickets(String query) {
        logger.info("Searching tickets in Jira with query: {}", query);
        
        // Validate configuration first
        if (!jiraConfig.isValidConfiguration()) {
            logger.error("Invalid Jira configuration. Please check base URL, username, and API token.");
            return new ArrayList<>();
        }
        
        try {
            String url = jiraConfig.getFormattedBaseUrl() + "rest/api/3/search";
            
            String jql = "text ~ \"" + query + "\" ORDER BY updated DESC";
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("jql", jql)
                .queryParam("maxResults", "20")
                .queryParam("fields", "summary,description,status,priority,assignee,reporter,created,updated");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jiraConfig.getBasicAuthHeader());
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json");
            headers.set("User-Agent", "ImpactLens/1.0");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<JiraApiResponse.SearchResponse> response = jiraRestTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, entity, JiraApiResponse.SearchResponse.class);
            
            JiraApiResponse.SearchResponse searchResponse = response.getBody();
            if (searchResponse != null && searchResponse.getIssues() != null) {
                List<JiraTicket> tickets = new ArrayList<>();
                for (JiraApiResponse.Issue issue : searchResponse.getIssues()) {
                    tickets.add(convertToJiraTicket(issue));
                }
                return tickets;
            }
            
        } catch (Exception e) {
            logger.error("Error searching tickets in Jira: {}", e.getMessage(), e);
        }
        
        return new ArrayList<>();
    }
    
    @Override
    public String getTicketComments(String ticketKey) {
        logger.info("Fetching comments for ticket: {}", ticketKey);
        
        try {
            String url = jiraConfig.getFormattedBaseUrl() + "rest/api/3/issue/" + ticketKey + "/comment";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jiraConfig.getBasicAuthHeader());
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json");
            headers.set("User-Agent", "ImpactLens/1.0");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<JiraApiResponse.CommentsResponse> response = jiraRestTemplate.exchange(
                url, HttpMethod.GET, entity, JiraApiResponse.CommentsResponse.class);
            
            JiraApiResponse.CommentsResponse commentsResponse = response.getBody();
            if (commentsResponse != null && commentsResponse.getComments() != null) {
                StringBuilder comments = new StringBuilder();
                for (JiraApiResponse.Comment comment : commentsResponse.getComments()) {
                    comments.append(comment.getBody()).append("\n");
                }
                return comments.toString();
            }
            
        } catch (Exception e) {
            logger.error("Error fetching comments for ticket {}: {}", ticketKey, e.getMessage(), e);
        }
        
        return "";
    }
    
    @Override
    public String getTicketAttachments(String ticketKey) {
        logger.info("Fetching attachments for ticket: {}", ticketKey);
        
        try {
            String url = jiraConfig.getFormattedBaseUrl() + "rest/api/3/issue/" + ticketKey + "?fields=attachment";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jiraConfig.getBasicAuthHeader());
            headers.set("Accept", "application/json");
            headers.set("Content-Type", "application/json");
            headers.set("User-Agent", "ImpactLens/1.0");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<JiraApiResponse.Issue> response = jiraRestTemplate.exchange(
                url, HttpMethod.GET, entity, JiraApiResponse.Issue.class);
            
            JiraApiResponse.Issue issue = response.getBody();
            if (issue != null && issue.getFields() != null) {
                // For now, return a simple message about attachments
                return "Attachments found for ticket: " + ticketKey;
            }
            
        } catch (Exception e) {
            logger.error("Error fetching attachments for ticket {}: {}", ticketKey, e.getMessage(), e);
        }
        
        return "";
    }
    
    private JiraTicket convertToJiraTicket(JiraApiResponse.Issue issue) {
        JiraTicket ticket = new JiraTicket();
        JiraApiResponse.Fields fields = issue.getFields();
        
        ticket.setTicketKey(issue.getKey());
        ticket.setTicketId(issue.getId());
        ticket.setSummary(fields.getSummary());
        ticket.setDescription(fields.getDescription());
        
        if (fields.getStatus() != null) {
            ticket.setStatus(fields.getStatus().getName());
        }
        
        if (fields.getPriority() != null) {
            ticket.setPriority(fields.getPriority().getName());
        }
        
        if (fields.getAssignee() != null) {
            ticket.setAssignee(fields.getAssignee().getDisplayName());
        }
        
        if (fields.getReporter() != null) {
            ticket.setReporter(fields.getReporter().getDisplayName());
        }
        
        // Parse dates
        try {
            if (fields.getCreated() != null) {
                ticket.setCreatedAt(LocalDateTime.parse(fields.getCreated().substring(0, 19)));
            }
            if (fields.getUpdated() != null) {
                ticket.setUpdatedAt(LocalDateTime.parse(fields.getUpdated().substring(0, 19)));
            }
        } catch (Exception e) {
            logger.warn("Error parsing dates for ticket {}: {}", issue.getKey(), e.getMessage());
            ticket.setCreatedAt(LocalDateTime.now());
            ticket.setUpdatedAt(LocalDateTime.now());
        }
        
        ticket.setLastSyncedAt(LocalDateTime.now());
        
        // Store raw data
        try {
            ticket.setRawData(objectMapper.writeValueAsString(issue));
        } catch (Exception e) {
            logger.warn("Error serializing raw data for ticket {}: {}", issue.getKey(), e.getMessage());
            ticket.setRawData("{}");
        }
        
        return ticket;
    }
} 