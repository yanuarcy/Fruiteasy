const express = require('express');
const bcrypt = require('bcryptjs');
const db = require('./firebase');

const router = express.Router();

// Function to generate random ID
const generateRandomID = () => {
  const min = 10000; // Minimal angka acak (4 digit)
  const max = 99999; // Maksimal angka acak (5 digit)
  return Math.floor(Math.random() * (max - min + 1)) + min;
};

const createCustomID = () => {
  const prefix = "120"; // Angka yang akan disisipkan di depan ID
  const randomNumber = generateRandomID();
  const customID = `${prefix}${randomNumber}`;
  return customID;
};

// Endpoint to request email verification
router.post('/request-verify-email', async (req, res) => {
  const { username, email, phone, password, confirmPassword } = req.body;

  if (!username || !email || !phone || !password || !confirmPassword) {
    return res.status(400).json({ error: 'Please enter all data correctly' });
  }

  if (password !== confirmPassword) {
    return res.status(400).send('Passwords do not match');
  }

  // Send verification email using Mailtrap
  const verificationLink = `https://fruiteasy-be-nrw674jbdq-et.a.run.app/UserAuth/signup?username=${username}&email=${email}&phone=${phone}&password=${password}&confirmPassword=${confirmPassword}`;

  const client = new MailtrapClient({ endpoint: ENDPOINT, token: TOKEN });

  const sender = {
    email: "mailtrap@demomailtrap.com",
    name: "Mailtrap Test",
  };
  const recipients = [
    {
      email: 'yanuarcahyo567@gmail.com',
      // email: email,
    }
  ];

  client
    .send({
      from: sender,
      to: recipients,
      template_uuid: "5f931b82-5590-4d4f-b518-90a98f3fc746",
      template_variables: {
        "user_name": username,
        "verification_link": verificationLink,
      }
    })
    .then(() => {
      res.status(200).send('Verification email sent');
    })
    .catch(error => {
      res.status(500).send('Error sending verification email: ' + error.message);
    });
});

// Endpoint to create a new user after email verification
router.get('/signup', async (req, res) => {
  const { username, email, phone, password, confirmPassword } = req.query;

  if (!username || !email || !phone || !password || !confirmPassword) {
    return res.status(400).json({ error: 'Please enter all data correctly' });
  }

  if (password !== confirmPassword) {
    return res.status(400).send('Passwords do not match');
  }

  const userID = createCustomID().toString();
  const hashedPassword = await bcrypt.hash(password, 10);

  const newUser = {
    id: userID,
    username: username,
    email: email,
    phone: phone,
    password: hashedPassword,
    fullName: username,
    gender: "",
    dateOfBirth: "",
    address: "",
    cities: "",
  };

  try {
    const usersRef = db.collection('users');
    const snapshotEmail = await usersRef.where('email', '==', email).get();

    if (!snapshotEmail.empty) {
      return res.status(400).send('Email already in use');
    }

    const newDoc = await usersRef.add(newUser);
    const uid = newDoc.id;
    const userDocRef = db.collection('users').doc(uid);

    await userDocRef.set({ ...newUser, uid: uid });
    res.status(201).send(`
    <!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title></title>
<!--[if !mso]><!-- -->
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!--<![endif]-->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<style type="text/css">
  #outlook a {
    padding: 0;
  }

  .ReadMsgBody {
    width: 100%;
  }

  .ExternalClass {
    width: 100%;
  }

  .ExternalClass * {
    line-height: 100%;
  }

  body {
    margin: 0;
    padding: 0;
    -webkit-text-size-adjust: 100%;
    -ms-text-size-adjust: 100%;
  }

  table,
  td {
    border-collapse: collapse;
    mso-table-lspace: 0pt;
    mso-table-rspace: 0pt;
  }

</style>
<!--[if !mso]><!-->
<style type="text/css">
  @media only screen and (max-width:480px) {
    @-ms-viewport {
      width: 320px;
    }
    @viewport {
      width: 320px;
    }
  }
</style>
<!--<![endif]-->
<!--[if mso]><xml>  <o:OfficeDocumentSettings>    <o:AllowPNG/>    <o:PixelsPerInch>96</o:PixelsPerInch>  </o:OfficeDocumentSettings></xml><![endif]-->
<!--[if lte mso 11]><style type="text/css">  .outlook-group-fix {    width:100% !important;  }</style><![endif]-->
<!--[if !mso]><!-->
<link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap" rel="stylesheet" type="text/css">
<style type="text/css">
  @import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600&display=swap');
</style>
<!--<![endif]-->
<style type="text/css">
  @media only screen and (max-width:595px) {
    .container {
      width: 100% !important;
    }
    .button {
      display: block !important;
      width: auto !important;
    }
  }
</style>
</head>

<body style="font-family: 'Inter', sans-serif; background: #E5E5E5;">
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
                        Email Verified Successfully!
                      </td>
                    </tr>
                    <tr>
                      <td style="padding: 0 0 10px 0; font-size: 14px; line-height: 150%; font-weight: 400; color: #000000; letter-spacing: 0.01em;">
                        Congratulations, ${username} ! Your email has been successfully verified and your account has been created.
                      </td>
                    </tr>
                    <tr>
                      <td style="padding: 0 0 16px 0; font-size: 14px; line-height: 150%; font-weight: 400; color: #000000; letter-spacing: 0.01em;">
                        You can now return to the application and log in with your newly created account.
                      </td>
                    </tr>
                    <tr>
                      <td style="padding: 0 0 60px;">
                        If you did not request this email, please contact our support at <a href="mailto:fruiteasy7@gmail.com">Fruiteasy</a>.
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
                <!--[if mso | IE]>      <table role="presentation" border="0" cellpadding="0" cellspacing="0">        <tr>          <td style="vertical-align:top;width:300px;">      <![endif]-->
                <div class="outlook-group-fix" style="padding: 0 0 20px 0; vertical-align: top; display: inline-block; text-align: center; width:100%;">
                  <span style="padding: 0; font-size: 11px; line-height: 15px; font-weight: normal; color: #8B949F;">Fruiteasy &copy 2024<br/>East Java, Indonesia</span>
                </div>
                <!--[if mso | IE]>      </td></tr></table>      <![endif]-->
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
    res.status(500).send('Error creating user: ' + error.message);
  }
});

// Endpoint to login
router.post('/login', async (req, res) => {
  try {
    const { emailOrUsername, password } = req.body;

    // Validasi input
    if (!emailOrUsername || !password) {
      return res.status(400).json({ error: 'Please enter all data correctly' });
    }

    // Verifikasi kredensial
    const emailSnapshot = await db.collection("users").where("email", "==", emailOrUsername).get();
    const usernameSnapshot = await db.collection("users").where("username", "==", emailOrUsername).get();

    const combinedSnapshot = emailSnapshot.docs.concat(usernameSnapshot.docs);

    if (combinedSnapshot.length === 0) {
      return res.status(404).json({ error: 'Data is not found' });
    }

    const userData = combinedSnapshot[0].data();
    const hashedPassword = userData.password;

    // Memeriksa kata sandi
    const passwordMatch = await bcrypt.compare(password, hashedPassword);
    if (!passwordMatch) {
      return res.status(401).json({ error: 'Wrong password' });
    }

    // Jika berhasil, kirim respons berhasil bersama dengan data pengguna
    return res.status(200).json({ message: 'Login Success', user: userData });
  } catch (error) {
    console.error('Error:', error);
    return res.status(500).json({ error: 'An error occurred during the login process', details: error.message });
  }
});

// Endpoint to Edit User Profile
router.post('/edit-profile', async (req, res) => {
  try {
    const userId = req.body.uid;
    const userData = req.body;

    // Validate the required fields (e.g., email, phone, etc.)
    const requiredFields = ['email', 'phone', 'fullName', 'gender', 'dateOfBirth', 'address', 'cities'];
    for (const field of requiredFields) {
      if (!userData[field]) {
        return res.status(400).send(`Field ${field} is required`);
      }
    }

    // Reference to the user document
    const userRef = db.collection('users').doc(userId);

    // Check if the user document exists
    const userDoc = await userRef.get();
    if (!userDoc.exists) {
      return res.status(404).send('User not found');
    }

    // Update user data in Firestore
    await userRef.set(userData, { merge: true });

    res.status(200).send('Profile updated successfully');
  } catch (error) {
    res.status(500).send('Error updating profile: ' + error.message);
  }
});

// Endpoint to reset password
router.post('/reset-password', async (req, res) => {
  const { uidLocal, currentPassword, newPassword, confirmNewPassword } = req.body;

  // Validasi input
  if (!currentPassword || !newPassword || !confirmNewPassword) {
    return res.status(400).json({ error: 'Please enter all data correctly' });
  }

  try {
    if (newPassword !== confirmNewPassword) {
      return res.status(400).send('New passwords do not match');
    }

    const userDocRef = db.collection("users").doc(uidLocal);
    const userDocSnapshot = await userDocRef.get();

    if (!userDocSnapshot.exists) {
      return res.status(404).json({ error: 'User not found' });
    }

    const user = userDocSnapshot.data();
    const userPassword = user.password;
    const isPasswordValid = await bcrypt.compare(currentPassword, userPassword);

    if (!isPasswordValid) {
      return res.status(400).send('Current password is incorrect');
    }

    const isNewPasswordSameAsOld = await bcrypt.compare(newPassword, userPassword);

    if (isNewPasswordSameAsOld) {
      return res.status(400).send('New password must be different from old password');
    }

    const hashedNewPassword = await bcrypt.hash(newPassword, 10);

    // Update user password
    await userDocRef.update({ password: hashedNewPassword });
    res.status(200).send('Password changed successfully');
  } catch (error) {
    res.status(500).send('Error changing password: ' + error.message);
  }
});

module.exports = router;
