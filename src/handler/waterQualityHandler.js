
/* eslint-disable quotes */

/* eslint-disable camelcase */

const axios = require('axios'); // Tambahkan axios untuk permintaan HTTP
const db = require('../config/db.js');
require('dotenv').config();
const { verifyToken } = require('../middlewares/verifyToken.js');


// Handler untuk menyimpan data kualitas air dan hit model ML di Cloud Run
const addWaterQuality = async (request, h) => {
  const decoded = verifyToken(request, h);
  if (decoded.isBoom) return decoded; // Jika token invalid, kembalikan respons error

  const userId = decoded.id; // Ambil userId dari JWT
  const { ph, turbidity, temperature } = request.payload;

  if (!ph || !turbidity || !temperature) {
    return h.response({ message: 'Semua field input harus diisi' }).code(400);
  }

  try {
    // Hit model ML di Cloud Run
    const cloudRunUrl = process.env.CLOUD_RUN_MODEL_URL;
    const mlResponse = await axios.post(`${cloudRunUrl}/predict`, {
      ph,
      turbidity,
      temperature,
    });

    const prediction = mlResponse.data.result; // Ambil hasil prediksi dari ML

    if (!prediction) {
      return h.response({ message: 'Prediksi tidak berhasil' }).code(500);
    }

    // Simpan data kualitas air ke tabel `water_quality_input`
    const result = await db.query(
      'INSERT INTO water_quality_input (ph, turbidity, temperature, user_id) VALUES (?, ?, ?, ?)',
      [ph, turbidity, temperature, userId]
    );

    const waterQualityId = result.insertId; // Dapatkan ID dari data yang dimasukkan

    // Simpan hasil prediksi ke tabel `water_quality_output`
    await db.query(
      'INSERT INTO water_quality_output (water_quality_id, prediction, user_id) VALUES (?, ?, ?)',
      [waterQualityId, prediction, userId]
    );

    return h.response({
      message: 'Data berhasil ditambahkan!',
      water_quality_id: waterQualityId,
      prediction,
    }).code(201);
  } catch (error) {
    console.error(error);
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
  const decoded = verifyToken(request, h); // Verifikasi token dan ambil userId dari JWT
  if (decoded.isBoom) return decoded; // Jika token invalid, kembalikan respons error

  const userId = decoded.id; // Ambil userId dari decoded token
  console.log('UserID from JWT:', userId); // Log userId dari token

  try {
    // Query untuk mengambil data yang sesuai dengan userId dan pastikan user_id tidak null
    const rows = await db.query(`
      SELECT 
        r.id AS prediction_id,
        r.water_quality_id,
        r.prediction,
        w.ph,
        w.turbidity,
        w.temperature,
        w.created_at,
        r.user_id
      FROM water_quality_output r
      JOIN water_quality_input w ON r.water_quality_id = w.id
      WHERE r.user_id = ? AND r.user_id IS NOT NULL`, [userId]);

    console.log('Query Result Rows:', rows); // Log hasil query untuk memeriksa data yang diambil

    if (!rows || rows.length === 0) {
      return h.response({ message: 'Tidak ada prediksi untuk pengguna ini!' }).code(404);
    }

    // Mengembalikan data prediksi sesuai dengan userId
    return h.response(rows).code(200);
  } catch (error) {
    console.error('Database error:', error.message);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};


// Handler untuk mendapatkan data kualitas air
const getWaterQuality = async (request, h) => {
  const decoded = verifyToken(request, h); // Verifikasi token dan ambil userId dari JWT
  if (decoded.isBoom) return decoded; // Jika token invalid, kembalikan respons error

  const userId = decoded.id; // Ambil userId dari decoded token
  console.log('UserID from JWT:', userId); // Log userId dari token

  try {
    // Ambil semua data dari tabel `water_quality_input` berdasarkan userId
    const rows = await db.query('SELECT * FROM water_quality_input WHERE user_id = ? AND user_id IS NOT NULL', [userId]);

    // Kembalikan data dalam response
    return h.response(rows).code(200);
  } catch (error) {
    console.error('Database error:', error.message);
    return h.response({ message: 'Database error!', error: error.message }).code(500);
  }
};



// Handler untuk memperbarui data kualitas air dan hit model ML di Cloud Run
const updateWaterQuality = async (request, h) => {
  const decoded = verifyToken(request, h); // Verifikasi token dan ambil userId dari JWT
  if (decoded.isBoom) return decoded; // Jika token invalid, kembalikan respons error

  const userId = decoded.id; // Ambil userId dari decoded token
  const { id } = request.params; // ID dari URL parameter
  const { ph, turbidity, temperature } = request.payload;

  if (!ph || !turbidity || !temperature) {
    return h.response({ message: 'Semua field input harus diisi' }).code(400);
  }

  try {
    // Periksa apakah data yang akan diperbarui milik pengguna yang terautentikasi
    const result = await db.query(
      'SELECT * FROM water_quality_input WHERE id = ? AND user_id = ?',
      [id, userId]
    );

    if (!result || result.length === 0) {
      return h.response({ message: 'Data tidak ditemukan atau tidak milik Anda!' }).code(404);
    }

    // Update data kualitas air di tabel `water_quality_input`
    const updateResult = await db.query(
      'UPDATE water_quality_input SET ph = ?, turbidity = ?, temperature = ? WHERE id = ?',
      [ph, turbidity, temperature, id]
    );

    if (updateResult.affectedRows === 0) {
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

    // Cek apakah prediction valid (tidak null)
    if (prediction === null || prediction === undefined) {
      return h.response({
        message: 'Prediksi tidak tersedia!',
        error: 'Prediksi ML mengembalikan nilai null',
      }).code(500);
    }

    // Simpan hasil prediksi ke tabel `water_quality_output` dengan foreign key `water_quality_id`
    await db.query(
      'INSERT INTO water_quality_output (water_quality_id, prediction, user_id) VALUES (?, ?, ?)',
      [id, prediction, userId]
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
  const decoded = verifyToken(request, h); // Verifikasi token dan ambil userId dari JWT
  if (decoded.isBoom) return decoded; // Jika token invalid, kembalikan respons error

  const userId = decoded.id; // Ambil userId dari decoded token
  const { id } = request.params; // ID dari URL parameter

  try {
    // Periksa apakah data yang akan dihapus milik pengguna yang terautentikasi
    const [waterQualityData] = await db.query(
      'SELECT * FROM water_quality_input WHERE id = ? AND user_id = ?',
      [id, userId]
    );

    if (!waterQualityData) {
      return h.response({ message: 'Data tidak ditemukan atau tidak milik Anda!' }).code(404);
    }

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


module.exports = { addWaterQuality, getWaterQuality, getPredictions, updateWaterQuality, deleteWaterQuality };