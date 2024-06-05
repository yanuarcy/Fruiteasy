const admin = require('firebase-admin');
const firebase = require('firebase/app');
require('firebase/auth');

const serviceAccount = require('./test-capstone-ce7f9-firebase-adminsdk-q5zv2-53cf45d6cb.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

firebase.initializeApp({
    apiKey: "AIzaSyDlSzHpMgLCUtPcRCOfySrZAtsiy7_A-D4",
    authDomain: "test-capstone-ce7f9.firebaseapp.com",
    projectId: "test-capstone-ce7f9",
    storageBucket: "test-capstone-ce7f9.appspot.com",
    messagingSenderId: "752231842091",
    appId: "1:752231842091:web:5653cf33542b5bd12b856e",
    measurementId: "G-GMJS2YBNQ0"
});

const db = admin.firestore();

module.exports = { admin, db, firebase };
