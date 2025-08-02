package com.impactlens.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.impactlens.config.OpenAIConfig;
import com.impactlens.dto.AnalysisResponse;
import com.impactlens.dto.OpenAIRequest;
import com.impactlens.dto.OpenAIResponse;
import com.impactlens.entities.JiraTicket;
import com.impactlens.services.OpenAIService;

@Service
public class OpenAIServiceImpl implements OpenAIService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAIServiceImpl.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    @Autowired
    private OpenAIConfig openAIConfig;
    
    @Autowired
    private RestTemplate openaiRestTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public List<String> extractKeywords(JiraTicket ticket) {
        logger.info("Extracting keywords from ticket: {}", ticket.getTicketKey());
        
        String prompt = String.format(
            "Extract 5-8 key technical keywords from this Jira ticket that would be useful for finding related tickets. " +
            "Focus on technical terms, technologies, features, and business concepts. " +
            "Return only a JSON array of strings, no other text.\n\n" +
            "Ticket Summary: %s\n" +
            "Ticket Description: %s",
            ticket.getSummary(),
            ticket.getDescription()
        );
        
        try {
            String response = callOpenAI(prompt);
            return parseKeywordsFromResponse(response);
        } catch (Exception e) {
            logger.error("Error extracting keywords for ticket {}: {}", ticket.getTicketKey(), e.getMessage(), e);
            return Arrays.asList("feature", "development", "bug", "enhancement", "testing");
        }
    }
    
    @Override
    public double calculateRelevanceScore(JiraTicket sourceTicket, JiraTicket relatedTicket) {
        logger.info("Calculating relevance score between {} and {}", 
            sourceTicket.getTicketKey(), relatedTicket.getTicketKey());
        
        String prompt = String.format(
            "Calculate a relevance score between 0.0 and 1.0 for how related these two Jira tickets are. " +
            "Consider technical similarity, business impact, and functional overlap. " +
            "Return only a number between 0.0 and 1.0, no other text.\n\n" +
            "Source Ticket:\nSummary: %s\nDescription: %s\n\n" +
            "Related Ticket:\nSummary: %s\nDescription: %s",
            sourceTicket.getSummary(),
            sourceTicket.getDescription(),
            relatedTicket.getSummary(),
            relatedTicket.getDescription()
        );
        
        try {
            String response = callOpenAI(prompt);
            return parseScoreFromResponse(response);
        } catch (Exception e) {
            logger.error("Error calculating relevance score: {}", e.getMessage(), e);
            return 0.5; // Default score
        }
    }
    
    @Override
    public AnalysisResponse.GapAnalysis performGapAnalysis(JiraTicket sourceTicket, List<JiraTicket> relatedTickets) {
        logger.info("Performing gap analysis for ticket: {}", sourceTicket.getTicketKey());
        
        StringBuilder relatedTicketsInfo = new StringBuilder();
        for (JiraTicket ticket : relatedTickets) {
            relatedTicketsInfo.append(String.format("- %s: %s\n", ticket.getTicketKey(), ticket.getSummary()));
        }
        
        String prompt = String.format(
            "Perform a gap analysis for this Jira ticket by comparing it with related tickets. " +
            "Identify potential missing requirements, edge cases, or considerations. " +
            "Return a JSON object with the following structure:\n" +
            "{\n" +
            "  \"category\": \"string\",\n" +
            "  \"description\": \"string\",\n" +
            "  \"severity\": \"Low|Medium|High\",\n" +
            "  \"impact\": \"string\",\n" +
            "  \"suggestions\": [\"string1\", \"string2\", \"string3\"]\n" +
            "}\n\n" +
            "Source Ticket:\nSummary: %s\nDescription: %s\n\n" +
            "Related Tickets:\n%s",
            sourceTicket.getSummary(),
            sourceTicket.getDescription(),
            relatedTicketsInfo.toString()
        );
        
        try {
            String response = callOpenAI(prompt);
            return parseGapAnalysisFromResponse(response);
        } catch (Exception e) {
            logger.error("Error performing gap analysis: {}", e.getMessage(), e);
            return createDefaultGapAnalysis();
        }
    }
    
    @Override
    public List<AnalysisResponse.RegressionArea> generateRegressionAreas(JiraTicket sourceTicket, List<JiraTicket> relatedTickets) {
        logger.info("Generating regression areas for ticket: {}", sourceTicket.getTicketKey());
        
        StringBuilder relatedTicketsInfo = new StringBuilder();
        for (JiraTicket ticket : relatedTickets) {
            relatedTicketsInfo.append(String.format("- %s: %s\n", ticket.getTicketKey(), ticket.getSummary()));
        }
        
        String prompt = String.format(
            "Generate regression testing areas for this Jira ticket based on the related tickets. " +
            "Identify areas that might be affected by the changes. " +
            "Return a JSON array of objects with the following structure:\n" +
            "[\n" +
            "  {\n" +
            "    \"area\": \"string\",\n" +
            "    \"description\": \"string\",\n" +
            "    \"riskLevel\": \"Low|Medium|High\",\n" +
            "    \"testCases\": [\"string1\", \"string2\", \"string3\"],\n" +
            "    \"rationale\": \"string\"\n" +
            "  }\n" +
            "]\n\n" +
            "Source Ticket:\nSummary: %s\nDescription: %s\n\n" +
            "Related Tickets:\n%s",
            sourceTicket.getSummary(),
            sourceTicket.getDescription(),
            relatedTicketsInfo.toString()
        );
        
        try {
            String response = callOpenAI(prompt);
            return parseRegressionAreasFromResponse(response);
        } catch (Exception e) {
            logger.error("Error generating regression areas: {}", e.getMessage(), e);
            return createDefaultRegressionAreas();
        }
    }
    
    @Override
    public String generateSummary(JiraTicket ticket, List<JiraTicket> relatedTickets) {
        logger.info("Generating summary for ticket: {}", ticket.getTicketKey());
        
        StringBuilder relatedTicketsInfo = new StringBuilder();
        for (JiraTicket relatedTicket : relatedTickets) {
            relatedTicketsInfo.append(String.format("- %s: %s\n", relatedTicket.getTicketKey(), relatedTicket.getSummary()));
        }
        
        String prompt = String.format(
            "Generate a concise summary of the impact analysis for this Jira ticket. " +
            "Include the number of related tickets found and key areas of concern. " +
            "Keep it under 200 words.\n\n" +
            "Ticket: %s\nSummary: %s\nDescription: %s\n\n" +
            "Related Tickets Found:\n%s",
            ticket.getTicketKey(),
            ticket.getSummary(),
            ticket.getDescription(),
            relatedTicketsInfo.toString()
        );
        
        try {
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating summary: {}", e.getMessage(), e);
            return String.format(
                "Analysis of ticket %s identified %d related tickets. " +
                "Recommend thorough regression testing in affected areas.",
                ticket.getTicketKey(),
                relatedTickets.size()
            );
        }
    }
    
    private String callOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAIConfig.getApiKey());
        
        OpenAIRequest.Message message = new OpenAIRequest.Message("user", prompt);
        List<OpenAIRequest.Message> messages = Arrays.asList(message);
        
        OpenAIRequest.ChatCompletionRequest request = new OpenAIRequest.ChatCompletionRequest(
            openAIConfig.getModel(),
            messages,
            openAIConfig.getTemperature(),
            openAIConfig.getMaxTokens()
        );
        
        HttpEntity<OpenAIRequest.ChatCompletionRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<OpenAIResponse.ChatCompletionResponse> response = openaiRestTemplate.exchange(
            OPENAI_API_URL, HttpMethod.POST, entity, OpenAIResponse.ChatCompletionResponse.class);
        
        if (response.getBody() != null && 
            response.getBody().getChoices() != null && 
            !response.getBody().getChoices().isEmpty()) {
            return response.getBody().getChoices().get(0).getMessage().getContent();
        }
        
        throw new RuntimeException("No response from OpenAI API");
    }
    
    private List<String> parseKeywordsFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.isArray()) {
                List<String> keywords = new ArrayList<>();
                for (JsonNode node : jsonNode) {
                    keywords.add(node.asText());
                }
                return keywords;
            }
        } catch (Exception e) {
            logger.warn("Error parsing keywords from OpenAI response: {}", e.getMessage());
        }
        return Arrays.asList("feature", "development", "bug", "enhancement", "testing");
    }
    
    private double parseScoreFromResponse(String response) {
        try {
            return Double.parseDouble(response.trim());
        } catch (Exception e) {
            logger.warn("Error parsing score from OpenAI response: {}", e.getMessage());
            return 0.5;
        }
    }
    
    private AnalysisResponse.GapAnalysis parseGapAnalysisFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            AnalysisResponse.GapAnalysis gapAnalysis = new AnalysisResponse.GapAnalysis();
            
            gapAnalysis.setCategory(jsonNode.get("category").asText());
            gapAnalysis.setDescription(jsonNode.get("description").asText());
            gapAnalysis.setSeverity(jsonNode.get("severity").asText());
            gapAnalysis.setImpact(jsonNode.get("impact").asText());
            
            List<String> suggestions = new ArrayList<>();
            JsonNode suggestionsNode = jsonNode.get("suggestions");
            if (suggestionsNode.isArray()) {
                for (JsonNode suggestion : suggestionsNode) {
                    suggestions.add(suggestion.asText());
                }
            }
            gapAnalysis.setSuggestions(suggestions);
            
            return gapAnalysis;
        } catch (Exception e) {
            logger.warn("Error parsing gap analysis from OpenAI response: {}", e.getMessage());
            return createDefaultGapAnalysis();
        }
    }
    
    private List<AnalysisResponse.RegressionArea> parseRegressionAreasFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            List<AnalysisResponse.RegressionArea> areas = new ArrayList<>();
            
            if (jsonNode.isArray()) {
                for (JsonNode areaNode : jsonNode) {
                    AnalysisResponse.RegressionArea area = new AnalysisResponse.RegressionArea();
                    area.setArea(areaNode.get("area").asText());
                    area.setDescription(areaNode.get("description").asText());
                    area.setRiskLevel(areaNode.get("riskLevel").asText());
                    area.setRationale(areaNode.get("rationale").asText());
                    
                    List<String> testCases = new ArrayList<>();
                    JsonNode testCasesNode = areaNode.get("testCases");
                    if (testCasesNode.isArray()) {
                        for (JsonNode testCase : testCasesNode) {
                            testCases.add(testCase.asText());
                        }
                    }
                    area.setTestCases(testCases);
                    
                    areas.add(area);
                }
            }
            
            return areas;
        } catch (Exception e) {
            logger.warn("Error parsing regression areas from OpenAI response: {}", e.getMessage());
            return createDefaultRegressionAreas();
        }
    }
    
    private AnalysisResponse.GapAnalysis createDefaultGapAnalysis() {
        AnalysisResponse.GapAnalysis gapAnalysis = new AnalysisResponse.GapAnalysis();
        gapAnalysis.setCategory("Requirements");
        gapAnalysis.setDescription("Unable to perform gap analysis due to API error");
        gapAnalysis.setSeverity("Medium");
        gapAnalysis.setImpact("Manual review recommended");
        gapAnalysis.setSuggestions(Arrays.asList("Review requirements manually", "Check for edge cases", "Verify acceptance criteria"));
        return gapAnalysis;
    }
    
    private List<AnalysisResponse.RegressionArea> createDefaultRegressionAreas() {
        List<AnalysisResponse.RegressionArea> areas = new ArrayList<>();
        
        AnalysisResponse.RegressionArea area = new AnalysisResponse.RegressionArea();
        area.setArea("General Testing");
        area.setDescription("Unable to generate specific regression areas due to API error");
        area.setRiskLevel("Medium");
        area.setTestCases(Arrays.asList("Perform general regression testing", "Test core functionality", "Verify data integrity"));
        area.setRationale("Manual regression testing recommended");
        areas.add(area);
        
        return areas;
    }
} 