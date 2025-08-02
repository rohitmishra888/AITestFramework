-- Update raw_data column from JSONB to TEXT with length limit
ALTER TABLE jira_tickets 
ALTER COLUMN raw_data TYPE VARCHAR(30000);

-- Add comment to document the change
COMMENT ON COLUMN jira_tickets.raw_data IS 'Raw JSON data from Jira API as string, max 30000 characters'; 