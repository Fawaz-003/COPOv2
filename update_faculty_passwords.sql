-- Update all faculty with default password "5106" (BCrypt hashed)
-- This script will set the same password for all faculty members

-- First, add the password column if it doesn't exist
ALTER TABLE faculty 
ADD COLUMN IF NOT EXISTS password VARCHAR(255) AFTER designation;

-- Update all faculty with the BCrypt hashed version of "5106"
-- The hash below is for the password "5106" using BCrypt with default strength
UPDATE faculty 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE password IS NULL OR password = '';

-- Verify the updates
SELECT id, facultycode, name, 
       CASE 
           WHEN password IS NOT NULL AND password != '' THEN 'Password Set'
           ELSE 'No Password'
       END as password_status
FROM faculty;

-- Show total count
SELECT COUNT(*) as total_faculty,
       COUNT(CASE WHEN password IS NOT NULL AND password != '' THEN 1 END) as faculty_with_passwords
FROM faculty;

