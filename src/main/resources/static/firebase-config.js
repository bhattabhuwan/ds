// Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyBPV0xtWOD-STiI6_q_Oz2zcyx5vuTjAls",
  authDomain: "doc-sab.firebaseapp.com",
  databaseURL: "https://doc-sab-default-rtdb.asia-southeast1.firebasedatabase.app",
  projectId: "doc-sab",
  storageBucket: "doc-sab.firebasestorage.app",
  messagingSenderId: "163730831148",
  appId: "1:163730831148:web:8210a3b391eaa32fa0de89",
  measurementId: "G-7T9L4DLTDY"
};

// Initialize Firebase with retry capability
function initFirebase() {
  try {
    console.log("Initializing Firebase from config.js");
    if (typeof firebase !== 'undefined') {
      if (!firebase.apps || !firebase.apps.length) {
        firebase.initializeApp(firebaseConfig);
        console.log("Firebase initialized successfully");
        // Signal that Firebase is ready
        window.firebaseReady = true;
        // Dispatch event for components waiting on Firebase
        const event = new CustomEvent('firebase-ready');
        document.dispatchEvent(event);
      } else {
        console.log("Firebase already initialized");
        window.firebaseReady = true;
      }
    } else {
      console.error("Firebase SDK is not loaded. Retrying in 100ms");
      setTimeout(initFirebase, 100);
      return;
    }
  } catch (e) {
    console.error("Firebase initialization error:", e);
    document.addEventListener('DOMContentLoaded', function() {
      console.log("Adding error message to page");
      const errorDiv = document.createElement('div');
      errorDiv.style = "position:fixed; top:0; left:0; right:0; background-color:red; color:white; padding:10px; text-align:center; z-index:9999;";
      errorDiv.innerText = "Error initializing Firebase: " + e.message;
      document.body.appendChild(errorDiv);
    });
  }
}

// Start initialization
document.addEventListener('DOMContentLoaded', function() {
  initFirebase();
});

// Make firebaseConfig available globally
window.firebaseConfig = firebaseConfig;
