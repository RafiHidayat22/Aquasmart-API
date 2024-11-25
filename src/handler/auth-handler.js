const User = require('../models/user.js');
const { hashPassword, comparePassword } = require('../middlewares/hashPassword');
const jwt = require('jsonwebtoken');
const Joi = require('joi');

// Validasi input menggunakan Joi
const userSchema = Joi.object({
  name: Joi.string().min(3).required(),
  email: Joi.string().email().required(),
  password: Joi.string().min(6).required(),
});

// Registrasi user baru
const register = async (req, h) => {
  const { email, password, name } = req.payload;

  // Validasi input
  const { error } = userSchema.validate({ email, password, name });
  if (error) {
    return h.response({ error: error.details[0].message }).code(400);
  }

  try {
    // Periksa jika email sudah digunakan
    return new Promise((resolve) => {
      User.findUserByEmail(email, async (err, user) => {
        if (err) resolve(h.response({ error: err.message }).code(500));

        if (user) {
          resolve(h.response({ message: 'Email already registered' }).code(400));
        }

        // Hash password
        const hashedPassword = await hashPassword(password);

        // Tambahkan user ke database
        User.addUser({ email, password: hashedPassword, name }, (err, results) => {
          if (err) resolve(h.response({ error: err.message }).code(500));
          resolve(h.response({ message: 'User registered successfully' }).code(201));
        });
      });
    });
  } catch (err) {
    return h.response({ error: err.message }).code(500);
  }
};

// Login user
const login = (req, h) => {
  const { email, password } = req.payload;

  return new Promise((resolve) => {
    User.findUserByEmail(email, async (err, user) => {
      if (err) {
        return resolve(h.response({ error: err.message }).code(500));
      }

      if (!user) {
        return resolve(h.response({ message: 'Email atau password salah' }).code(400));
      }

      // Pastikan user ditemukan sebelum mengecek password
      const isMatch = await comparePassword(password, user.password);
      if (!isMatch) {
        return resolve(h.response({ message: 'Email atau password salah' }).code(400));
      }

      // Buat token JWT
      const token = jwt.sign({ id: user.id, email: user.email }, 'your-secret-key', { expiresIn: '1h' });

      resolve(h.response({ message: 'Login berhasil', token }).code(200));
    });
  });
};


module.exports = { login, register };