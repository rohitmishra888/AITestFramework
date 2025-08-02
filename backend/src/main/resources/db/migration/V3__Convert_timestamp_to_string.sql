-- Convert TIMESTAMP columns to VARCHAR for LocalDateTime fields that are now stored as strings
-- This migration aligns the database schema with the code changes

-- Update jira_tickets table
ALTER TABLE jira_tickets 
    ALTER COLUMN created_at TYPE VARCHAR(50),
    ALTER COLUMN updated_at TYPE VARCHAR(50),
    ALTER COLUMN last_synced_at TYPE VARCHAR(50),
    ALTER COLUMN ttl_expires_at TYPE VARCHAR(50);

-- Update analysis_results table
ALTER TABLE analysis_results 
    ALTER COLUMN created_at TYPE VARCHAR(50);

-- Update related_tickets table
ALTER TABLE related_tickets 
    ALTER COLUMN created_at TYPE VARCHAR(50);

-- Update webhook_events table
ALTER TABLE webhook_events 
    ALTER COLUMN processed_at TYPE VARCHAR(50),
    ALTER COLUMN created_at TYPE VARCHAR(50);

-- Update user_preferences table
ALTER TABLE user_preferences 
    ALTER COLUMN created_at TYPE VARCHAR(50),
    ALTER COLUMN updated_at TYPE VARCHAR(50);

-- Update refresh_tokens table
ALTER TABLE refresh_tokens 
    ALTER COLUMN expires_at TYPE VARCHAR(50),
    ALTER COLUMN created_at TYPE VARCHAR(50);

-- Update users table
ALTER TABLE users 
    ALTER COLUMN created_at TYPE VARCHAR(50),
    ALTER COLUMN updated_at TYPE VARCHAR(50); 