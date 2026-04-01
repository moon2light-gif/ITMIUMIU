SET search_path TO s502511, public;

INSERT INTO Observer (name, sex, height, weight, birth_date) VALUES
('Jennaro', 'male', 179.43, 78.43, '1996-03-15'),
('Robert', 'male', NULL, NULL, '2000-03-12'),
('Maria', 'female', 170.54, 56.20, '2005-12-29'),
('Jennaro', 'male', 187.56, 156.32, '1954-10-21'),
('Mary', 'female', 150.34, 48.32, '2001-06-03');

INSERT INTO Dinosaur (name, species) VALUES 
('Blue', 'Velociraptor'),
('Leon', 'Tyrannosaurus'),
('Ada', 'Velociraptor'),
('Zoe', 'Velociraptor'),
('Pippin', 'Brachiosaurus'); 

INSERT INTO Elevation (type, material, height) VALUES 
('Hill', 'soil', 1.56),
('Mountain', 'rock', 9.34),
('Car', 'metal', 2.14),
('Tree', 'wood', 5.43);

INSERT INTO Boxes (type, material, size) VALUES 
('With electrical panels', 'wood', 'big'),
('With balloons', 'plastic', 'medium'), 
('With beer', 'steel', 'huge'),
('With electircal panels', 'wood', 'small');

INSERT INTO Dino_description (dino_id, color, age_group, eye_color) VALUES
(2,'grey', 'baby', 'brown'), 
(5, 'orange', 'teen', 'black'),
(1, 'green with yellow stripes', 'adult', 'black'),
(4, 'blue with white dots', 'adult', 'yellow');

INSERT INTO Dino_state (dino_id, action, sound) VALUES 
(5, 'stands and looks', 'moo'), 
(2, 'plays with others', 'chirps'),
(3, 'eats', 'growls'),
(1, 'stands and looks', 'chirps'),
(4, 'running around', NULL);

INSERT INTO Dino_position (boxes_id, dino_id, distance_in_meters) VALUES
(2, 2, 1.52),
(1, 2, 4.52),
(3, 1, 3.12),
(2, 5, 12.23),
(1, 4, 0.52);

INSERT INTO Observer_shelter (location_name, observer_id, elevation_id, boxes_id, visibility, illumination) VALUES 
('Silent Hill', 2, 3, 1, TRUE, 'dark'),
('Sector-B1', 1, 3, 3, FALSE, 'light'),
('Jurassic Park', 5, 1, 2, FALSE, 'very dark'),
('Jungle', 4, 2, 2, TRUE, 'light'), 
('Sector-B1', 4, 2, 1, FALSE, 'dark'), 
('Racoon city', 1, 3, 1, TRUE, 'obscurity'), 
('Sesame Street', 5, 1, 1, FALSE, 'light');
