CREATE TABLE water_quality_input (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ph FLOAT NOT NULL,
    turbidity FLOAT NOT NULL,
    temprature FLOAT NOT NULL,
    user_id VARCHAR(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE water_quality_output (
  id INT AUTO_INCREMENT PRIMARY KEY,
  water_quality_id INT,
  prediction VARCHAR(255),
  recommendation VARCHAR(255) DEFAULT 'No recommendation',
  user_id VARCHAR(16),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (water_quality_id) REFERENCES water_quality_input(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);