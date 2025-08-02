package com.impactlens.dto;

import java.util.List;

public class JiraApiResponse {
    
    public static class Issue {
        private String id;
        private String key;
        private Fields fields;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        
        public Fields getFields() { return fields; }
        public void setFields(Fields fields) { this.fields = fields; }
    }
    
    public static class Fields {
        private String summary;
        private String description;
        private Status status;
        private Priority priority;
        private User assignee;
        private User reporter;
        private String created;
        private String updated;
        private List<Comment> comment;
        
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
        
        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }
        
        public User getAssignee() { return assignee; }
        public void setAssignee(User assignee) { this.assignee = assignee; }
        
        public User getReporter() { return reporter; }
        public void setReporter(User reporter) { this.reporter = reporter; }
        
        public String getCreated() { return created; }
        public void setCreated(String created) { this.created = created; }
        
        public String getUpdated() { return updated; }
        public void setUpdated(String updated) { this.updated = updated; }
        
        public List<Comment> getComment() { return comment; }
        public void setComment(List<Comment> comment) { this.comment = comment; }
    }
    
    public static class Status {
        private String name;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    public static class Priority {
        private String name;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    public static class User {
        private String displayName;
        private String emailAddress;
        
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public String getEmailAddress() { return emailAddress; }
        public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    }
    
    public static class Comment {
        private String body;
        private String created;
        private User author;
        
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        
        public String getCreated() { return created; }
        public void setCreated(String created) { this.created = created; }
        
        public User getAuthor() { return author; }
        public void setAuthor(User author) { this.author = author; }
    }
    
    public static class SearchResponse {
        private List<Issue> issues;
        private int total;
        
        public List<Issue> getIssues() { return issues; }
        public void setIssues(List<Issue> issues) { this.issues = issues; }
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }
    
    public static class CommentsResponse {
        private List<Comment> comments;
        private int total;
        
        public List<Comment> getComments() { return comments; }
        public void setComments(List<Comment> comments) { this.comments = comments; }
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }
} 