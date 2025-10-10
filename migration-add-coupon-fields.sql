-- Migration script to add coupon fields to orders table
-- This script adds coupon tracking fields to the orders table

-- Add coupon_code column to orders table
ALTER TABLE orders ADD COLUMN IF NOT EXISTS coupon_code VARCHAR(255);

-- Add discount_amount column to orders table
ALTER TABLE orders ADD COLUMN IF NOT EXISTS discount_amount DECIMAL(12,2);

-- Add index on coupon_code for better query performance
CREATE INDEX IF NOT EXISTS idx_orders_coupon_code ON orders(coupon_code);

-- Verify the changes
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'orders' 
ORDER BY ordinal_position;
