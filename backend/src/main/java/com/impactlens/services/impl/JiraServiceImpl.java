package com.impactlens.services.impl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.impactlens.dto.SyncResult;
import com.impactlens.entities.JiraTicket;
import com.impactlens.repositories.JiraTicketRepository;
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
    
    @Autowired
    private JiraTicketRepository jiraTicketRepository;
    
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
    
    @Override
    public SyncResult syncAllJiraTickets(String jql, int maxResults) {
        logger.info("Starting JIRA sync with JQL: {}, maxResults: {}", jql, maxResults);
        
        SyncResult result = new SyncResult(jql);
        
        // Validate configuration first
        if (!jiraConfig.isValidConfiguration()) {
            logger.error("Invalid Jira configuration. Please check base URL, username, and API token.");
            result.setStatus("FAILED");
            result.setErrorMessage("Invalid Jira configuration");
            result.setSyncEndTime(java.time.LocalDateTime.now());
            return result;
        }
        
        try {
            // Get existing ticket keys from database for efficient comparison
            Set<String> existingTicketKeys = new HashSet<>(jiraTicketRepository.findAllTicketKeys());
            logger.info("Found {} existing tickets in database", existingTicketKeys.size());
            
            // Fetch tickets from JIRA
            List<JiraTicket> ticketsToSave = new ArrayList<>();
            List<String> failedTicketKeys = new ArrayList<>();
            
            String url = jiraConfig.getFormattedBaseUrl() + "rest/api/3/search";
            
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("jql", jql)
                .queryParam("maxResults", maxResults)
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
                logger.info("Fetched {} tickets from JIRA", searchResponse.getIssues().size());
                
                for (JiraApiResponse.Issue issue : searchResponse.getIssues()) {
                    try {
                        JiraTicket ticket = convertToJiraTicket(issue);
                        
                        // Check if ticket already exists
                        if (existingTicketKeys.contains(ticket.getTicketKey())) {
                            // Update existing ticket
                            JiraTicket existingTicket = jiraTicketRepository.findByTicketKey(ticket.getTicketKey()).orElse(null);
                            if (existingTicket != null) {
                                // Update fields
                                existingTicket.setSummary(ticket.getSummary());
                                existingTicket.setDescription(ticket.getDescription());
                                existingTicket.setStatus(ticket.getStatus());
                                existingTicket.setPriority(ticket.getPriority());
                                existingTicket.setAssignee(ticket.getAssignee());
                                existingTicket.setReporter(ticket.getReporter());
                                existingTicket.setCreatedAt(ticket.getCreatedAt());
                                existingTicket.setUpdatedAt(ticket.getUpdatedAt());
                                existingTicket.setRawData(ticket.getRawData());
                                existingTicket.setLastSyncedAt(ticket.getLastSyncedAt());
                                
                                ticketsToSave.add(existingTicket);
                                result.setExistingTicketsUpdated(result.getExistingTicketsUpdated() + 1);
                            }
                        } else {
                            // Add new ticket
                            ticketsToSave.add(ticket);
                            result.setNewTicketsAdded(result.getNewTicketsAdded() + 1);
                        }
                        
                        result.setTotalTicketsFetched(result.getTotalTicketsFetched() + 1);
                        
                    } catch (Exception e) {
                        logger.error("Error processing ticket {}: {}", issue.getKey(), e.getMessage());
                        failedTicketKeys.add(issue.getKey());
                        result.setFailedTickets(result.getFailedTickets() + 1);
                    }
                }
                
                // Save all tickets to database
                if (!ticketsToSave.isEmpty()) {
                    jiraTicketRepository.saveAll(ticketsToSave);
                    logger.info("Successfully saved {} tickets to database", ticketsToSave.size());
                }
                
                result.setFailedTicketKeys(failedTicketKeys);
                result.setStatus("COMPLETED");
                
            } else {
                logger.warn("No tickets found or empty response from JIRA");
                result.setStatus("COMPLETED");
            }
            
        } catch (Exception e) {
            logger.error("Error syncing JIRA tickets: {}", e.getMessage(), e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
        }
        
        result.setSyncEndTime(java.time.LocalDateTime.now());
        logger.info("JIRA sync completed. Total: {}, New: {}, Updated: {}, Failed: {}", 
            result.getTotalTicketsFetched(), result.getNewTicketsAdded(), 
            result.getExistingTicketsUpdated(), result.getFailedTickets());
        
        return result;
    }
    
    private JiraTicket convertToJiraTicket(JiraApiResponse.Issue issue) {
        JiraTicket ticket = new JiraTicket();
        JiraApiResponse.Fields fields = issue.getFields();
        
        ticket.setTicketKey(issue.getKey());
        ticket.setTicketId(issue.getId());
        ticket.setSummary(fields.getSummary());
        
        // Handle description field which can be string or rich text object
        String description = extractTextFromField(fields.getDescription());
        ticket.setDescription(description);
        
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
                ticket.setCreatedAt(fields.getCreated());
            }
            if (fields.getUpdated() != null) {
                ticket.setUpdatedAt(fields.getUpdated());
            }
        } catch (Exception e) {
            logger.warn("Error parsing dates for ticket {}: {}", issue.getKey(), e.getMessage());
            ticket.setCreatedAt(java.time.LocalDateTime.now().toString());
            ticket.setUpdatedAt(java.time.LocalDateTime.now().toString());
        }
        
        ticket.setLastSyncedAt(java.time.LocalDateTime.now().toString());
        
        // Store raw data
        try {
            ticket.setRawData(objectMapper.writeValueAsString(issue));
        } catch (Exception e) {
            logger.warn("Error serializing raw data for ticket {}: {}", issue.getKey(), e.getMessage());
            ticket.setRawData("{}");
        }
        
        return ticket;
    }
    
    /**
     * Extract text from a JIRA field that can be either a string or a rich text object
     */
    private String extractTextFromField(Object field) {
        if (field == null) {
            return null;
        }
        
        if (field instanceof String) {
            return (String) field;
        }
        
        // Handle rich text objects (Atlassian Document Format)
        try {
            String json = objectMapper.writeValueAsString(field);
            // Try to extract text from rich text format
            if (json.contains("\"type\":\"doc\"") || json.contains("\"content\"")) {
                // This is a rich text object, extract plain text
                return extractTextFromRichText(json);
            } else {
                // Fallback to string representation
                return field.toString();
            }
        } catch (Exception e) {
            logger.warn("Error extracting text from field: {}", e.getMessage());
            return field.toString();
        }
    }
    
    /**
     * Extract plain text from JIRA rich text format
     */
    private String extractTextFromRichText(String json) {
        try {
            // Simple text extraction from rich text JSON
            // Remove JSON structure and extract text content
            String text = json.replaceAll("\"type\":\"[^\"]*\"", "")
                             .replaceAll("\"content\":\\[", "")
                             .replaceAll("\\]", "")
                             .replaceAll("\"text\":\"", "")
                             .replaceAll("\"[^}]*}", "")
                             .replaceAll("\\{", "")
                             .replaceAll("\\}", "")
                             .replaceAll(",", " ")
                             .trim();
            
            // Clean up any remaining JSON artifacts
            text = text.replaceAll("\\s+", " ").trim();
            
            return text.isEmpty() ? null : text;
        } catch (Exception e) {
            logger.warn("Error extracting text from rich text: {}", e.getMessage());
            return null;
        }
    }
} 