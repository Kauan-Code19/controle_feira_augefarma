CREATE TABLE Entry_Exit_Records (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    client_id BIGINT,
    laboratory_id BIGINT,
    checkin_time TIMESTAMP NOT NULL,
    checkout_time TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES Clients(id),
    FOREIGN KEY (laboratory_id) REFERENCES Laboratories(id)
);