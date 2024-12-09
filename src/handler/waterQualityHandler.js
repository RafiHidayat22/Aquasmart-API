/* eslint-disable camelcase */
const axios = require('axios');
const db = require('../config/db.js');
require('dotenv').config();

// Handler untuk menyimpan data kualitas air dan memproses model ML di Cloud Run
const addWaterQuality = async (request, h) => {
  const { ph, turbidity, temperature } = request.payload;

  // Validasi input
  if (!ph || !turbidity || !temperature) {
    return h.response({ message: 'Semua field input harus diisi' }).code(400);
  }

  try {
    // Mengecek koneksi database dengan query SELECT 1
    console.log('Mengecek koneksi ke database...');
    const connectionCheck = await db.query('SELECT 1');
console.log('Koneksi ke database berhasil:', connectionCheck);  // Menampilkan hasil query

    // Mengambil URL model ML dari environment
    const cloudRunUrl = process.env.MODEL_URL;
    const mlResponse = await axios.post(`${cloudRunUrl}/predict`, {
      ph,
      turbidity,
      temperature,
    });

    console.log('Respons dari Cloud Run:', mlResponse.data);

    // Pastikan respons dari ML ada dan valid
    const prediction = mlResponse.data?.result;
    if (!prediction) {
      throw new Error('Respons dari model ML tidak valid');
    }

    // Menyisipkan data ke tabel water_quality_input
    console.log('Melakukan insert data ke tabel water_quality_input...');
    const insertResult = await db.query(
      'INSERT INTO water_quality_input (ph, turbidity, temperature) VALUES (?, ?, ?)',
      [ph, turbidity, temperature]
    );

    console.log('Insert result water_quality_input:', insertResult);

    // Pastikan data berhasil disisipkan
    if (insertResult.affectedRows > 0) {
      const waterQualityId = insertResult.insertId;
      console.log('Water Quality ID:', waterQualityId); // Memeriksa ID yang digunakan

      // Menyisipkan hasil prediksi ke tabel water_quality_output
      console.log('Melakukan insert ke tabel water_quality_output...');
      const outputResult = await db.query(
        'INSERT INTO water_quality_output (water_quality_id, prediction) VALUES (?, ?)',
        [waterQualityId, prediction]
      );

      console.log('Insert result water_quality_output:', outputResult);

      return h.response({
        message: 'Data berhasil ditambahkan!',
        water_quality_id: waterQualityId,
        prediction,
      }).code(201);
    } else {
      throw new Error('Gagal menyimpan data kualitas air');
    }

  } catch (error) {
    console.error('Error:', error.message);
    return h.response({
      message: 'Terjadi kesalahan saat memproses data',
      error: error.message,
    }).code(500);
  }
};

// Handler untuk mendapatkan semua data prediksi
const getPredictions = async (request, h) => {
  try {
    const [rows] = await db.query(`
      SELECT 
        r.id AS prediction_id,
        r.water_quality_id,
        r.prediction,
        w.ph,
        w.turbidity,
        w.temperature,
        w.created_at
      FROM water_quality_output r
      JOIN water_quality_input w ON r.water_quality_id = w.id
    `);

    return h.response(rows).code(200);
  } catch (error) {
    console.error('Error:', error.message);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};

// Handler untuk mendapatkan prediksi berdasarkan ID
const getPredictionById = async (request, h) => {
  const { id } = request.params;

  try {
    const [rows] = await db.query(`
      SELECT 
        r.id AS prediction_id,
        r.water_quality_id,
        r.prediction,
        w.ph,
        w.turbidity,
        w.temperature,
        w.created_at
      FROM water_quality_output r
      JOIN water_quality_input w ON r.water_quality_id = w.id
      WHERE r.id = ?`, [id]);

    if (rows.length === 0) {
      return h.response({ message: 'Prediksi tidak ditemukan!' }).code(404);
    }

    return h.response(rows[0]).code(200);
  } catch (error) {
    console.error('Error:', error.message);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};

// Handler untuk mendapatkan data kualitas air
const getWaterQuality = async (request, h) => {
  try {
    const [rows] = await db.query('SELECT * FROM water_quality_input');
    return h.response(rows).code(200);
  } catch (error) {
    console.error('Error:', error.message);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};

// Handler untuk memperbarui data kualitas air dan hit model ML di Cloud Run
const updateWaterQuality = async (request, h) => {
  const { id } = request.params;
  const { ph, turbidity, temperature } = request.payload;

  // Validasi input
  if (!ph || !turbidity || !temperature) {
    return h.response({ message: 'Semua field input harus diisi' }).code(400);
  }

  try {
    const [updateResult] = await db.query(
      'UPDATE water_quality_input SET ph = ?, turbidity = ?, temperature = ? WHERE id = ?',
      [ph, turbidity, temperature, id]
    );

    if (updateResult.affectedRows === 0) {
      return h.response({ message: 'Data tidak ditemukan!' }).code(404);
    }

    // Mengambil hasil prediksi dari model ML
    const cloudRunUrl = process.env.MODEL_URL;
    const mlResponse = await axios.post(`${cloudRunUrl}/predict`, {
      ph,
      turbidity,
      temperature,
    });

    if (!mlResponse.data || !mlResponse.data.prediction) {
      throw new Error('Respons dari model ML tidak valid');
    }

    const prediction = mlResponse.data.prediction;

    // Menyisipkan hasil prediksi ke tabel output
    await db.query(
      'INSERT INTO water_quality_output (water_quality_id, prediction) VALUES (?, ?)',
      [id, prediction]
    );

    return h.response({ message: 'Data berhasil diperbarui!', prediction }).code(200);
  } catch (error) {
    console.error('Error:', error.message);
    return h.response({
      message: error.response?.data?.message || 'Terjadi kesalahan saat memproses data',
      error: error.message,
    }).code(500);
  }
};

// Handler untuk menghapus data kualitas air
const deleteWaterQuality = async (request, h) => {
  const { id } = request.params;

  try {
    // Menghapus data kualitas air dan prediksi terkait
    const [deleteResult] = await db.query('DELETE FROM water_quality_input WHERE id = ?', [id]);

    if (deleteResult.affectedRows === 0) {
      return h.response({ message: 'Data tidak ditemukan!' }).code(404);
    }

    // Hapus prediksi terkait di water_quality_output
    await db.query('DELETE FROM water_quality_output WHERE water_quality_id = ?', [id]);

    return h.response({ message: 'Data berhasil dihapus!' }).code(200);
  } catch (error) {
    console.error('Error:', error.message);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};

module.exports = {
  addWaterQuality,
  getWaterQuality,
  getPredictions,
  updateWaterQuality,
  deleteWaterQuality,
  getPredictionById,
};
