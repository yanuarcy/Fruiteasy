const express = require('express');
const bcrypt = require('bcryptjs');
const { admin, db, firebase } = require('./firebase');

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

// Middleware to verify Firebase ID token
const verifyToken = async (req, res, next) => {
  const idToken = req.headers.authorization?.split('Bearer ')[1];

  if (!idToken) {
    return res.status(401).send('Unauthorized: No token provided');
  }

  try {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    req.userId = decodedToken.uid;
    next();
  } catch (error) {
    res.status(401).send('Unauthorized: Invalid token');
  }
};

// Endpoint to create a new user
router.post('/signup', async (req, res) => {
  const { username, email, phone, password, confirmPassword } = req.body;
  
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
    cities:"",
  };

  try {
    const usersRef = db.collection('users');
    const snapshotEmail = await usersRef.where('email', '==', email).get();

    if (!snapshotEmail.empty) {
      return res.status(400).send('Email already in use');
    }

    await usersRef.add(newUser);
    res.status(201).send('User created successfully');
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

// Tahap Pengembangan
// Endpoint to get user data
router.get('/profile', verifyToken, async (req, res) => {
  try {
      const userRef = db.collection('users').doc(req.userId);
      const userDoc = await userRef.get();

      if (!userDoc.exists) {
          return res.status(400).send('User not found');
      }

      res.status(200).json(userDoc.data());
  } catch (error) {
      res.status(500).send('Error fetching user data: ' + error.message);
  }
});

// Endpoint to request password reset
router.post('/request-reset-password', async (req, res) => {
  const { email } = req.body;

  try {
    await firebase.auth().sendPasswordResetEmail(email);
    res.status(200).send('Password reset link sent to your email');
  } catch (error) {
    res.status(500).send('Error requesting password reset: ' + error.message);
  }
});

// Endpoint to reset password
router.post('/reset-password', verifyToken, async (req, res) => {
  const { userIdLocal, currentPassword, newPassword, confirmNewPassword } = req.body;

  // Validasi input
  if (!currentPassword || !newPassword || !confirmNewPassword) {
    return res.status(400).json({ error: 'Please enter all data correctly' });
  }

  if (newPassword !== confirmNewPassword) {
    return res.status(400).send('New passwords do not match');
  }

  try {
    const userCollection = await db.collection('users');
    const userDocRef = doc (userCollection, userIdLocal);

    await updateDoc(userDocRef, {password: hashedNewPassword});

    if (userSnapshot.empty) {
      return res.status(404).json({ error: 'User not found' });
    }
  
    const user = userSnapshot.docs[0].data();
    const userPassword = user.password;
    const isPasswordValid = await bcrypt.compare(currentPassword, userPassword);
  
    if (!isPasswordValid) {
      return res.status(400).send('Current password is incorrect');
    }
  
    const hashedNewPassword = await bcrypt.hash(newPassword, 10);
    
    // Update kata sandi pengguna
    await db.collection("users").doc(userSnapshot.docs[0].id).update({ password: hashedNewPassword });

    res.status(200).send('Password changed successfully');
  } catch (error) {
    res.status(500).send('Error changing password: ' + error.message);
  }
});

module.exports = router;
