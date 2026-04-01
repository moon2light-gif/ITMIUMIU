SET search_path TO s502511, public;

DROP TABLE IF EXISTS Observer_shelter CASCADE;
DROP TABLE IF EXISTS Dino_state CASCADE;
DROP TABLE IF EXISTS Dino_description CASCADE;
DROP TABLE IF EXISTS Dino_position CASCADE;
DROP TABLE IF EXISTS Observer CASCADE;
DROP TABLE IF EXISTS Boxes CASCADE;
DROP TABLE IF EXISTS Elevation CASCADE;
DROP TABLE IF EXISTS Dinosaur CASCADE;

CREATE TABLE Observer (
	observer_id BIGSERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	sex VARCHAR(30) NOT NULL,
	height DECIMAL(5,2),
	weight DECIMAL(5,2),
	birth_date DATE
);

CREATE TABLE Elevation (
	elevation_id BIGSERIAL PRIMARY KEY,
	type VARCHAR(50) NOT NULL, 
	material VARCHAR(30),
	height DECIMAL(3,2) NOT NULL
);

CREATE TABLE Boxes (
	set_id BIGSERIAL PRIMARY KEY,
	type VARCHAR(50) NOT NULL,
	material VARCHAR(30),
	size VARCHAR(20)
);

CREATE TABLE Dinosaur (
	dino_id BIGSERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	species VARCHAR(100) NOT NULL
);	

CREATE TABLE Dino_description (
	dino_id BIGINT REFERENCES dinosaur(dino_id) PRIMARY KEY,
	color VARCHAR(50) NOT NULL,
	age_group VARCHAR(30) NOT NULL,
	eye_color VARCHAR(50) NOT NULL
);

CREATE TABLE Dino_state (
	dino_id BIGINT REFERENCES dinosaur(dino_id) PRIMARY KEY,
	action VARCHAR(100) NOT NULL,
	sound VARCHAR(50)
);

CREATE TABLE Dino_position (
	boxes_id BIGINT REFERENCES boxes(set_id) NOT NULL,
	dino_id BIGINT REFERENCES dinosaur(dino_id) NOT NULL,
	distance_in_meters DECIMAL(4,2) NOT NULL,
	PRIMARY KEY (boxes_id, dino_id)
);

CREATE TABLE Observer_shelter (
	location_name VARCHAR(100) NOT NULL,
	observer_id BIGINT REFERENCES observer(observer_id),
	elevation_id BIGINT REFERENCES elevation(elevation_id),
	boxes_id BIGINT REFERENCES boxes(set_id),
	visibility BOOLEAN NOT NULL,
	illumination VARCHAR(50) NOT NULL,
	PRIMARY KEY (observer_id, elevation_id, boxes_id)
);
