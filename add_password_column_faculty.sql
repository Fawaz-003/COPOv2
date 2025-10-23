-- Add password column to faculty table
-- This script adds a password column to store BCrypt hashed passwords

ALTER TABLE faculty 
ADD COLUMN password VARCHAR(255) AFTER designation;

-- Optional: Add a comment to the column
ALTER TABLE faculty 
MODIFY COLUMN password VARCHAR(255) COMMENT 'BCrypt hashed password for faculty authentication';

-- Optional: Set a default empty password for existing records
-- UPDATE faculty SET password = '' WHERE password IS NULL;

-- Verify the column was added
SELECT * FROM faculty LIMIT 5;

