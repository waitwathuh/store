-- Create index for customer table
CREATE INDEX idx_customer_name ON customer (name);

-- Create indexes for order_product table
CREATE INDEX idx_order_product_order_id ON order_product (order_id);
CREATE INDEX idx_order_product_product_id ON order_product (product_id);
