


/* eslint-disable camelcase */

const axios = require('axios'); // Tambahkan axios untuk permintaan HTTP
const db = require('../config/db.js');
require('dotenv').config();
const { verifyToken } = require('../middlewares/verifyToken.js');

const fishTypeMap = {
  'Bandeng': 0, 'Bawal': 1, 'Gabus': 2, 'Gurami': 3, 'Ikan Mas': 4,
  'Kakap': 5, 'Lele': 6, 'Mujair': 7, 'Nila': 8, 'Tenggiri': 9
};

// Handler untuk mendapatkan daftar fish type
const getFishTypes = async (request, h) => {
  try {
    const fishTypes = Object.keys(fishTypeMap).map((name) => ({ name }));
    return h.response({
      message: 'Daftar jenis ikan berhasil diambil',
      data: fishTypes
    }).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      message: 'Gagal mengambil daftar jenis ikan',
      error: error.message
    }).code(500);
  }
};


// Handler untuk menyimpan data kualitas air dan hit model ML di Cloud Run
const addPrediksiPanen = async (request, h) => {
  const decoded = verifyToken(request, h);
  if (decoded.isBoom) return decoded; // Jika token invalid, kembalikan respons error

  const { fish_size, water_condition, fish_type, feed_amount } = request.payload;
  const userId = decoded.id; // Ambil userId dari decoded token

  try {
    // Validasi fish_type
    if (!fishTypeMap.hasOwnProperty(fish_type)) {
      return h.response({
        message: 'Jenis ikan tidak valid'
      }).code(400);
    }

    // Kirim data ke model ML untuk prediksi
    const cloudRunUrl2 = process.env.MODEL2_URL;
    const response = await axios.post(`${cloudRunUrl2}/predict`, {
      fish_size,
      water_condition,
      fish_type,
      feed_amount,
    });

    // Ambil hasil prediksi dari response model ML
    const { predicted_days_to_harvest, recommended_feed } = response.data;

    // Simpan data lengkap ke database
    await db.query(
      'INSERT INTO PrediksiPanen (user_id, fish_size, water_condition, fish_type, feed_amount, predicted_days_to_harvest, recommended_feed) VALUES (?, ?, ?, ?, ?, ?, ?)',
      [
        userId,
        fish_size,
        water_condition,
        fish_type,
        feed_amount,
        predicted_days_to_harvest,
        recommended_feed
      ]
    );

    return h.response({
      message: 'Prediksi panen berhasil disimpan',
      data: {
        predicted_days_to_harvest: `${predicted_days_to_harvest} hari`,
        recommended_feed: `${recommended_feed} kg`
      }
    }).code(201);
  } catch (error) {
    console.error(error);
    return h.response({
      message: 'Gagal menyimpan prediksi panen',
      error: error.message
    }).code(500);
  }
};





const getPrediksiPanen = async (request, h) => {
  const decoded = verifyToken(request, h); // Verifikasi token dan ambil userId dari JWT
  if (decoded.isBoom) return decoded; // Jika token invalid, kembalikan respons error

  const userId = decoded.id; // Ambil userId dari decoded token
  console.log('UserID from JWT:', userId); // Log userId dari token

  try {
    // Ambil data prediksi dari tabel PrediksiPanen berdasarkan userId
    const result = await db.query(
      'SELECT * FROM PrediksiPanen WHERE user_id = ? ORDER BY created_at DESC',
      [userId]
    );

    return h.response({
      message: 'Data prediksi panen berhasil diambil',
      data: result,
    }).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      message: 'Gagal mengambil data prediksi panen',
      error: error.message,
    }).code(500);
  }
};



// Handler untuk menghapus data kualitas air
const deletePrediksiPanen = async (request, h) => {
  const decoded = verifyToken(request, h); // Verifikasi token dan ambil userId dari JWT
  if (decoded.isBoom) return decoded; // Jika token invalid, kembalikan respons error

  const { id } = request.params; // Ambil id prediksi yang akan dihapus
  const userId = decoded.id; // Ambil userId dari decoded token

  try {
    // Cek apakah prediksi tersebut milik user yang login
    const checkResult = await db.query(
      'SELECT * FROM PrediksiPanen WHERE id = ? AND user_id = ?',
      [id, userId]
    );

    if (checkResult.length === 0) {
      return h.response({
        message: 'Data prediksi panen tidak ditemukan atau tidak milik user ini',
      }).code(404);
    }

    // Hapus data prediksi berdasarkan id dan userId
    const result = await db.query(
      'DELETE FROM PrediksiPanen WHERE id = ? AND user_id = ?',
      [id, userId]
    );

    return h.response({
      message: 'Prediksi panen berhasil dihapus',
    }).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      message: 'Gagal menghapus prediksi panen',
      error: error.message,
    }).code(500);
  }
};




module.exports = { getFishTypes, addPrediksiPanen, getPrediksiPanen, deletePrediksiPanen };