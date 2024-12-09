/* eslint-disable linebreak-style */
const Hapi = require('@hapi/hapi');
const authRoutes = require('./routes/routes.js');
const waterQualityRoutes = require('./routes/waterQualityRoutes.js');

const init = async () => {
  const server = Hapi.server({
    port: 9000,
    host: 'localhost',
    routes: {
      cors: {
        origin: ['*'],
      },
    },
  });

  server.route(authRoutes);
  server.route(waterQualityRoutes);

  await server.start();
  console.log('Server running on %s', server.info.uri);
};

process.on('unhandledRejection', (err) => {
  console.log(err);
  process.exit(1);
});

init();