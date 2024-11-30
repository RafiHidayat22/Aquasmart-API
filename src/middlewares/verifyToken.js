const jwt = require('jsonwebtoken');

const verifyToken = (req, h) => {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return h.response({
      status: 'fail',
      message: 'Token tidak ditemukan atau tidak valid.',
    }).code(401).takeover();
  }

  const token = authHeader.split(' ')[1];
  try {
    const decoded = jwt.verify(token, 'your-secret-key'); // Gunakan secret key yang sama saat membuat JWT
    return decoded; // Berikan informasi user yang terdekode
  } catch (err) {
    return h.response({
      status: 'fail',
      message: 'Token tidak valid atau sudah kadaluarsa.',
    }).code(401).takeover();
  }
};

module.exports = { verifyToken };
