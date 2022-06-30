DROP TABLE IF EXISTS c_estatus;
CREATE TABLE IF NOT EXISTS c_estatus (
  id serial PRIMARY KEY NOT NULL,
  description TEXT NOT NULL
) ;

INSERT INTO c_estatus (description) VALUES
	('Activo'),
	('Inactivo');

DROP TABLE IF EXISTS t_alcaldia;
CREATE TABLE IF NOT EXISTS t_alcaldia (
  id serial PRIMARY KEY NOT NULL,
  name TEXT NOT NULL,
  geopolygon polygon,
  estado int DEFAULT '1'
) ;


DROP TABLE IF EXISTS t_mb;

CREATE TABLE IF NOT EXISTS t_mb (
    id serial PRIMARY KEY,
    vehicle_id int DEFAULT NULL,
    point_latitude DOUBLE PRECISION,
    point_longitude DOUBLE PRECISION,
    geopoint Point,
    estado int DEFAULT '1'
);


--DROP USER IF EXISTS usersavedata;
--CREATE USER usersavedata WITH encrypted  PASSWORD '123456789A';
--grant all privileges on database mtbMX to usersavedata;


--DROP USER IF EXISTS userconsulta;
--CREATE USER userconsulta WITH encrypted  PASSWORD '123456789A';
--grant all privileges on database mtbMX to userconsulta;
