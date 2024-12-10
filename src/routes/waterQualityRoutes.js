/* eslint-disable linebreak-style */
/* eslint-disable indent */
/* eslint-disable linebreak-style */
/* eslint-disable no-trailing-spaces */
const { verifyToken } = require('../middlewares/verifyToken');
const { 
    addWaterQuality, 
    getWaterQuality, 
    getPredictions,  
    updateWaterQuality, 
    deleteWaterQuality,
  
  } = require('../handler/waterQualityHandler');
  
  const waterQualityRoutes = [
    {
      method: 'POST',
      path: '/api/water-quality',
      handler: addWaterQuality,
      options: {
        pre: [{ method: verifyToken }]
      },
    },
    {
      method: 'GET',
      path: '/api/water-quality',
      handler: getWaterQuality,
      options: {
        pre: [{ method: verifyToken }]
      },
    },
    {
      method: 'GET',
      path: '/api/water-quality/predictions',
      handler: getPredictions,
      options: {
        pre: [{ method: verifyToken }]
      },
    },
    {
      method: 'PUT',
      path: '/api/water-quality/{id}',  // Route untuk mengupdate data kualitas air berdasarkan ID
      handler: updateWaterQuality,
      options: {
        pre: [{ method: verifyToken }]
      },
    },
    {
      method: 'DELETE',
      path: '/api/water-quality/{id}',  // Route untuk menghapus data kualitas air berdasarkan ID
      handler: deleteWaterQuality,
      options: {
        pre: [{ method: verifyToken }]
      },
    },
  ];
  
  module.exports = waterQualityRoutes;
  