package com.impactlens.dto;

import java.util.List;
import java.util.UUID;

public class AnalysisResponse {
    
    private UUID analysisId;
    private String status;
    private AnalysisReport report;
    private AnalysisMetadata metadata;
    
    public static class AnalysisReport {
        private String summary;
        private List<GapAnalysis> gapsIdentified;
        private List<RelatedTicket> relatedTickets;
        private List<RegressionArea> regressionAreas;
        private List<String> recommendations;
        
        // Getters and Setters
        public String getSummary() {
            return summary;
        }
        
        public void setSummary(String summary) {
            this.summary = summary;
        }
        
        public List<GapAnalysis> getGapsIdentified() {
            return gapsIdentified;
        }
        
        public void setGapsIdentified(List<GapAnalysis> gapsIdentified) {
            this.gapsIdentified = gapsIdentified;
        }
        
        public List<RelatedTicket> getRelatedTickets() {
            return relatedTickets;
        }
        
        public void setRelatedTickets(List<RelatedTicket> relatedTickets) {
            this.relatedTickets = relatedTickets;
        }
        
        public List<RegressionArea> getRegressionAreas() {
            return regressionAreas;
        }
        
        public void setRegressionAreas(List<RegressionArea> regressionAreas) {
            this.regressionAreas = regressionAreas;
        }
        
        public List<String> getRecommendations() {
            return recommendations;
        }
        
        public void setRecommendations(List<String> recommendations) {
            this.recommendations = recommendations;
        }
    }
    
    public static class GapAnalysis {
        private String category;
        private String description;
        private String severity;
        private String impact;
        private List<String> suggestions;
        
        // Getters and Setters
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public void setSeverity(String severity) {
            this.severity = severity;
        }
        
        public String getImpact() {
            return impact;
        }
        
        public void setImpact(String impact) {
            this.impact = impact;
        }
        
        public List<String> getSuggestions() {
            return suggestions;
        }
        
        public void setSuggestions(List<String> suggestions) {
            this.suggestions = suggestions;
        }
    }
    
    public static class RelatedTicket {
        private String ticketKey;
        private String summary;
        private String status;
        private String priority;
        private double relevanceScore;
        private String relationshipType;
        private String impactDescription;
        
        // Getters and Setters
        public String getTicketKey() {
            return ticketKey;
        }
        
        public void setTicketKey(String ticketKey) {
            this.ticketKey = ticketKey;
        }
        
        public String getSummary() {
            return summary;
        }
        
        public void setSummary(String summary) {
            this.summary = summary;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getPriority() {
            return priority;
        }
        
        public void setPriority(String priority) {
            this.priority = priority;
        }
        
        public double getRelevanceScore() {
            return relevanceScore;
        }
        
        public void setRelevanceScore(double relevanceScore) {
            this.relevanceScore = relevanceScore;
        }
        
        public String getRelationshipType() {
            return relationshipType;
        }
        
        public void setRelationshipType(String relationshipType) {
            this.relationshipType = relationshipType;
        }
        
        public String getImpactDescription() {
            return impactDescription;
        }
        
        public void setImpactDescription(String impactDescription) {
            this.impactDescription = impactDescription;
        }
    }
    
    public static class RegressionArea {
        private String area;
        private String description;
        private String riskLevel;
        private List<String> testCases;
        private String rationale;
        
        // Getters and Setters
        public String getArea() {
            return area;
        }
        
        public void setArea(String area) {
            this.area = area;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getRiskLevel() {
            return riskLevel;
        }
        
        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }
        
        public List<String> getTestCases() {
            return testCases;
        }
        
        public void setTestCases(List<String> testCases) {
            this.testCases = testCases;
        }
        
        public String getRationale() {
            return rationale;
        }
        
        public void setRationale(String rationale) {
            this.rationale = rationale;
        }
    }
    
    public static class AnalysisMetadata {
        private long processingTime;
        private int ticketsAnalyzed;
        private boolean cacheHit;
        private String completedAt;
        private String modelUsed;
        
        // Getters and Setters
        public long getProcessingTime() {
            return processingTime;
        }
        
        public void setProcessingTime(long processingTime) {
            this.processingTime = processingTime;
        }
        
        public int getTicketsAnalyzed() {
            return ticketsAnalyzed;
        }
        
        public void setTicketsAnalyzed(int ticketsAnalyzed) {
            this.ticketsAnalyzed = ticketsAnalyzed;
        }
        
        public boolean isCacheHit() {
            return cacheHit;
        }
        
        public void setCacheHit(boolean cacheHit) {
            this.cacheHit = cacheHit;
        }
        
        public String getCompletedAt() {
            return completedAt;
        }
        
        public void setCompletedAt(String completedAt) {
            this.completedAt = completedAt;
        }
        
        public String getModelUsed() {
            return modelUsed;
        }
        
        public void setModelUsed(String modelUsed) {
            this.modelUsed = modelUsed;
        }
    }
    
    // Constructors
    public AnalysisResponse() {}
    
    public AnalysisResponse(UUID analysisId, String status, AnalysisReport report, AnalysisMetadata metadata) {
        this.analysisId = analysisId;
        this.status = status;
        this.report = report;
        this.metadata = metadata;
    }
    
    // Getters and Setters
    public UUID getAnalysisId() {
        return analysisId;
    }
    
    public void setAnalysisId(UUID analysisId) {
        this.analysisId = analysisId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public AnalysisReport getReport() {
        return report;
    }
    
    public void setReport(AnalysisReport report) {
        this.report = report;
    }
    
    public AnalysisMetadata getMetadata() {
        return metadata;
    }
    
    public void setMetadata(AnalysisMetadata metadata) {
        this.metadata = metadata;
    }
} 