const express = require('express');
const bodyParser = require('body-parser');
const authRoutes = require('./Auth/UserAuth'); // Import UserAuth routes
const forgotPassRoutes = require('./Auth/ForgotPass'); // Import ForgotPass.js routes
const predictRoutes = require('./Auth/Predict'); // Import Predict.js routes
const contentRoutes = require('./Auth/GetContent'); // Import GetContent.js routes
const reportBugRoutes = require('./CS-Support/ReportBug'); // Import ReportBug.js routes
const contactUsRoutes = require('./CS-Support/ContactUs'); // Import ContactUs.js routes

require('dotenv').config();

const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());

app.use('/UserAuth', authRoutes); // Use the auth routes with '/UserAuth' prefix
app.use('/ForgotPass', forgotPassRoutes); 
app.use('/Predict', predictRoutes); 
app.use('/GetContent', contentRoutes);
app.use('/Support', [reportBugRoutes, contactUsRoutes]);

const PORT = process.env.PORT || 3030;
const HOST = '0.0.0.0'; // Listen on all interfaces

app.listen(PORT, HOST, () => {
    console.log(`Server is running on http://${HOST}:${PORT}`);
});