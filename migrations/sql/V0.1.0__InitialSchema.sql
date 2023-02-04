DROP TABLE IF EXISTS comercial_activity;

CREATE TABLE comercial_activity (
    id SERIAL PRIMARY KEY
    , name TEXT NOT NULL
);

DROP TABLE IF EXISTS stratum;

CREATE TABLE stratum (
    id SERIAL PRIMARY KEY
    , name TEXT NOT NULL
);

DROP TABLE IF EXISTS shop_type;

CREATE TABLE shop_type (
    id SERIAL PRIMARY KEY
    , name TEXT NOT NULL
);

DROP TABLE IF EXISTS shop;

CREATE TABLE shop (
    id SERIAL PRIMARY KEY
    , name TEXT NOT NULL
    , business_name TEXT
    , activity_id INT REFERENCES comercial_activity (id)
    , stratum_id INT REFERENCES stratum (id)
    , address TEXT NOT NULL
    , phone_number TEXT
    , email TEXT
    , website TEXT
    , shop_type_id INT REFERENCES shop_type (id)
    , position GEOGRAPHY (POINT) NOT NULL
);
