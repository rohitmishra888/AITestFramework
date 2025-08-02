package com.impactlens.services;

import com.impactlens.dto.SyncResult;
import com.impactlens.entities.JiraTicket;

public interface JiraService {
    
    /**
     * Fetch a ticket from Jira API
     */
    JiraTicket fetchTicket(String ticketKey);
    
    /**
     * Update ticket data from Jira
     */
    JiraTicket updateTicket(String ticketKey);
    
    /**
     * Search for tickets in Jira
     */
    Iterable<JiraTicket> searchTickets(String query);
    
    /**
     * Get ticket comments
     */
    String getTicketComments(String ticketKey);
    
    /**
     * Get ticket attachments
     */
    String getTicketAttachments(String ticketKey);
    
    /**
     * Fetch all JIRAs and save them to database
     * Updates existing tickets and adds new ones
     */
    SyncResult syncAllJiraTickets(String jql, int maxResults);
} 