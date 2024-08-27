CREATE TABLE pharmacy_representatives (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    cnpj VARCHAR(20) NOT NULL UNIQUE,
    corporate_reason VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE laboratories (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    corporate_reason VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE laboratory_members (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    laboratory_id BIGINT NOT NULL,
    FOREIGN KEY (laboratory_id) REFERENCES laboratories(id)
);

CREATE TABLE entry_exit_records (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    pharmacy_representative_id BIGINT,
    laboratory_member_id BIGINT,
    checkin_time TIMESTAMP NOT NULL,
    checkout_time TIMESTAMP,
    event_segment VARCHAR(10) NOT NULL,
    FOREIGN KEY (pharmacy_representative_id) REFERENCES pharmacy_representatives(id),
    FOREIGN KEY (laboratory_member_id) REFERENCES laboratory_members(id)
);