-- Create product table
CREATE TABLE product (
                          id BIGSERIAL PRIMARY KEY,
                          description VARCHAR(255) NOT NULL
);

-- Create order_product table
CREATE TABLE order_product (
                          order_id BIGINT NOT NULL,
                          product_id BIGINT NOT NULL,
                          PRIMARY KEY (order_id, product_id),
                          CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES "order" (id) ON DELETE CASCADE,
                          CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);
