const db = require('../config/db');

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


module.exports = { findUserByEmail, addUser };