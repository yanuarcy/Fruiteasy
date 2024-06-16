const express = require('express');
const multer = require('multer');
const axios = require('axios');
const fs = require('fs');
const path = require('path');
const FormData = require('form-data');
const csv = require('csv-parser');
const db = require('../firebase');

require('dotenv').config();

const router = express.Router();

const csvFilePath = process.env.CSV_DATABUAH;

// Fungsi untuk membaca data dari file CSV
async function fetchDataFromCSV(csvFilePath) {
    try {
        const response = await axios({
            method: 'get',
            url: csvFilePath,
            responseType: 'stream'
        });

        return new Promise((resolve, reject) => {
            const results = [];
            response.data
                .pipe(csv())
                .on('data', (data) => results.push(data))
                .on('end', () => resolve(results))
                .on('error', (error) => reject(error));
        });
    } catch (error) {
        throw new Error(`Error fetching CSV data: ${error.message}`);
    }
}

// Fungsi untuk mencocokkan nama buah dengan data dari file CSV
function getFruitData(fruitName, fruitData) {
    return fruitData.find(item => item.nama_buah.toLowerCase() === fruitName.toLowerCase());
}

// Konfigurasi Multer untuk menyimpan file dengan nama asli
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'Auth/uploads/');
    },
    filename: function (req, file, cb) {
        cb(null, file.originalname);
    }
});

const upload = multer({ storage: storage });

// Function to generate random ID
const generateRandomID = () => {
    const min = 10000; // Minimal angka acak (4 digit)
    const max = 99999; // Maksimal angka acak (5 digit)
    return Math.floor(Math.random() * (max - min + 1)) + min;
};

const createCustomID = () => {
    const prefix = "220"; // Angka yang akan disisipkan di depan ID
    const randomNumber = generateRandomID();
    const customID = `${prefix}${randomNumber}`;
    return customID;
};

// Endpoint POST untuk menyimpan riwayat
router.post('/history', async (req, res) => {
    try {
        const { userLocalid, fruitName } = req.body;

        const historyID = createCustomID().toString();
        const createdAt = new Date().toISOString();

        // Simpan data ke Firestore
        const docRef = await db.collection('history').add({
            historyID,
            userLocalid,
            fruitName,
            createdAt
        });

        res.status(201).json({ message: 'Riwayat berhasil disimpan', id: docRef.id });
    } catch (error) {
        console.error('Error:', error);
        res.status(500).json({ error: 'Terjadi kesalahan saat menyimpan riwayat' });
    }
});

// Endpoint GET untuk mendapatkan riwayat beserta data buah
router.get('/get-history', async (req, res) => {
    const { userLocalid } = req.query;

    try {
        // Ambil riwayat dari Firestore berdasarkan userLocalid
        const historySnapshot = await db.collection('history').where('userLocalid', '==', userLocalid).get();
        if (historySnapshot.empty) {
            return res.status(404).json({ message: 'Belum memiliki riwayat' });
        }
        const history = [];

        // Ambil data buah dari CSV
        const fruitData = await fetchDataFromCSV(csvFilePath);

        // Gabungkan riwayat dengan data buah
        historySnapshot.forEach(doc => {
            const { userLocalid, fruitName, createdAt } = doc.data();
            const fruit = getFruitData(fruitName, fruitData);

            history.push({
                userLocalid,
                fruitName,
                createdAt,
                fruitData: fruit // Data buah dari CSV
            });
        });

        res.status(200).json(history);
    } catch (error) {
        console.error('Error:', error);
        res.status(500).json({ error: 'Terjadi kesalahan saat mengambil riwayat' });
    }
});

//Endpoint POST Upload gambar input user
router.post('/upload', upload.single('image'), async (req, res) => {
    try {
        // Path file yang di-upload
        const filePath = path.join(__dirname, 'uploads', req.file.originalname);
        console.log(filePath)
        // Membuat form data
        const form = new FormData();
        form.append('file', fs.createReadStream(filePath));

        // Mengirim request ke Flask API
        const response = await axios.post(process.env.MODEL_URL, form, { //GANTI URL INI
            headers: {
                ...form.getHeaders(),
                'Content-Type': 'multipart/form-data'
            }
        });

        // Menghapus file setelah upload
        fs.unlinkSync(filePath);

        // // Mengembalikan response dari Flask API ke client
        // const predictionResult = response.data;

        // // Mendapatkan userLocalid dari request body
        // const { userLocalid } = req.body;

        // // Simpan data riwayat
        // const historyData = {
        //     userLocalid,
        //     fruitName: predictionResult.nama_buah,
        // };

        // // Kirim data riwayat ke endpoint /history
        // await axios.post(process.env.HISTORY_URL, historyData); //GANTI URL INI

        // Mengembalikan respons prediksi ke client
        res.json(response.data);
    } catch (error) {
        console.error(error.response ? error.response.data : error.message);
        res.status(500).send(error.response ? error.response.data : 'Something went wrong!');
    }
});

module.exports = router;