const express = require('express');
const csv = require('csv-parser');
const axios = require('axios');

const router = express.Router();

// Fungsi untuk mencari data berdasarkan bulan
function getDataByMonth(data, month) {
    return data.filter(item => parseInt(item.month) === month);
}
// Path ke file CSV
const csvOlahanBuah = process.env.CSV_DATAOLAHAN;
const csvMusimBuah = process.env.CSV_DATAMUSIM;

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

// Fungsi untuk mengubah string menjadi array integer
function stringToArray(data, delimiter = ";") {
    if (data.trim() === "") {
        return [];
    }
    return data.split(delimiter).map(x => parseInt(x.trim(), 10));
}

// Endpoint GET untuk musim buah
router.get('/musim_buah', async (req, res) => {
    const getBulan = req.query.bulan;

    if (!getBulan) {
        return res.status(400).json({ error: 'Harap Masukkan Data Yang Sesuai' });
    } else if (!/^\d+$/.test(getBulan)) {
        return res.status(400).json({ error: 'Parameter bulan harus bernilai angka positif' });
    }

    try {
        const data = await fetchDataFromCSV(csvMusimBuah);
        const seasonContent = data.filter(season => {
            const arrayBulan = stringToArray(season['bulan']);
            return arrayBulan.includes(parseInt(getBulan, 10));
        }).map(season => ({
            Buah: season['buah'],
            Bulan: stringToArray(season['bulan']),
            Icon: season['icon']
        }));

        if (seasonContent.length !== 0) {
            res.json(seasonContent);
        } else {
            res.status(404).json({ error: 'Bulan tidak terdaftar saat ini' });
        }
    } catch (error) {
        console.error('Error:', error);
        res.status(500).json({ error: 'Terjadi kesalahan saat mengambil data' });
    }
});

module.exports = router;