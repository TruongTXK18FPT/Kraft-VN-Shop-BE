-- Migration script to remove fullName and phone columns from addresses table
-- This script removes redundant fields since User entity already has name and phone

-- Remove fullName column from addresses table
ALTER TABLE addresses DROP COLUMN IF EXISTS full_name;

-- Remove phone column from addresses table  
ALTER TABLE addresses DROP COLUMN IF EXISTS phone;

-- Optional: Add index on user_id if not already exists (for better query performance)
CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id);

-- Optional: Add index on is_default for faster default address queries
CREATE INDEX IF NOT EXISTS idx_addresses_is_default ON addresses(is_default);

-- Verify the changes
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'addresses' 
ORDER BY ordinal_position;
