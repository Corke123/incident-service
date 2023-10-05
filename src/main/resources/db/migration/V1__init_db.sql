CREATE TABLE type
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name      VARCHAR(256) NOT NULL,
    parent_id UUID,
    CONSTRAINT fk_parent_type FOREIGN KEY (parent_id) REFERENCES type (id)
);

INSERT INTO type (id, name, parent_id)
VALUES ('1f00eabe-0c54-4efe-ab6f-a4d1f78f9951', 'Nesreće', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('a95d0859-169c-478f-8cd1-c6992f85262c', 'Krađe', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('1d0c3d57-c074-4ed5-93b4-18997ef011a2', 'Vandalizam', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('e32195df-92d5-48cd-9a9e-2586449322d3', 'Zlostavljanje', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('3ed091f9-1a8d-4fbe-a1bc-9bb7b1db7e6c', 'Ekološki problemi', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('d68765fb-1224-4401-a765-ea0cdf8c1db8', 'Opasnosti po bezbednost', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('db7a2c6c-fcc1-4267-a4a4-81213197a05e', 'Prijave buke', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('07c1af1a-3c78-48a2-b923-26191849f02a', 'Problemi javnog zdravlja', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('73a65d20-25ec-42ec-aea9-114b63231bac', 'Problemi sa infrastrukturom', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('873fc9d4-db0f-4722-929a-fc054bb97a0a', 'Sumnjive aktivnosti', NULL);
INSERT INTO type (id, name, parent_id)
VALUES ('1d0a3778-dd82-4e16-9e43-153064d84348', 'Ostalo', NULL);

INSERT INTO type (id, name, parent_id)
VALUES ('1cef1aba-daa8-43e2-8a80-bce8f6d640db', 'Saobraćajne nesreće', '1f00eabe-0c54-4efe-ab6f-a4d1f78f9951');
INSERT INTO type (id, name, parent_id)
VALUES ('d7f76951-5a96-414e-9b35-c9ecd8acca11', 'Radne nesreće', '1f00eabe-0c54-4efe-ab6f-a4d1f78f9951');

INSERT INTO type (id, name, parent_id)
VALUES ('264e7a38-bd92-44db-8e44-3171d40d7177', 'Oštećenje ili uništavanje imovine',
        '1d0c3d57-c074-4ed5-93b4-18997ef011a2');
INSERT INTO type (id, name, parent_id)
VALUES ('97fb1e50-2da2-415f-b16a-141451e0c90a', 'Grafiti', '1d0c3d57-c074-4ed5-93b4-18997ef011a2');

INSERT INTO type (id, name, parent_id)
VALUES ('02446cf4-b9a2-457e-8570-aee883a0a747', 'Verbalno', 'e32195df-92d5-48cd-9a9e-2586449322d3');
INSERT INTO type (id, name, parent_id)
VALUES ('feaf72ec-f04e-4670-bba5-8e694ad376ed', 'Fizičko ', 'e32195df-92d5-48cd-9a9e-2586449322d3');
INSERT INTO type (id, name, parent_id)
VALUES ('452add63-4d97-4934-9251-363dcea1613b', 'Online', 'e32195df-92d5-48cd-9a9e-2586449322d3');

INSERT INTO type (id, name, parent_id)
VALUES ('366e3f84-3df4-4db6-a937-fc63ff12d9ab', 'Oštećenje puteva', '73a65d20-25ec-42ec-aea9-114b63231bac');
INSERT INTO type (id, name, parent_id)
VALUES ('ac1b4b1a-67aa-45e4-a00a-471dd24ecc5e', 'Pokvarene ulične svetiljke', '73a65d20-25ec-42ec-aea9-114b63231bac');
INSERT INTO type (id, name, parent_id)
VALUES ('aca14505-27f5-492e-af57-299dc6bb8461', 'Neispravni komunalni sistemi', '73a65d20-25ec-42ec-aea9-114b63231bac');

CREATE TABLE incident
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(2048)            NOT NULL,
    image_id    UUID                     NOT NULL,
    image_url   VARCHAR(2048)            NOT NULL,
    longitude   DECIMAL(9, 6)            NOT NULL,
    latitude    DECIMAL(9, 6)            NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE,
    status      VARCHAR(255)             NOT NULL,
    type_id     UUID                     NOT NULL,
    CONSTRAINT fk_incident_type FOREIGN KEY (type_id) REFERENCES type (id)
);
