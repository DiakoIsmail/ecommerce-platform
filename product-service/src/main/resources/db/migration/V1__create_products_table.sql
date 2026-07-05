CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description VARCHAR(1000),
                          price NUMERIC(10, 2) NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT now()
);