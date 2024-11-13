CREATE TABLE participants (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE pharmacy_representatives (
    id BIGINT PRIMARY KEY,
    cnpj VARCHAR(20) NOT NULL UNIQUE,
    corporate_reason VARCHAR(160) NOT NULL UNIQUE,
    FOREIGN KEY (id) REFERENCES participants(id)
);

CREATE TABLE laboratory_members (
    id BIGINT PRIMARY KEY,
    laboratory_id BIGINT NOT NULL,
    FOREIGN KEY (laboratory_id) REFERENCES laboratories(id),
    FOREIGN KEY (id) REFERENCES participants(id)
);

CREATE TABLE entry_records (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    participant_id BIGINT NOT NULL,
    checkin_time TIMESTAMP NOT NULL,
    event_segment VARCHAR(10) NOT NULL,

    FOREIGN KEY (participant_id) REFERENCES participants(id)
);

CREATE TABLE exit_records (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    participant_id BIGINT NOT NULL,
    checkout_time TIMESTAMP NOT NULL,
    event_segment VARCHAR(10) NOT NULL,

    FOREIGN KEY (participant_id) REFERENCES participants(id)
);