const express = require("express");
const { MailtrapClient } = require("mailtrap");

require('dotenv').config();

const router = express.Router();

const TOKEN = process.env.MAILTRAP_TOKEN;
const ENDPOINT = "https://send.api.mailtrap.io/";

const client = new MailtrapClient({ endpoint: ENDPOINT, token: TOKEN });

router.post('/report-bug', async (req, res) => {
    const { username, email, rating, message } = req.body;

    // Validasi input
    if (!email || !rating || !message) {
        return res.status(400).send('Semua field harus diisi');
    }

    const sender = {
        email: "mailtrap@fruiteasy.org",
        name: "Fruiteasy Team",
    };
    const recipients = [
        {
            email: "fruiteasyteam@gmail.com",
        }
    ];

    client
        .send({
            from: sender,
            to: recipients,
            template_uuid: "69de0e9e-81b4-4f22-a110-0ceaed245096",
            template_variables: {
                "user_email": email,
                "user_message": message,
                "user_feedback": rating,
                "username": username
            }
        })
        .then(() => {
            res.status(200).send('Report Bug sent successfully');
        })
        .catch(error => {
            res.status(500).send('Error sending Report Bug: ' + error.message);
        });
});

module.exports = router;