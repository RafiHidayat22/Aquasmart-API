/* eslint-disable linebreak-style */
/* eslint-disable camelcase */
/* eslint-disable linebreak-style */
const axios = require('axios'); // Tambahkan axios untuk permintaan HTTP
const db = require('../db.js');
require('dotenv').config();


// Handler untuk menyimpan data kualitas air dan hit model ML di Cloud Run
const addWaterQuality = async (request, h) => {
  const { ph, turbidity, temperature } = request.payload;

  if (!ph || !turbidity || !temperature) {
    return h.response({ message: 'Semua field input harus diisi' }).code(400);
  }

  try {
    // Hit model ML di Cloud Run
    const cloudRunUrl = process.env.CLOUD_RUN_MODEL_URL; // URL Cloud Run dari .env
    const mlResponse = await axios.post(`${cloudRunUrl}/predict`, {
      ph,
      turbidity,
      temperature,
    });

    const prediction = mlResponse.data.prediction; // Ambil hasil prediksi dari ML
    // Simpan data kualitas air ke tabel `water_quality_input`
    const [waterResult] = await db.query(
      'INSERT INTO water_quality_input (ph, turbidity, temprature) VALUES (?, ?, ?)',
      [ph, turbidity, temperature]
    );

    // Simpan hasil prediksi ke tabel `water_quality_output` dengan foreign key `water_quality_id`
    const waterQualityId = waterResult.insertId; // ID dari tabel `water_quality_input`
    await db.query(
      'INSERT INTO water_quality_output (water_quality_id, prediction) VALUES (?, ?)',
      [waterQualityId, prediction]
    );

    return h.response({
      message: 'Data berhasil ditambahkan!',
      water_quality_id: waterQualityId,
      prediction,
    }).code(201);

  } catch (error) {
    console.error(error);

    // Tangani error dari Cloud Run atau database
    if (error.response) {
      return h.response({
        message: 'Error saat memproses prediksi ML',
        error: error.response.data,
      }).code(500);
    }

    return h.response({
      message: 'Database error atau masalah koneksi!',
      error: error.message,
    }).code(500);
  }
};

const getPredictions = async (request, h) => {
  try {
    const [rows] = await db.query(`
      SELECT 
        r.id AS prediction_id,
        r.water_quality_id,
        r.prediction,
        w.ph,
        w.turbidity,
        w.temprature,
        w.created_at
      FROM water_quality_output r
      JOIN water_quality_input w ON r.water_quality_id = w.id
    `);

    return h.response(rows).code(200);
  } catch (error) {
    console.error(error);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};

const getPredictionById = async (request, h) => {
  const { id } = request.params; // ID dari URL parameter

  try {
    const [rows] = await db.query(`
      SELECT 
        r.id AS prediction_id,
        r.water_quality_id,
        r.prediction,
        w.ph,
        w.turbidity,
        w.temprature,
        w.created_at
      FROM water_quality_output r
      JOIN water_quality_input w ON r.water_quality_id = w.id
      WHERE r.id = ?`, [id]);

    if (rows.length === 0) {
      return h.response({ message: 'Prediksi tidak ditemukan!' }).code(404);
    }

    return h.response(rows[0]).code(200); // Mengembalikan hasil prediksi berdasarkan ID
  } catch (error) {
    console.error(error);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};

// Handler untuk mendapatkan data kualitas air
const getWaterQuality = async (request, h) => {
  try {
    const [rows] = await db.query('SELECT * FROM water_quality_input');
    return h.response(rows).code(200);
  } catch (error) {
    console.error(error);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};

// Handler untuk memperbarui data kualitas air dan hit model ML di Cloud Run
const updateWaterQuality = async (request, h) => {
  const { id } = request.params; // ID dari URL parameter
  const { ph, turbidity, temperature } = request.payload;

  if (!ph || !turbidity || !temperature) {
    return h.response({ message: 'Semua field input harus diisi' }).code(400);
  }

  try {
    // Update data kualitas air di tabel `water_quality_input`
    const [result] = await db.query(
      'UPDATE water_quality_input SET ph = ?, turbidity = ?, temprature = ? WHERE id = ?',
      [ph, turbidity, temperature, id]
    );

    if (result.affectedRows === 0) {
      return h.response({ message: 'Data tidak ditemukan!' }).code(404);
    }

    // Hit model ML di Cloud Run dengan data yang diperbarui
    const cloudRunUrl = process.env.CLOUD_RUN_MODEL_URL; // URL Cloud Run dari .env
    const mlResponse = await axios.post(`${cloudRunUrl}/predict`, {
      ph,
      turbidity,
      temperature,
    });

    const prediction = mlResponse.data.prediction; // Ambil hasil prediksi dari ML

    // Simpan hasil prediksi ke tabel `water_quality_output` dengan foreign key `water_quality_id`
    await db.query(
      'INSERT INTO water_quality_output (water_quality_id, prediction) VALUES (?, ?)',
      [id, prediction]
    );

    return h.response({ message: 'Data berhasil diperbarui!', prediction }).code(200);
  } catch (error) {
    console.error(error);

    // Tangani error dari Cloud Run atau database
    if (error.response) {
      return h.response({
        message: 'Error saat memproses prediksi ML',
        error: error.response.data,
      }).code(500);
    }

    return h.response({
      message: 'Database error atau masalah koneksi!',
      error: error.message,
    }).code(500);
  }
};

// Handler untuk menghapus data kualitas air
const deleteWaterQuality = async (request, h) => {
  const { id } = request.params; // ID dari URL parameter

  try {
    // Hapus data kualitas air dan hasil prediksi terkait di tabel `water_quality_output`
    const [deleteResult] = await db.query('DELETE FROM water_quality_input WHERE id = ?', [id]);

    if (deleteResult.affectedRows === 0) {
      return h.response({ message: 'Data tidak ditemukan!' }).code(404);
    }

    // Hapus hasil prediksi dari tabel `water_quality_output` berdasarkan foreign key
    await db.query('DELETE FROM water_quality_output WHERE water_quality_id = ?', [id]);

    return h.response({ message: 'Data berhasil dihapus!' }).code(200);
  } catch (error) {
    console.error(error);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};

module.exports = { addWaterQuality, getWaterQuality, getPredictions, updateWaterQuality, deleteWaterQuality, getPredictionById };
