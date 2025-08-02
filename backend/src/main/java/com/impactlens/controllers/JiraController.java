package com.impactlens.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.impactlens.dto.SyncResult;
import com.impactlens.entities.JiraTicket;
import com.impactlens.services.JiraService;

import java.util.List;

@RestController
@RequestMapping("/api/jira")
@CrossOrigin(origins = "*")
public class JiraController {
    
    private static final Logger logger = LoggerFactory.getLogger(JiraController.class);
    
    @Autowired
    private JiraService jiraService;
    
    /**
     * Fetch a single JIRA ticket by key
     */
    @GetMapping("/ticket/{ticketKey}")
    public ResponseEntity<JiraTicket> getTicket(@PathVariable String ticketKey) {
        logger.info("Fetching JIRA ticket: {}", ticketKey);
        
        JiraTicket ticket = jiraService.fetchTicket(ticketKey);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update a JIRA ticket by key
     */
    @PutMapping("/ticket/{ticketKey}")
    public ResponseEntity<JiraTicket> updateTicket(@PathVariable String ticketKey) {
        logger.info("Updating JIRA ticket: {}", ticketKey);
        
        JiraTicket ticket = jiraService.updateTicket(ticketKey);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Search JIRA tickets
     */
    @GetMapping("/search")
    public ResponseEntity<Iterable<JiraTicket>> searchTickets(@RequestParam String query) {
        logger.info("Searching JIRA tickets with query: {}", query);
        
        Iterable<JiraTicket> tickets = jiraService.searchTickets(query);
        return ResponseEntity.ok(tickets);
    }
    
    /**
     * Get JIRA ticket comments
     */
    @GetMapping("/ticket/{ticketKey}/comments")
    public ResponseEntity<String> getTicketComments(@PathVariable String ticketKey) {
        logger.info("Fetching comments for JIRA ticket: {}", ticketKey);
        
        String comments = jiraService.getTicketComments(ticketKey);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * Get JIRA ticket attachments
     */
    @GetMapping("/ticket/{ticketKey}/attachments")
    public ResponseEntity<String> getTicketAttachments(@PathVariable String ticketKey) {
        logger.info("Fetching attachments for JIRA ticket: {}", ticketKey);
        
        String attachments = jiraService.getTicketAttachments(ticketKey);
        return ResponseEntity.ok(attachments);
    }
    
    /**
     * Sync all JIRA tickets and save to database
     * Updates existing tickets and adds new ones
     */
    @PostMapping("/sync")
    public ResponseEntity<SyncResult> syncAllTickets(@RequestBody SyncRequest request) {
        String jql = request.getJql() != null ? request.getJql() : "ORDER BY updated DESC";
        int maxResults = request.getMaxResults() != null ? request.getMaxResults() : 100;
        
        logger.info("Starting JIRA sync with JQL: {}, maxResults: {}", jql, maxResults);
        
        try {
            SyncResult result = jiraService.syncAllJiraTickets(jql, maxResults);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error during JIRA sync: {}", e.getMessage(), e);
            SyncResult errorResult = new SyncResult(jql);
            errorResult.setStatus("FAILED");
            errorResult.setErrorMessage(e.getMessage());
            errorResult.setSyncEndTime(java.time.LocalDateTime.now());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }
    
    /**
     * Sync all JIRA tickets with default parameters
     */
    @PostMapping("/sync/all")
    public ResponseEntity<SyncResult> syncAllTicketsDefault() {
        SyncRequest request = new SyncRequest();
        request.setJql("ORDER BY updated DESC");
        request.setMaxResults(100);
        return syncAllTickets(request);
    }
    
    /**
     * Sync recent JIRA tickets (updated in last 30 days)
     */
    @PostMapping("/sync/recent")
    public ResponseEntity<SyncResult> syncRecentTickets(@RequestBody(required = false) SyncRequest request) {
        int days = request != null && request.getDays() != null ? request.getDays() : 30;
        String jql = String.format("updated >= -%dd ORDER BY updated DESC", days);
        
        SyncRequest syncRequest = new SyncRequest();
        syncRequest.setJql(jql);
        syncRequest.setMaxResults(100);
        return syncAllTickets(syncRequest);
    }
    
    /**
     * Request DTO for sync operations
     */
    public static class SyncRequest {
        private String jql;
        private Integer maxResults;
        private Integer days;
        
        public String getJql() {
            return jql;
        }
        
        public void setJql(String jql) {
            this.jql = jql;
        }
        
        public Integer getMaxResults() {
            return maxResults;
        }
        
        public void setMaxResults(Integer maxResults) {
            this.maxResults = maxResults;
        }
        
        public Integer getDays() {
            return days;
        }
        
        public void setDays(Integer days) {
            this.days = days;
        }
    }
} 