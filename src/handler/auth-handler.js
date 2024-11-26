const { nanoid } = require('nanoid');
const User = require('../models/user');
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
    return h.response({
      status: 'fail',
      message: error.details[0].message,
    }).code(400);
  }

  try {
    // Periksa jika email sudah digunakan
    return new Promise((resolve) => {
      User.findUserByEmail(email, async (err, user) => {
        if (err) {
          console.error('Error while checking email:', err); // Log error
          const response = h.response({
            status: 'error',
            message: 'Terjadi kesalahan pada server.',
          });
          response.code(500);
          return resolve(response);
        }

        if (user) {
          const response = h.response({
            status: 'fail',
            message: 'Email sudah terdaftar.',
          });
          response.code(400);
          return resolve(response);
        }

        // Hash password
        const hashedPassword = await hashPassword(password);
        const id = nanoid(16); // Tambahkan ID unik

        // Tambahkan user ke database
        console.log('User Data Before Insert:', { id, email, password: hashedPassword, name });
        User.addUser({ id, email, password: hashedPassword, name }, (err, results) => {
          if (err) {
            console.error('Error while adding user:', err); // Log error dari model
            const response = h.response({
              status: 'error',
              message: 'Gagal mendaftarkan user. Terjadi kesalahan pada server.',
            });
            response.code(500);
            return resolve(response);
          }

          console.log('Add User Results:', results); // Log hasil jika berhasil
          const response = h.response({
            status: 'success',
            message: 'User berhasil didaftarkan.',
            data: {
              userId: id,
            },
          });
          response.code(201);
          return resolve(response);
        });

      });
    });
  } catch (err) {
    console.error('Error in registration process:', err); // Log error
    const response = h.response({
      status: 'error',
      message: 'Terjadi kesalahan pada server.',
    });
    response.code(500);
    return response;
  }
};

// Login user
const login = (req, h) => {
  const { email, password } = req.payload;

  return new Promise((resolve) => {
    User.findUserByEmail(email, async (err, user) => {
      if (err) {
        console.error('Error while finding user:', err); // Log error
        return resolve(h.response({
          status: 'error',
          message: 'Terjadi kesalahan pada server.',
        }).code(500));
      }

      if (!user) {
        return resolve(h.response({
          status: 'fail',
          message: 'Email atau password salah',
        }).code(400));
      }

      // Pastikan user ditemukan sebelum mengecek password
      const isMatch = await comparePassword(password, user.password);
      if (!isMatch) {
        return resolve(h.response({
          status: 'fail',
          message: 'Email atau password salah',
        }).code(400));
      }

      // Buat token JWT
      const token = jwt.sign({ id: user.id, email: user.email }, 'your-secret-key', { expiresIn: '1h' });

      resolve(h.response({
        status: 'success',
        message: 'Login berhasil',
        token,
      }).code(200));
    });
  });
};

module.exports = { login, register };
