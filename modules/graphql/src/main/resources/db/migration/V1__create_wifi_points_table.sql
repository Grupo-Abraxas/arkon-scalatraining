CREATE TABLE wifi_points (
                             id SERIAL PRIMARY KEY,
                             program VARCHAR(255) NOT NULL,
                             installation_date VARCHAR(255) NOT NULL,
                             latitude DOUBLE PRECISION NOT NULL,
                             longitude DOUBLE PRECISION NOT NULL,
                             neighborhood VARCHAR(255) NOT NULL,
                             municipality VARCHAR(255) NOT NULL
);

CREATE EXTENSION cube;
CREATE EXTENSION earthdistance;
