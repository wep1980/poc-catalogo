CREATE TABLE tb_role (
id BIGSERIAL PRIMARY KEY,
authority VARCHAR(255) NOT NULL
);
CREATE INDEX idx_role_authority
    ON tb_role (authority);


CREATE TABLE tb_user (
id BIGSERIAL PRIMARY KEY,
first_name VARCHAR(255),
last_name VARCHAR(255),
email VARCHAR(255) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL
);
CREATE INDEX idx_user_email
    ON tb_user (email);
