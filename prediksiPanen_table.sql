CREATE TABLE PrediksiPanen (
    id SERIAL PRIMARY KEY, -- Kolom ID untuk setiap record
    user_id VARCHAR(16) NOT NULL, -- ID pengguna yang terkait dengan prediksi (menggunakan VARCHAR(16) sesuai dengan id di users)
    fish_size VARCHAR(255) NOT NULL, -- Ukuran ikan
    water_condition VARCHAR(255) NOT NULL, -- Kondisi air
    fish_type VARCHAR(255) NOT NULL, -- Jenis ikan
    feed_amount DECIMAL(10, 2) NOT NULL, -- Jumlah pakan
    predicted_days_to_harvest INT NOT NULL, -- Hari prediksi untuk panen
    recommended_feed DECIMAL(10, 2) NOT NULL, -- Pakan yang disarankan
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Waktu prediksi dibuat
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Waktu prediksi diperbarui
    FOREIGN KEY (user_id) REFERENCES users(id) -- Relasi dengan tabel Users
);

-- Index pada user_id untuk mempercepat pencarian berdasarkan user
CREATE INDEX idx_user_id ON PrediksiPanen (user_id);
