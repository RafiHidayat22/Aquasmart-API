const handler = require('../handler/handler.js');
const { verifyToken } = require('../middlewares/verifyToken');

module.exports = [
  {
    method: 'POST',
    path: '/auth/register',
    handler: handler.register,
  },
  {
    method: 'POST',
    path: '/auth/login',
    handler: handler.login,
  },
  {
    method: 'GET',
    path: '/profile',
    handler: handler.profile,
    options: {
      auth: false,
    },
  },
  {
    method: 'PUT',
    path: '/profile/picture',
    options: {
      pre: [{ method: verifyToken }],
      payload: { maxBytes: 5 * 1024 * 1024, output: 'stream', parse: true, multipart: true },
    },
    handler: handler.updateProfilePicture,
  },
];