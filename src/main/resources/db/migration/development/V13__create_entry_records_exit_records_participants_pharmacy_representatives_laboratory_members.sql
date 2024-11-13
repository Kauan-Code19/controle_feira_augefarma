CREATE TABLE participants (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE  -- Atributos comuns
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
    FOREIGN KEY (laboratory_id) REFERENCES Laboratories(id),
    FOREIGN KEY (id) REFERENCES participants(id)
);

-- Criando a tabela entry_records
CREATE TABLE entry_records (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    participant_id BIGINT NOT NULL,
    checkin_time TIMESTAMP NOT NULL,
    event_segment VARCHAR(10) NOT NULL,

    -- Definindo chave estrangeira para a tabela Participant sem ON DELETE CASCADE
    FOREIGN KEY (participant_id) REFERENCES Participants(id)
);

-- Criando a tabela exit_records
CREATE TABLE Exit_Records (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    participant_id BIGINT NOT NULL,
    checkout_time TIMESTAMP NOT NULL,
    event_segment VARCHAR(10) NOT NULL,

    -- Definindo chave estrangeira para a tabela Participant sem ON DELETE CASCADE
    FOREIGN KEY (participant_id) REFERENCES Participants(id)
);