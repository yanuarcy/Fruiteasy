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
  const fullName = username;

  if (password !== confirmPassword) {
    return res.status(400).send('Passwords do not match');
  }

  const userID = createCustomID().toString();
  const hashedPassword = await bcrypt.hash(password, 10);

  const newUser = {
    username,
    userID,
    fullName,
    email,
    phone,
    password: hashedPassword,
  };

  try {
    const usersRef = db.collection('users');
    const snapshot = await usersRef.where('email', '==', email).get();

    if (!snapshot.empty) {
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
  const { emailOrFullName, password } = req.body;

  try {
    const emailSnapshot = await db.collection("users").where("email", "==", emailOrFullName).get();
    const usernameSnapshot = await db.collection("users").where("fullName", "==", emailOrFullName).get();

    const combinedSnapshot = emailSnapshot.docs.concat(usernameSnapshot.docs);

    if (combinedSnapshot.length === 0) {
      return res.status(400).send('No user found with this email or username');
    }

    let user;
    combinedSnapshot.forEach(doc => {
      user = doc.data();
    });

    const isPasswordValid = await bcrypt.compare(password, user.password);

    if (!isPasswordValid) {
      return res.status(400).send('Invalid password');
    }

    res.status(200).send('Login successful');
  } catch (error) {
    res.status(500).send('Error logging in: ' + error.message);
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

// Endpoint to change password
router.post('/change-password', verifyToken, async (req, res) => {
  const { currentPassword, newPassword, confirmNewPassword } = req.body;

  if (newPassword !== confirmNewPassword) {
    return res.status(400).send('New passwords do not match');
  }

  try {
    const userRef = db.collection('users').doc(req.userId);
    const userDoc = await userRef.get();

    if (!userDoc.exists) {
      return res.status(400).send('User not found');
    }

    const user = userDoc.data();
    const isPasswordValid = await bcrypt.compare(currentPassword, user.password);

    if (!isPasswordValid) {
      return res.status(400).send('Current password is incorrect');
    }

    const hashedNewPassword = await bcrypt.hash(newPassword, 10);
    await userRef.update({ password: hashedNewPassword });

    res.status(200).send('Password changed successfully');
  } catch (error) {
    res.status(500).send('Error changing password: ' + error.message);
  }
});

module.exports = router;