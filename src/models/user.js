const db = require('../config/db');
const util = require('util');

// Cari user berdasarkan email
const findUserByEmail = (email, callback) => {
  const sql = 'SELECT * FROM users WHERE email = ?';
  db.query(sql, [email], (err, results) => {
    if (err) return callback(err, null);

    // Pastikan results memiliki data
    if (results.length === 0) {
      return callback(null, null); // Tidak ditemukan
    }

    callback(null, results[0]); // Kembalikan data user
  });
};

// Tambahkan user baru
const addUser = (data, callback) => {
  const sql = 'INSERT INTO users SET ?';
  db.query(sql, data, (err, results) => {
    if (err) {
      console.error('SQL Error:', err.message); // Log pesan error SQL
      return callback(err, null);
    }

    // Tambahkan validasi jika insert gagal
    if (!results || results.affectedRows === 0) {
      console.error('Insert Operation Failed:', results); // Log hasil jika gagal
      return callback(new Error('Failed to insert user'), null);
    }

    console.log('Insert Success:', results); // Log jika berhasil
    callback(null, results);
  });
};

const findUserById = (id) => {
  return new Promise((resolve, reject) => {
    const sql = 'SELECT * FROM users WHERE id = ?';
    db.query(sql, [id], (err, results) => {
      if (err) {
        reject(err);
      }
      resolve(results[0]);
    });
  });
};

const updatePP = async (id, profilePictureUrl) => {
  const sql = 'UPDATE users SET profile_picture_url = ?, updated_at = NOW() WHERE id = ?';
  const query = util.promisify(db.query).bind(db);
  try {
    const result = await query(sql, [profilePictureUrl, id]);

    if (result.affectedRows === 0) {
      throw new Error('User tidak ditemukan atau gagal diperbarui.');
    }

    return result;
  } catch (error) {
    console.error('Error updating profile picture:', error);
    throw error;
  }
};


module.exports = { findUserByEmail, addUser, findUserById, updatePP  };