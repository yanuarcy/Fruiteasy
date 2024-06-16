const express = require("express");
const { MailtrapClient } = require("mailtrap");

require('dotenv').config();

const router = express.Router();

const TOKEN = process.env.MAILTRAP_TOKEN;
const ENDPOINT = "https://send.api.mailtrap.io/";

const client = new MailtrapClient({ endpoint: ENDPOINT, token: TOKEN });

router.post('/contact-us', async (req, res) => {
    const { name, email, phone, message } = req.body;

    // Validasi sederhana
    if (!name || !email || !phone || !message) {
        return res.status(400).json({ error: 'All fields are required' });
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
        template_uuid: "56a2a591-d0c3-4f20-b070-239b077c04a9",
        template_variables: {
            "user_name": name,
            "user_email": email,
            "user_phone": phone,
            "user_message": message
        }
        })
        .then(() => {
        res.status(200).send('Contact Us sent successfully');
        })
        .catch(error => {
        res.status(500).send('Error sending Contact Us: ' + error.message);
        });
});

module.exports = router;