CREATE TABLE tb_category (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(255) NOT NULL,
created_at TIMESTAMP WITHOUT TIME ZONE,
updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_category_name
    ON tb_category (name);


CREATE TABLE tb_product (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(255) NOT NULL,
description TEXT,
price DOUBLE PRECISION,
img_url VARCHAR(255),
date TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX idx_product_name
    ON tb_product (name);
