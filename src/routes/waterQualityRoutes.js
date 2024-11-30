/* eslint-disable linebreak-style */
/* eslint-disable indent */
/* eslint-disable linebreak-style */
/* eslint-disable no-trailing-spaces */
const { 
    addWaterQuality, 
    getWaterQuality, 
    getPredictions,  
    updateWaterQuality, 
    deleteWaterQuality,
    getPredictionById
  
  } = require('../handlers/waterQualityHandler');
  
  const waterQualityRoutes = [
    {
      method: 'POST',
      path: '/api/water-quality',
      handler: addWaterQuality,
    },
    {
      method: 'GET',
      path: '/api/water-quality',
      handler: getWaterQuality,
    },
    {
      method: 'GET',
      path: '/api/water-quality/predictions',
      handler: getPredictions,
    },
    {
      method: 'GET',
      path: '/api/water-quality/predictions/{id}', // Route untuk mendapatkan prediksi berdasarkan ID
      handler: getPredictionById,
    },
    {
      method: 'PUT',
      path: '/api/water-quality/{id}',  // Route untuk mengupdate data kualitas air berdasarkan ID
      handler: updateWaterQuality,
    },
    {
      method: 'DELETE',
      path: '/api/water-quality/{id}',  // Route untuk menghapus data kualitas air berdasarkan ID
      handler: deleteWaterQuality,
    },
  ];
  
  module.exports = waterQualityRoutes;
  