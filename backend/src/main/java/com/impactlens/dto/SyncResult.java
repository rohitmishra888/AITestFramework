package com.impactlens.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SyncResult {
    
    private int totalTicketsFetched;
    private int newTicketsAdded;
    private int existingTicketsUpdated;
    private int failedTickets;
    private List<String> failedTicketKeys;
    private LocalDateTime syncStartTime;
    private LocalDateTime syncEndTime;
    private String jql;
    private String status;
    private String errorMessage;
    
    public SyncResult() {}
    
    public SyncResult(String jql) {
        this.jql = jql;
        this.syncStartTime = LocalDateTime.now();
        this.status = "IN_PROGRESS";
    }
    
    // Getters and Setters
    public int getTotalTicketsFetched() {
        return totalTicketsFetched;
    }
    
    public void setTotalTicketsFetched(int totalTicketsFetched) {
        this.totalTicketsFetched = totalTicketsFetched;
    }
    
    public int getNewTicketsAdded() {
        return newTicketsAdded;
    }
    
    public void setNewTicketsAdded(int newTicketsAdded) {
        this.newTicketsAdded = newTicketsAdded;
    }
    
    public int getExistingTicketsUpdated() {
        return existingTicketsUpdated;
    }
    
    public void setExistingTicketsUpdated(int existingTicketsUpdated) {
        this.existingTicketsUpdated = existingTicketsUpdated;
    }
    
    public int getFailedTickets() {
        return failedTickets;
    }
    
    public void setFailedTickets(int failedTickets) {
        this.failedTickets = failedTickets;
    }
    
    public List<String> getFailedTicketKeys() {
        return failedTicketKeys;
    }
    
    public void setFailedTicketKeys(List<String> failedTicketKeys) {
        this.failedTicketKeys = failedTicketKeys;
    }
    
    public LocalDateTime getSyncStartTime() {
        return syncStartTime;
    }
    
    public void setSyncStartTime(LocalDateTime syncStartTime) {
        this.syncStartTime = syncStartTime;
    }
    
    public LocalDateTime getSyncEndTime() {
        return syncEndTime;
    }
    
    public void setSyncEndTime(LocalDateTime syncEndTime) {
        this.syncEndTime = syncEndTime;
    }
    
    public String getJql() {
        return jql;
    }
    
    public void setJql(String jql) {
        this.jql = jql;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public long getDurationInSeconds() {
        if (syncStartTime != null && syncEndTime != null) {
            return java.time.Duration.between(syncStartTime, syncEndTime).getSeconds();
        }
        return 0;
    }
} 