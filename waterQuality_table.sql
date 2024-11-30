CREATE TABLE water_quality_input (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ph FLOAT NOT NULL,
    turbidity FLOAT NOT NULL,
    temprature FLOAT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE water_quality_output (
    id INT AUTO_INCREMENT PRIMARY KEY,
    water_quality_id INT NOT NULL,
    prediction FLOAT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (water_quality_id) REFERENCES water_quality_input(id)
);
