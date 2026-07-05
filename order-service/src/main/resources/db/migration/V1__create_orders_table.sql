CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        product_id BIGINT NOT NULL,
                        quantity INTEGER NOT NULL,
                        total_price NUMERIC(10, 2) NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT now()
);