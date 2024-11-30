/* eslint-disable linebreak-style */
const { nanoid } = require('nanoid');
const User = require('../models/user.js');
const { hashPassword, comparePassword } = require('../middlewares/hashPassword.js');
const { verifyToken } = require('../middlewares/verifyToken.js');
const jwt = require('jsonwebtoken');
const Joi = require('joi');
const { uploadFile } = require('../uploadImage/image.js');

// Validasi input menggunakan Joi
const userSchema = Joi.object({
  name: Joi.string().min(3).required(),
  email: Joi.string().email().required(),
  password: Joi.string().min(6).required(),
  phoneNumber: Joi.string()
    .pattern(/^[0-9]{10,15}$/)
    .required(),
  dateBirth: Joi.date().required(),
});

// Registrasi user baru
const register = async (req, h) => {
  const { name, email, dateBirth, phoneNumber, password } = req.payload;

  // Validasi input
  const { error } = userSchema.validate({ name, email, dateBirth, phoneNumber, password });
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
        const id = nanoid(16);

        // Tambahkan user ke database
        console.log('User Data Before Insert:', { id, name, email, dateBirth, phoneNumber, password: hashedPassword });
        User.addUser({ id, name, email, dateBirth, phoneNumber, password: hashedPassword }, (err, results) => {
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

const profile = async (req, h) => {
  const decoded = verifyToken(req, h);
  if (decoded.isBoom) return decoded;

  try {
    const user = await User.findUserById(decoded.id);
    if (!user) {
      return h.response({
        status: 'fail',
        message: 'Pengguna tidak ditemukan.',
      }).code(404);
    }

    const profilePicture = user.profile_picture_url || 'https://example.com/default-profile-picture.jpg'; // URL default

    return h.response({
      status: 'success',
      data: {
        id: user.id,
        name: user.name,
        email: user.email,
        phoneNumber: user.phoneNumber,
        dateBirth: user.dateBirth,
        profilePicture,
      },
    }).code(200);
  } catch (err) {
    console.error(err);
    return h.response({
      status: 'error',
      message: 'Terjadi kesalahan pada server.',
    }).code(500);
  }
};


// Update foto profil
const updateProfilePicture = async (req, h) => {
  const decoded = verifyToken(req, h);
  if (decoded.isBoom) return decoded;

  const file = req.payload.profilePicture;
  if (!file || !file.hapi.filename) {
    return h.response({
      status: 'fail',
      message: 'Foto profil tidak ditemukan.',
    }).code(400);
  }

  try {
    const fileUrl = await uploadFile(file);
    console.log('Uploaded File URL:', fileUrl);

    // Memperbarui foto profil dengan URL baru
    await User.updatePP(decoded.id, fileUrl);

    return h.response({
      status: 'success',
      message: 'Foto profil berhasil diupdate.',
      data: { profilePicture: fileUrl },
    }).code(200);
  } catch (err) {
    console.error(err);
    return h.response({
      status: 'error',
      message: 'Gagal mengupdate foto profil. Terjadi kesalahan server.',
    }).code(500);
  }
};



module.exports = { login, register, profile, updateProfilePicture };
