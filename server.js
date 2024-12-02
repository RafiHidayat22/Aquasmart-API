const Hapi = require('@hapi/hapi');
const { Storage } = require('@google-cloud/storage');
const storage = new Storage();

// Ganti dengan nama bucketmu
const bucketName = 'model-aquasmart';

// Path ke file model di bucket
const modelFileName = 'model-aquasmart/model1/model.json';
const binFileName = 'model-aquasmart/model1/group1-shard1of1.bin';

// Fungsi untuk mengunduh file dari Google Cloud Storage
const loadModelFromStorage = async () => {
    try {
        // Mengakses file model.json
        const modelFile = storage.bucket(bucketName).file(modelFileName);
        const modelData = await modelFile.download();

        // Mengakses file group1-shard1of1.bin
        const binFile = storage.bucket(bucketName).file(binFileName);
        const binData = await binFile.download();

        // Kembalikan data model dan bin
        return { modelData, binData };
    } catch (err) {
        console.error('Error loading model from storage:', err);
        throw new Error('Unable to load model from storage');
    }
};

// Fungsi untuk menginisialisasi server Hapi
const init = async () => {
    const server = Hapi.server({
        port: 3000,
        host: '0.0.0.0',
    });

    // Route untuk mengakses model
    server.route({
        method: 'GET',
        path: '/model',
        handler: async (request, h) => {
            try {
                const { modelData, binData } = await loadModelFromStorage();

                // Kamu bisa melakukan proses dengan model dan bin data di sini
                // Misalnya, return data sebagai JSON atau proses inferensi model

                return h.response({
                    modelJson: modelData.toString(), // Atau parsing JSON jika perlu
                    binData: binData.toString('base64'), // Mengembalikan bin dalam bentuk base64
                }).code(200);
            } catch (err) {
                return h.response('Error loading model').code(500);
            }
        },
    });

    await server.start();
    console.log('Server running on %s', server.info.uri);
};

init();
