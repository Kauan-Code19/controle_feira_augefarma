CREATE TABLE Laboratories (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    corporate_reason VARCHAR(160) NOT NULL UNIQUE
);