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
    public ResponseEntity<SyncResult> syncAllTickets(
            @RequestParam(defaultValue = "ORDER BY updated DESC") String jql,
            @RequestParam(defaultValue = "100") int maxResults) {
        
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
        return syncAllTickets("ORDER BY updated DESC", 100);
    }
    
    /**
     * Sync recent JIRA tickets (updated in last 30 days)
     */
    @PostMapping("/sync/recent")
    public ResponseEntity<SyncResult> syncRecentTickets(
            @RequestParam(defaultValue = "30") int days) {
        
        String jql = String.format("updated >= -%dd ORDER BY updated DESC", days);
        return syncAllTickets(jql, 100);
    }
} 