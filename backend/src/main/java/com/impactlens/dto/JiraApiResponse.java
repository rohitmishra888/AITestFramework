package com.impactlens.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraApiResponse {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Issue {
        private String expand;
        private String id;
        private String self;
        private String key;
        private Fields fields;

        public String getExpand() { return expand; }
        public void setExpand(String expand) { this.expand = expand; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getSelf() { return self; }
        public void setSelf(String self) { this.self = self; }

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }

        public Fields getFields() { return fields; }
        public void setFields(Fields fields) { this.fields = fields; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fields {
        private String summary;
        private Object description; // Changed from String to Object to handle rich text
        private Status status;
        private Priority priority;
        private User assignee;
        private User reporter;
        private User creator;
        private String created;
        private String updated;
        private CommentWrapper comment;
        private List<Object> attachment;

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        public Object getDescription() { return description; }
        public void setDescription(Object description) { this.description = description; }

        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }

        public Priority getPriority() { return priority; }
        public void setPriority(Priority priority) { this.priority = priority; }

        public User getAssignee() { return assignee; }
        public void setAssignee(User assignee) { this.assignee = assignee; }

        public User getReporter() { return reporter; }
        public void setReporter(User reporter) { this.reporter = reporter; }

        public User getCreator() { return creator; }
        public void setCreator(User creator) { this.creator = creator; }

        public String getCreated() { return created; }
        public void setCreated(String created) { this.created = created; }

        public String getUpdated() { return updated; }
        public void setUpdated(String updated) { this.updated = updated; }

        public CommentWrapper getComment() { return comment; }
        public void setComment(CommentWrapper comment) { this.comment = comment; }

        public List<Object> getAttachment() { return attachment; }
        public void setAttachment(List<Object> attachment) { this.attachment = attachment; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
        private String self;
        private String description;
        private String iconUrl;
        private String name;
        private String id;
        private StatusCategory statusCategory;

        public String getSelf() { return self; }
        public void setSelf(String self) { this.self = self; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIconUrl() { return iconUrl; }
        public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public StatusCategory getStatusCategory() { return statusCategory; }
        public void setStatusCategory(StatusCategory statusCategory) { this.statusCategory = statusCategory; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatusCategory {
        private String self;
        private int id;
        private String key;
        private String colorName;
        private String name;

        public String getSelf() { return self; }
        public void setSelf(String self) { this.self = self; }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }

        public String getColorName() { return colorName; }
        public void setColorName(String colorName) { this.colorName = colorName; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Priority {
        private String self;
        private String iconUrl;
        private String name;
        private String id;

        public String getSelf() { return self; }
        public void setSelf(String self) { this.self = self; }

        public String getIconUrl() { return iconUrl; }
        public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String self;
        private String accountId;
        private String displayName;
        private boolean active;
        private String timeZone;
        private String accountType;

        public String getSelf() { return self; }
        public void setSelf(String self) { this.self = self; }

        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public String getTimeZone() { return timeZone; }
        public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentWrapper {
        private List<Comment> comments;
        private String self;
        private int maxResults;
        private int total;
        private int startAt;

        public List<Comment> getComments() { return comments; }
        public void setComments(List<Comment> comments) { this.comments = comments; }

        public String getSelf() { return self; }
        public void setSelf(String self) { this.self = self; }

        public int getMaxResults() { return maxResults; }
        public void setMaxResults(int maxResults) { this.maxResults = maxResults; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }

        public int getStartAt() { return startAt; }
        public void setStartAt(int startAt) { this.startAt = startAt; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Comment {
        private String self;
        private String id;
        private String body;
        private String created;
        private String updated;
        private User author;
        private User updateAuthor;

        public String getSelf() { return self; }
        public void setSelf(String self) { this.self = self; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }

        public String getCreated() { return created; }
        public void setCreated(String created) { this.created = created; }

        public String getUpdated() { return updated; }
        public void setUpdated(String updated) { this.updated = updated; }

        public User getAuthor() { return author; }
        public void setAuthor(User author) { this.author = author; }

        public User getUpdateAuthor() { return updateAuthor; }
        public void setUpdateAuthor(User updateAuthor) { this.updateAuthor = updateAuthor; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchResponse {
        private String expand;
        private int startAt;
        private int maxResults;
        private int total;
        private List<Issue> issues;

        public String getExpand() { return expand; }
        public void setExpand(String expand) { this.expand = expand; }

        public int getStartAt() { return startAt; }
        public void setStartAt(int startAt) { this.startAt = startAt; }

        public int getMaxResults() { return maxResults; }
        public void setMaxResults(int maxResults) { this.maxResults = maxResults; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }

        public List<Issue> getIssues() { return issues; }
        public void setIssues(List<Issue> issues) { this.issues = issues; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentsResponse {
        private int startAt;
        private int maxResults;
        private int total;
        private List<Comment> comments;

        public int getStartAt() { return startAt; }
        public void setStartAt(int startAt) { this.startAt = startAt; }

        public int getMaxResults() { return maxResults; }
        public void setMaxResults(int maxResults) { this.maxResults = maxResults; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }

        public List<Comment> getComments() { return comments; }
        public void setComments(List<Comment> comments) { this.comments = comments; }
    }
} 