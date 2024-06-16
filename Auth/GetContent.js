const express = require('express');
const csv = require('csv-parser');
const fs = require('fs');

const router = express.Router();

// Fungsi untuk mencari data berdasarkan bulan
function getDataByMonth(data, month) {
    return data.filter(item => parseInt(item.month) === month);
}
// Path ke file CSV
const csvOlahanBuah = './csv/content-fruits.csv';

// Fungsi untuk membaca data dari file CSV
function fetchDataFromCSV(csvFilePath) {
    return new Promise((resolve, reject) => {
        const results = [];

        fs.createReadStream(csvFilePath)
        .pipe(csv())
        .on('data', (data) => results.push(data))
        .on('end', () => resolve(results))
        .on('error', (error) => reject(error));
    });
}

// Endpoint GET untuk mengambil data berdasarkan bulan saat ini
router.get('/content-fruit/current-month', async (req, res) => {
    try {
        // Mendapatkan bulan saat ini dalam angka
        const currentMonth = new Date().getMonth() + 1; // getMonth() mengembalikan bulan dari 0-11, jadi tambahkan 1

        // Ambil data dari CSV
        const data = await fetchDataFromCSV(csvOlahanBuah);
    
        // Cari data berdasarkan bulan saat ini
        const filteredData = getDataByMonth(data, currentMonth);
    
        res.status(200).json(filteredData);
    } catch (error) {
        console.error('Error:', error);
        res.status(500).json({ error: 'Terjadi kesalahan saat mengambil data' });
    }
});

module.exports = router;