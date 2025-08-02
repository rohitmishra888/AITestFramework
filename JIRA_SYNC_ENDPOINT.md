# JIRA Sync Endpoint Documentation

## Overview

The JIRA sync endpoint allows you to fetch all JIRA tickets and save them to the database, updating existing tickets and adding new ones. This provides a comprehensive way to keep your local database in sync with JIRA.

## Endpoints

### 1. Sync All JIRA Tickets

**POST** `/api/jira/sync`

Fetches JIRA tickets based on JQL query and saves them to the database.

#### Parameters:
- `jql` (optional, default: "ORDER BY updated DESC"): JQL query to filter tickets
- `maxResults` (optional, default: 100): Maximum number of tickets to fetch

#### Example Request:
```bash
curl -X POST "http://localhost:8080/api/jira/sync?jql=project%20%3D%20PROJ&maxResults=50"
```

#### Example Response:
```json
{
  "totalTicketsFetched": 45,
  "newTicketsAdded": 12,
  "existingTicketsUpdated": 33,
  "failedTickets": 0,
  "failedTicketKeys": [],
  "syncStartTime": "2024-01-15T10:30:00",
  "syncEndTime": "2024-01-15T10:32:15",
  "jql": "project = PROJ",
  "status": "COMPLETED",
  "errorMessage": null,
  "durationInSeconds": 135
}
```

### 2. Sync All Tickets (Default)

**POST** `/api/jira/sync/all`

Syncs all tickets with default parameters (ORDER BY updated DESC, maxResults=100).

### 3. Sync Recent Tickets

**POST** `/api/jira/sync/recent`

Syncs tickets updated in the last N days.

#### Parameters:
- `days` (optional, default: 30): Number of days to look back

#### Example Request:
```bash
curl -X POST "http://localhost:8080/api/jira/sync/recent?days=7"
```

## Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `totalTicketsFetched` | int | Total number of tickets fetched from JIRA |
| `newTicketsAdded` | int | Number of new tickets added to database |
| `existingTicketsUpdated` | int | Number of existing tickets updated |
| `failedTickets` | int | Number of tickets that failed to process |
| `failedTicketKeys` | List<String> | List of ticket keys that failed |
| `syncStartTime` | LocalDateTime | When the sync started |
| `syncEndTime` | LocalDateTime | When the sync completed |
| `jql` | String | The JQL query used |
| `status` | String | Status: "COMPLETED", "FAILED", "IN_PROGRESS" |
| `errorMessage` | String | Error message if sync failed |
| `durationInSeconds` | long | Total duration of sync in seconds |

## JQL Examples

### Sync all tickets:
```
ORDER BY updated DESC
```

### Sync tickets from specific project:
```
project = PROJ ORDER BY updated DESC
```

### Sync tickets updated in last 7 days:
```
updated >= -7d ORDER BY updated DESC
```

### Sync tickets with specific status:
```
status = "In Progress" ORDER BY updated DESC
```

### Sync tickets assigned to specific user:
```
assignee = "john.doe@company.com" ORDER BY updated DESC
```

## Error Handling

The endpoint handles various error scenarios:

1. **Invalid JIRA Configuration**: Returns error if JIRA credentials are not configured
2. **Network Errors**: Handles connection timeouts and network issues
3. **Individual Ticket Errors**: Continues processing other tickets if one fails
4. **API Rate Limits**: Respects JIRA API rate limits

## Performance Considerations

- The endpoint fetches tickets in batches (controlled by `maxResults`)
- Existing tickets are efficiently identified using database queries
- Bulk save operations are used for better performance
- Failed tickets are logged but don't stop the entire sync process

## Security

- Uses Basic Authentication with JIRA API token
- Validates JIRA configuration before making requests
- Logs sensitive operations for audit purposes

## Monitoring

The endpoint provides comprehensive logging:
- Sync start/completion times
- Number of tickets processed
- Error details for failed tickets
- Performance metrics

## Usage Examples

### Sync all tickets from a specific project:
```bash
curl -X POST "http://localhost:8080/api/jira/sync" \
  -H "Content-Type: application/json" \
  -d '{"jql": "project = IMPACT", "maxResults": 200}'
```

### Sync recent tickets (last 14 days):
```bash
curl -X POST "http://localhost:8080/api/jira/sync/recent?days=14"
```

### Sync tickets with specific criteria:
```bash
curl -X POST "http://localhost:8080/api/jira/sync" \
  -H "Content-Type: application/json" \
  -d '{"jql": "project = IMPACT AND status = \"In Progress\" ORDER BY updated DESC", "maxResults": 50}'
``` 