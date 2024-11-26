const authHandler = require('../handler/auth-handler');

module.exports = [
  {
    method: 'GET',
    path: '/auth',
    handler: (request, h) => {
      return 'Authentication route';
    },
  },
  {
    method: 'POST',
    path: '/auth/register',
    handler: authHandler.register,
  },
  {
    method: 'POST',
    path: '/auth/login',
    handler: authHandler.login,
  },
];