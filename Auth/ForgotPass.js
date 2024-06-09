const express = require('express');
const bcrypt = require('bcryptjs');
const db = require('./firebase');

const { v4: uuidv4 } = require('uuid');
const { MailtrapClient } = require("mailtrap");

const router = express.Router();

// Mailtrap configuration
const TOKEN = '8f611029e2e95bbb8e6b5abc92bdb8bc';
const ENDPOINT = "https://send.api.mailtrap.io/";
const client = new MailtrapClient({ endpoint: ENDPOINT, token: TOKEN });

// Endpoint to request password reset link
router.post('/request-password-link', async (req, res) => {
    const { email } = req.body;

    if (!email) {
        return res.status(400).send("Please enter your email");
    }

    try {
        const userDocSnapshot = await db.collection("users").where("email", "==", email).get();

        if (userDocSnapshot.empty) {
            return res.status(404).send("User not found");
        }

        const resetToken = uuidv4();
        const resetLink = `https://fruiteasy-be-nrw674jbdq-et.a.run.app/ForgotPass/reset-password-view?token=${resetToken}`;

        // Store reset token in database with expiry time (e.g., 24 hours)
        const userDocRef = userDocSnapshot.docs[0].ref;
        await userDocRef.update({
            resetToken: resetToken,
            resetTokenExpiry: Date.now() + 24 * 60 * 60 * 1000 // 24 hours
        });

        const sender = {
            email: "mailtrap@demomailtrap.com",
            name: "Fruiteasy Team",
        };
        const recipients = [
            {
                email: 'yanuarcahyo567@gmail.com', // For testing purposes; replace with `email` in production
            }
        ];

        client
            .send({
                from: sender,
                to: recipients,
                template_uuid: "9ff1dbec-c15c-4552-a6f7-9352305923e5",
                template_variables: {
                    "pass_reset_link": resetLink,
                    "user_email": 'yanuarcahyo567@gmail.com' // For testing purposes; replace with `email` in production
                }
            })
            .then(response => {
                console.log("Email sent successfully!", response);
                res.status(200).send("Password reset link sent successfully!");
            })
            .catch(error => {
                console.error("Error sending email:", error);
                res.status(500).send("Error sending email: " + error.message);
            });

    } catch (error) {
        res.status(500).send('Error requesting password reset: ' + error.message);
    }
});

// Endpoint to return the reset password form
router.get('/reset-password-view', (req, res) => {
    const token = req.query.token;

    res.send(`
    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>Password Reset</title>
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <style type="text/css">
        body {
          margin: 0;
          padding: 0;
          -webkit-text-size-adjust: 100%;
          -ms-text-size-adjust: 100%;
        }
        table, td {
          border-collapse: collapse;
          mso-table-lspace: 0pt;
          mso-table-rspace: 0pt;
        }
        #outlook a { padding: 0; }
        .ReadMsgBody { width: 100%; }
        .ExternalClass { width: 100%; }
        .ExternalClass * { line-height: 100%; }
      </style>
      <style type="text/css">
        @media only screen and (max-width:480px) {
          @-ms-viewport { width: 320px; }
          @viewport { width: 320px; }
        }
      </style>
      <link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap" rel="stylesheet" type="text/css">
      <style type="text/css">
        @import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap');
        @media only screen and (max-width:595px) {
          .container { width: 100% !important; }
          .button { display: block !important; width: auto !important; }
        }
      </style>
    </head>
    <body style="font-family: 'Open Sans', sans-serif; background: #E5E5E5;">
      <table width="100%" cellspacing="0" cellpadding="0" border="0" align="center" bgcolor="#F6FAFB">
        <tbody>
          <tr>
            <td valign="top" align="center">
              <table class="container" width="600" cellspacing="0" cellpadding="0" border="0">
                <tbody>
                  <tr>
                    <td style="padding:48px 0 30px 0; text-align: center; font-size: 14px; color: #4C83EE;">
                      Fruiteasy
                    </td>
                  </tr>
                  <tr>
                    <td class="main-content" style="padding: 48px 30px 40px; color: #000000;" bgcolor="#ffffff">
                      <table width="100%" cellspacing="0" cellpadding="0" border="0">
                        <tbody>
                          <tr>
                            <td style="padding: 0 0 24px 0; font-size: 18px; line-height: 150%; font-weight: bold; color: #000000; letter-spacing: 0.01em;">
                              Reset Your Password
                            </td>
                          </tr>
                          <tr>
                            <td style="padding: 0 0 10px 0; font-size: 14px; line-height: 150%; font-weight: 400; color: #000000; letter-spacing: 0.01em;">
                              Please enter your new password below.
                            </td>
                          </tr>
                          <tr>
                            <td style="padding: 0 0 16px 0;">
                              <form action="/ForgotPass/reset-password-link" method="POST">
                                <input type="hidden" name="token" value="${token}" />
                                <label for="password" style="font-size: 14px; color: #000000;">New Password:</label>
                                <input type="password" name="password" required style="width: 100%; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 5px;" />
                                <button type="submit" style="width: 100%; background: #22D172; text-decoration: none; padding: 10px 0; color: #fff; font-size: 14px; line-height: 21px; text-align: center; font-weight: bold; border-radius: 7px;">Reset Password</button>
                              </form>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding: 0 0 16px;">
                              <span style="display: block; width: 117px; border-bottom: 1px solid #8B949F;"></span>
                            </td>
                          </tr>
                          <tr>
                            <td style="font-size: 14px; line-height: 170%; font-weight: 400; color: #000000; letter-spacing: 0.01em;">
                              Best regards, <br><strong>Fruiteasy</strong>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding: 24px 0 48px; font-size: 0px;">
                      <div class="outlook-group-fix" style="padding: 0 0 20px 0; vertical-align: top; display: inline-block; text-align: center; width:100%;">
                        <span style="padding: 0; font-size: 11px; line-height: 15px; font-weight: normal; color: #8B949F;">Fruiteasy &copy 2024<br/>East Java, Indonesia</span>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </td>
          </tr>
        </tbody>
      </table>
    </body>
    </html>
  `);
});

// Endpoint to reset the password
router.post('/reset-password-link', async (req, res) => {
    const { token, password } = req.body;

    if (!token || !password) {
        return res.status(400).send("Please fill in all the fields");
    }

    try {
        const userDocSnapshot = await db.collection("users").where("resetToken", "==", token).get();

        if (userDocSnapshot.empty) {
            return res.status(404).send("Invalid or expired token");
        }

        const userDocRef = userDocSnapshot.docs[0].ref;
        const user = userDocSnapshot.docs[0].data();

        if (user.resetTokenExpiry < Date.now()) {
            return res.status(400).send("Token has expired");
        }

        const hashedNewPassword = await bcrypt.hash(password, 10);

        // Update user's password and remove reset token
        await userDocRef.update({
            password: hashedNewPassword,
            resetToken: null,
            resetTokenExpiry: null
        });

        res.status(200).send(`
      <!DOCTYPE html>
      <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Password Reset Success</title>
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style type="text/css">
          body {
            margin: 0;
            padding: 0;
            -webkit-text-size-adjust: 100%;
            -ms-text-size-adjust: 100%;
          }
          table, td {
            border-collapse: collapse;
            mso-table-lspace: 0pt;
            mso-table-rspace: 0pt;
          }
          #outlook a { padding: 0; }
          .ReadMsgBody { width: 100%; }
          .ExternalClass { width: 100%; }
          .ExternalClass * { line-height: 100%; }
        </style>
        <style type="text/css">
          @media only screen and (max-width:480px) {
            @-ms-viewport { width: 320px; }
            @viewport { width: 320px; }
          }
        </style>
        <link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap" rel="stylesheet" type="text/css">
        <style type="text/css">
          @import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap');
          @media only screen and (max-width:595px) {
            .container { width: 100% !important; }
            .button { display: block !important; width: auto !important; }
          }
        </style>
      </head>
      <body style="font-family: 'Open Sans', sans-serif; background: #E5E5E5;">
        <table width="100%" cellspacing="0" cellpadding="0" border="0" align="center" bgcolor="#F6FAFB">
          <tbody>
            <tr>
              <td valign="top" align="center">
                <table class="container" width="600" cellspacing="0" cellpadding="0" border="0">
                  <tbody>
                    <tr>
                      <td style="padding:48px 0 30px 0; text-align: center; font-size: 14px; color: #4C83EE;">
                        Fruiteasy
                      </td>
                    </tr>
                    <tr>
                      <td class="main-content" style="padding: 48px 30px 40px; color: #000000;" bgcolor="#ffffff">
                        <table width="100%" cellspacing="0" cellpadding="0" border="0">
                          <tbody>
                            <tr>
                              <td style="padding: 0 0 24px 0; font-size: 18px; line-height: 150%; font-weight: bold; color: #000000; letter-spacing: 0.01em;">
                                Password Reset Successfully
                              </td>
                            </tr>
                            <tr>
                              <td style="padding: 0 0 10px 0; font-size: 14px; line-height: 150%; font-weight: 400; color: #000000; letter-spacing: 0.01em;">
                                Your password has been reset successfully. You can now return to the app and log in with your new password.
                              </td>
                            </tr>
                            <tr>
                              <td style="padding: 0 0 16px;">
                                <span style="display: block; width: 117px; border-bottom: 1px solid #8B949F;"></span>
                              </td>
                            </tr>
                            <tr>
                              <td style="font-size: 14px; line-height: 170%; font-weight: 400; color: #000000; letter-spacing: 0.01em;">
                                Best regards, <br><strong>Fruiteasy</strong>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding: 24px 0 48px; font-size: 0px;">
                        <div class="outlook-group-fix" style="padding: 0 0 20px 0; vertical-align: top; display: inline-block; text-align: center; width:100%;">
                          <span style="padding: 0; font-size: 11px; line-height: 15px; font-weight: normal; color: #8B949F;">Fruiteasy &copy 2024<br/>East Java, Indonesia</span>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </td>
            </tr>
          </tbody>
        </table>
      </body>
      </html>
    `);
    } catch (error) {
        res.status(500).send('Error resetting password: ' + error.message);
    }
});

module.exports = router;