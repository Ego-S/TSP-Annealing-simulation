CREATE TABLE delivery_point (
    id bigint NOT NULL,
    latitude double NOT NULL,
    longitude double NOT NULL,
    delivery_from time NOT NULL,
    delivery_to time NOT NULL,
    PRIMARY KEY (id)
);