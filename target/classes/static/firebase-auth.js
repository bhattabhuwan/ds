// Firebase Auth Implementation - DoctorSab
// Auth handlers for Firebase Authentication

// Check if Firebase is initialized and available
function ensureFirebaseAuth() {
  if (!window.firebase || !firebase.auth) {
    console.error("Firebase Auth is not available");
    throw new Error("Firebase Auth is not available. Please check your internet connection.");
  }
  return true;
}

// Redirect helper function for post-login navigation
function redirectToDashboard() {
  console.log("Redirecting to dashboard");
  window.location.href = '/dashboard/user';
}

// Email/Password Authentication Functions
function loginWithEmailPassword(email, password) {
  ensureFirebaseAuth();
  console.log(`Attempting to login with email: ${email}`);
  
  return firebase.auth().signInWithEmailAndPassword(email, password)
    .then(userCredential => {
      console.log("Login successful");
      return { success: true, user: userCredential.user };
    })
    .catch(error => {
      console.error("Login error:", error.message);
      return { success: false, error: error.message };
    });
}

function registerWithEmailPassword(email, password, userData) {
  ensureFirebaseAuth();
  console.log(`Attempting to register email: ${email}, role: ${userData.role}`);
  
  return firebase.auth().createUserWithEmailAndPassword(email, password)
    .then(userCredential => {
      console.log("Registration successful, now storing user data");
      // Store additional user data in Firestore
      return storeUserData(userCredential.user.uid, userData)
        .then(() => {
          return { success: true, user: userCredential.user };
        });
    })
    .catch(error => {
      console.error("Registration error:", error.message);
      return { success: false, error: error.message };
    });
}

// Google Authentication
function loginWithGoogle() {
  ensureFirebaseAuth();
  console.log("Attempting Google login");
  const provider = new firebase.auth.GoogleAuthProvider();
  
  // Configure Google provider
  provider.addScope('https://www.googleapis.com/auth/userinfo.email');
  provider.addScope('https://www.googleapis.com/auth/userinfo.profile');
  
  provider.setCustomParameters({
    prompt: 'select_account'
  });
  
  return firebase.auth().signInWithPopup(provider)
    .then(result => {
      // Check if this is a new user
      const isNewUser = result._tokenResponse?.isNewUser;
      
      if (isNewUser) {
        console.log("New user via Google, storing basic profile");
        // Store basic user data for new Google sign-ins
        return storeUserData(result.user.uid, {
          name: result.user.displayName || '',
          email: result.user.email,
          photoURL: result.user.photoURL || '',
          role: 'patient', // Default role
          createdAt: new Date().toISOString()
        }).then(() => {
          return { success: true, user: result.user };
        });
      }
      
      return { success: true, user: result.user };
    })
    .catch(error => {
      console.error("Google login error:", error.message);
      return { success: false, error: error.message };
    });
}

// Logout function
function logoutUser() {
  ensureFirebaseAuth();
  console.log("Attempting to logout user");
  
  return firebase.auth().signOut()
    .then(() => {
      console.log("Logout successful");
      // Redirect to home page after successful logout
      window.location.href = '/?logout=success';
      return { success: true };
    })
    .catch(error => {
      console.error("Logout error:", error.message);
      // Even if Firebase logout fails, clear the session and redirect
      window.location.href = '/?logout=error';
      return { success: false, error: error.message };
    });
}

// Firestore Functions
function storeUserData(userId, userData) {
  console.log(`Storing user data for ${userId}`, userData);
  
  return firebase.firestore().collection("users").doc(userId).set({
    ...userData,
    updatedAt: new Date().toISOString()
  }, { merge: true })
    .then(() => {
      console.log("User data stored successfully");
      return { success: true };
    })
    .catch(error => {
      console.error("Error storing user data:", error.message);
      return { success: false, error: error.message };
    });
}

function getUserData(userId) {
  return firebase.firestore().collection("users").doc(userId).get()
    .then(doc => {
      if (doc.exists) {
        return { success: true, data: doc.data() };
      } else {
        return { success: false, error: "User data not found" };
      }
    })
    .catch(error => {
      console.error("Error getting user data:", error.message);
      return { success: false, error: error.message };
    });
}

// UI update functions
function updateUIForLoggedInUser(user) {
  console.log("Updating UI for logged in user", user.email);
  
  // Hide login/signup buttons
  const loginBtn = document.getElementById('loginBtn');
  const signupBtn = document.getElementById('signupBtn');
  if (loginBtn) loginBtn.style.display = 'none';
  if (signupBtn) signupBtn.style.display = 'none';
  
  // Update header to show user info
  const menuDiv = document.querySelector('.menu');
  if (menuDiv) {
    // Check if user account element already exists
    let userAccountDiv = document.querySelector('.user-account');
    if (!userAccountDiv) {
      userAccountDiv = document.createElement('div');
      userAccountDiv.className = 'user-account';
      
      const userAvatar = document.createElement('div');
      userAvatar.className = 'user-avatar';
      userAvatar.innerHTML = `<img src="${user.photoURL || 'https://via.placeholder.com/40'}" alt="User">`;
      
      const userName = document.createElement('span');
      userName.className = 'user-name';
      userName.textContent = user.displayName || user.email;
      
      const logoutBtn = document.createElement('button');
      logoutBtn.className = 'btn btn-logout';
      logoutBtn.innerHTML = '<i class="fas fa-sign-out-alt"></i>';
      logoutBtn.addEventListener('click', () => {
        logoutUser().then(() => {
          window.location.reload();
        });
      });
      
      userAccountDiv.appendChild(userAvatar);
      userAccountDiv.appendChild(userName);
      userAccountDiv.appendChild(logoutBtn);
      
      const ctaDiv = menuDiv.querySelector('.cta');
      if (ctaDiv) {
        ctaDiv.appendChild(userAccountDiv);
      }
    }
  }
  
  // Close the authentication modal if it's open
  const authModal = document.getElementById('authModal');
  if (authModal) authModal.style.display = 'none';
}

function updateUIForLoggedOutUser() {
  console.log("Updating UI for logged out user");
  
  // Show login/signup buttons
  const loginBtn = document.getElementById('loginBtn');
  const signupBtn = document.getElementById('signupBtn');
  if (loginBtn) loginBtn.style.display = 'block';
  if (signupBtn) signupBtn.style.display = 'block';
  
  // Remove user account element if it exists
  const userAccountDiv = document.querySelector('.user-account');
  if (userAccountDiv) userAccountDiv.remove();
}

// Check if user is logged in
function getCurrentUser() {
  ensureFirebaseAuth();
  const user = firebase.auth().currentUser;
  return user;
}

// Make auth functions available globally
window.loginWithEmailPassword = loginWithEmailPassword;
window.registerWithEmailPassword = registerWithEmailPassword;
window.loginWithGoogle = loginWithGoogle;
window.logoutUser = logoutUser;
window.getUserData = getUserData;
window.getCurrentUser = getCurrentUser;

// Initialize auth state listener when the script loads
document.addEventListener('DOMContentLoaded', function() {
  console.log("Setting up Firebase auth state listener");
  
  firebase.auth().onAuthStateChanged((user) => {
    if (user) {
      console.log('User logged in:', user.email);
      updateUIForLoggedInUser(user);
      
      // Check if we need to redirect to dashboard (only from home page)
      const currentPath = window.location.pathname;
      if (currentPath === '/' || currentPath === '/login' || currentPath === '/index.html') {
        console.log('User logged in on home/login page, redirecting to dashboard');
        redirectToDashboard();
      }
    } else {
      console.log('User logged out');
      updateUIForLoggedOutUser();
    }
  });
});

// Export functions to global window object for access from other scripts
window.loginWithEmailPassword = loginWithEmailPassword;
window.registerWithEmailPassword = registerWithEmailPassword;
window.loginWithGoogle = loginWithGoogle;
window.logoutUser = logoutUser;
window.storeUserData = storeUserData;
window.getUserData = getUserData;
window.updateUIForLoggedInUser = updateUIForLoggedInUser;
window.updateUIForLoggedOutUser = updateUIForLoggedOutUser;
