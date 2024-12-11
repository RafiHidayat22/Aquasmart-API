/* eslint-disable linebreak-style */
/* eslint-disable indent */
/* eslint-disable linebreak-style */
/* eslint-disable no-trailing-spaces */
const { verifyToken } = require('../middlewares/verifyToken');
const { 
  getFishTypes, addPrediksiPanen, getPrediksiPanen, deletePrediksiPanen
  
  } = require('../handler/prediksiPanenHandler');
  
  const prediksiPanenRoutes = [
    {
      method: 'GET',
      path: '/api/fish-types',
      handler: getFishTypes
  },
    {
        method: 'POST',
        path: '/prediksiPanen',
        handler: addPrediksiPanen, // Menambahkan prediksi panen
        options: {
            pre: [{ method: verifyToken }]
          },
      },
      {
        method: 'GET',
        path: '/prediksiPanen',
        handler: getPrediksiPanen, // Mengambil prediksi panen berdasarkan user
        options: {
            pre: [{ method: verifyToken }]
          },
      },
      {
        method: 'DELETE',
        path: '/prediksi/{id}',
        handler: deletePrediksiPanen, // Menghapus prediksi panen berdasarkan id
        options: {
            pre: [{ method: verifyToken }]
          },
      },
  ];
  
  module.exports = prediksiPanenRoutes;
  