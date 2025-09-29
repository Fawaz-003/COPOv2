-- Add is_deleted column to departments table
ALTER TABLE departments ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Update existing departments to be active (not deleted)
UPDATE departments SET is_deleted = FALSE WHERE is_deleted IS NULL;
