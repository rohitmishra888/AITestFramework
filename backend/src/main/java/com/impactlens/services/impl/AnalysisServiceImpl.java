package com.impactlens.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.impactlens.dto.AnalysisRequest;
import com.impactlens.dto.AnalysisResponse;
import com.impactlens.entities.JiraTicket;
import com.impactlens.repositories.JiraTicketRepository;
import com.impactlens.services.AnalysisService;
import com.impactlens.services.JiraService;
import com.impactlens.services.OpenAIService;

@Service
public class AnalysisServiceImpl implements AnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceImpl.class);
    
    @Autowired
    private JiraService jiraService;
    
    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private JiraTicketRepository jiraTicketRepository;
    
    @Override
   // @Cacheable(value = "analysis", key = "#request.ticketId + ':' + #request.options.hashCode()")
    public AnalysisResponse analyzeTicket(AnalysisRequest request, Authentication authentication) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting analysis for ticket: {}", request.getTicketId());
        
        try {
            // Step 1: Fetch or retrieve ticket data
            JiraTicket ticket = getTicketData(request.getTicketId());
            
            // Step 2: Extract keywords for related ticket search
            List<String> keywords = openAIService.extractKeywords(ticket);
            
            // Step 3: Find related tickets
            List<JiraTicket> relatedTickets = findRelatedTickets(ticket, keywords, request.getOptions());
            
            // Step 4: Perform gap analysis
            AnalysisResponse.GapAnalysis gapAnalysis = performGapAnalysis(ticket, relatedTickets);
            
            // Step 5: Generate regression testing recommendations
            List<AnalysisResponse.RegressionArea> regressionAreas = generateRegressionAreas(ticket, relatedTickets);
            
            // Step 6: Create comprehensive report
            AnalysisResponse response = buildAnalysisResponse(
                request.getTicketId(), 
                ticket, 
                relatedTickets, 
                gapAnalysis, 
                regressionAreas,
                System.currentTimeMillis() - startTime
            );
            
            logger.info("Analysis completed for ticket: {} in {}ms", 
                request.getTicketId(), response.getMetadata().getProcessingTime());
            
            return response;
            
        } catch (Exception e) {
            logger.error("Analysis failed for ticket: {}", request.getTicketId(), e);
            throw new RuntimeException("Analysis failed", e);
        }
    }
    
    @Override
    public AnalysisResponse getAnalysisStatus(UUID analysisId, Authentication authentication) {
        // For now, return a simple status response
        // In a real implementation, this would check the status of an async analysis
        AnalysisResponse response = new AnalysisResponse();
        response.setAnalysisId(analysisId);
        response.setStatus("completed");
        return response;
    }
    
    @Override
    public Iterable<AnalysisResponse> getAnalysisHistory(int page, int size, Authentication authentication) {
        // For now, return empty list
        // In a real implementation, this would query the database for analysis history
        return new ArrayList<>();
    }
    
    @Override
    public void deleteAnalysis(UUID analysisId, Authentication authentication) {
        // For now, just log the deletion
        // In a real implementation, this would delete from the database
        logger.info("Deleting analysis: {}", analysisId);
    }
    
    private JiraTicket getTicketData(String ticketId) {
        // First try to get from cache/database
        Optional<JiraTicket> cachedTicket = jiraTicketRepository.findByTicketKey(ticketId);
        
        if (cachedTicket.isPresent()) {
            logger.info("Found ticket {} in cache", ticketId);
            return cachedTicket.get();
        }
        
        // If not in cache, fetch from Jira
        logger.info("Fetching ticket {} from Jira", ticketId);
        JiraTicket ticket = jiraService.fetchTicket(ticketId);
        jiraTicketRepository.save(ticket);
        
        return ticket;
    }
    
    private List<JiraTicket> findRelatedTickets(JiraTicket sourceTicket, List<String> keywords, AnalysisRequest.AnalysisOptions options) {
        logger.info("Searching for related tickets using {} keywords", keywords.size());
        
        // Search for tickets containing the keywords
        List<JiraTicket> candidates = new ArrayList<>();
        for (String keyword : keywords) {
            candidates.addAll(jiraTicketRepository.searchTickets(keyword));
        }
        
        // Remove duplicates and the source ticket
        candidates = candidates.stream()
            .distinct()
            .filter(ticket -> !ticket.getTicketKey().equals(sourceTicket.getTicketKey()))
            .collect(Collectors.toList());
        
        // Analyze relevance for each candidate
        List<JiraTicket> relatedTickets = new ArrayList<>();
        for (JiraTicket candidate : candidates) {
            double relevanceScore = openAIService.calculateRelevanceScore(sourceTicket, candidate);
            
            if (relevanceScore >= options.getMinRelevanceScore()) {
                relatedTickets.add(candidate);
                
                if (relatedTickets.size() >= options.getMaxRelatedTickets()) {
                    break;
                }
            }
        }
        
        logger.info("Found {} related tickets", relatedTickets.size());
        return relatedTickets;
    }
    
    private AnalysisResponse.GapAnalysis performGapAnalysis(JiraTicket sourceTicket, List<JiraTicket> relatedTickets) {
        logger.info("Performing gap analysis for ticket: {}", sourceTicket.getTicketKey());
        
        // Use OpenAI to perform gap analysis
        return openAIService.performGapAnalysis(sourceTicket, relatedTickets);
    }
    
    private List<AnalysisResponse.RegressionArea> generateRegressionAreas(JiraTicket sourceTicket, List<JiraTicket> relatedTickets) {
        logger.info("Generating regression testing areas for ticket: {}", sourceTicket.getTicketKey());
        
        // Use OpenAI to generate regression testing recommendations
        return openAIService.generateRegressionAreas(sourceTicket, relatedTickets);
    }
    
    private AnalysisResponse buildAnalysisResponse(
            String ticketId, 
            JiraTicket ticket, 
            List<JiraTicket> relatedTickets, 
            AnalysisResponse.GapAnalysis gapAnalysis,
            List<AnalysisResponse.RegressionArea> regressionAreas,
            long processingTime) {
        
        AnalysisResponse response = new AnalysisResponse();
        response.setAnalysisId(UUID.randomUUID());
        response.setStatus("completed");
        
        // Build report
        AnalysisResponse.AnalysisReport report = new AnalysisResponse.AnalysisReport();
        report.setSummary(generateSummary(ticket, relatedTickets));
        report.setGapsIdentified(Arrays.asList(gapAnalysis));
        report.setRelatedTickets(convertToRelatedTickets(relatedTickets));
        report.setRegressionAreas(regressionAreas);
        report.setRecommendations(generateRecommendations(ticket, relatedTickets));
        
        response.setReport(report);
        
        // Build metadata
        AnalysisResponse.AnalysisMetadata metadata = new AnalysisResponse.AnalysisMetadata();
        metadata.setProcessingTime(processingTime);
        metadata.setTicketsAnalyzed(relatedTickets.size() + 1);
        metadata.setCacheHit(false); // TODO: Implement proper cache hit detection
        metadata.setCompletedAt(java.time.LocalDateTime.now().toString());
        metadata.setModelUsed("gpt-4");
        
        response.setMetadata(metadata);
        
        return response;
    }
    
    private String generateSummary(JiraTicket ticket, List<JiraTicket> relatedTickets) {
        return String.format("Analysis of ticket %s identified %d related tickets with potential impacts on %s areas.",
            ticket.getTicketKey(), relatedTickets.size(), ticket.getSummary());
    }
    
    private List<AnalysisResponse.RelatedTicket> convertToRelatedTickets(List<JiraTicket> tickets) {
        return tickets.stream().map(ticket -> {
            AnalysisResponse.RelatedTicket relatedTicket = new AnalysisResponse.RelatedTicket();
            relatedTicket.setTicketKey(ticket.getTicketKey());
            relatedTicket.setSummary(ticket.getSummary());
            relatedTicket.setStatus(ticket.getStatus());
            relatedTicket.setPriority(ticket.getPriority());
            relatedTicket.setRelevanceScore(0.8); // TODO: Calculate actual relevance score
            relatedTicket.setRelationshipType("similar");
            relatedTicket.setImpactDescription("Potential impact on similar functionality");
            return relatedTicket;
        }).collect(Collectors.toList());
    }
    
    private List<String> generateRecommendations(JiraTicket ticket, List<JiraTicket> relatedTickets) {
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Review related tickets for potential conflicts");
        recommendations.add("Consider impact on existing functionality");
        recommendations.add("Plan regression testing for affected areas");
        recommendations.add("Update documentation if needed");
        return recommendations;
    }
} 