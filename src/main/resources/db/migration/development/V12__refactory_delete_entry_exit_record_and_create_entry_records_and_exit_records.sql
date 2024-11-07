CREATE TABLE participants (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE  -- Atributos comuns
);

ALTER TABLE pharmacy_representatives
    DROP COLUMN name,
    DROP COLUMN cpf,
    ADD COLUMN participant_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_participant FOREIGN KEY (participant_id) REFERENCES participants(id);

ALTER TABLE laboratory_members
    DROP COLUMN name,
    DROP COLUMN cpf,
    ADD COLUMN participant_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_participant FOREIGN KEY (participant_id) REFERENCES participants(id);

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