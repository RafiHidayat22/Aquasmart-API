const { Storage } = require('@google-cloud/storage');
const Multer = require('multer');

const storage = new Storage();


const bucketName = 'foto-profile-aquasmart';
const bucket = storage.bucket(bucketName);

const multer = Multer({
  storage: Multer.memoryStorage(),
  limits: { fileSize: 5 * 1024 * 1024 }, // Batas file 5MB
});

const uploadFile = (file) => {
  return new Promise((resolve, reject) => {
    if (!file) return reject(new Error('File tidak ditemukan.'));

    const blob = bucket.file(`${Date.now()}_${file.originalname}`);
    const blobStream = blob.createWriteStream({ resumable: false });

    blobStream
      .on('error', reject)
      .on('finish', () => {
        const publicUrl = `https://storage.googleapis.com/${bucketName}/${blob.name}`;
        resolve(publicUrl);
      })
      .end(file.buffer);
  });
};

module.exports = { multer, uploadFile };
