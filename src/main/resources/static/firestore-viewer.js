// Firestore Viewer Utility
import { 
  collection, 
  getDocs,
  query,
  where,
  limit,
  orderBy
} from "https://www.gstatic.com/firebasejs/10.7.0/firebase-firestore.js";

// Wait for Firestore to be initialized globally
document.addEventListener('DOMContentLoaded', function() {
  console.log("Firestore Viewer initialized");

  // Helper function to open Firestore Console for specific collections
  window.openFirestoreConsole = function(collectionName = null) {
    try {
      // Get project ID from firebaseConfig
      const projectId = 'doc-sab'; // Hardcoded from firebase-config.js
      
      let firestoreConsoleUrl = `https://console.firebase.google.com/project/${projectId}/firestore/data`;
      
      // If collection specified, direct to that collection
      if (collectionName) {
        firestoreConsoleUrl += `/${collectionName}`;
      }
      
      window.open(firestoreConsoleUrl, '_blank');
      console.log("Opening Firestore console:", firestoreConsoleUrl);
      
      return true;
    } catch (error) {
      console.error("Error opening Firestore console:", error);
      return false;
    }
  };
  
  // Add Firestore viewer button to auth forms
  const addFirestoreButtons = () => {
    // Add to login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm && !loginForm.querySelector('.firestore-btn')) {
      const loginSocialSection = loginForm.querySelector('.social-login');
      if (loginSocialSection) {
        const openFirestoreBtn = document.createElement('button');
        openFirestoreBtn.className = 'btn btn-secondary firestore-btn';
        openFirestoreBtn.type = 'button'; // Prevent form submission
        openFirestoreBtn.innerHTML = '<i class="fas fa-database"></i> Open Firebase Console';
        openFirestoreBtn.onclick = () => window.openFirestoreConsole();
        
        loginSocialSection.appendChild(openFirestoreBtn);
      }
    }
    
    // Add to signup form
    const signupForm = document.getElementById('signupForm');
    if (signupForm && !signupForm.querySelector('.firestore-btn')) {
      const signupSocialSection = signupForm.querySelector('.social-login');
      if (signupSocialSection) {
        const openFirestoreBtn = document.createElement('button');
        openFirestoreBtn.className = 'btn btn-secondary firestore-btn';
        openFirestoreBtn.type = 'button'; // Prevent form submission
        openFirestoreBtn.innerHTML = '<i class="fas fa-database"></i> Open Firebase Console';
        openFirestoreBtn.onclick = () => window.openFirestoreConsole();
        
        signupSocialSection.appendChild(openFirestoreBtn);
      }
    }
  };
  
  // Call once when loaded
  setTimeout(addFirestoreButtons, 1000);
  
  // Also add when tabs change
  const authTabs = document.querySelectorAll('.auth-tab');
  authTabs.forEach(tab => {
    tab.addEventListener('click', function() {
      setTimeout(addFirestoreButtons, 100);
    });
  });
});

// Export a function to view users collection
export async function viewUsersCollection(db, filterRole = null) {
  try {
    let usersQuery;
    
    if (filterRole) {
      usersQuery = query(
        collection(db, "users"), 
        where("role", "==", filterRole),
        orderBy("createdAt", "desc"),
        limit(10)
      );
    } else {
      usersQuery = query(
        collection(db, "users"),
        orderBy("createdAt", "desc"),
        limit(10)
      );
    }
    
    const querySnapshot = await getDocs(usersQuery);
    
    const users = [];
    querySnapshot.forEach((doc) => {
      users.push({
        id: doc.id,
        ...doc.data()
      });
    });
    
    console.log(`Found ${users.length} users${filterRole ? ` with role '${filterRole}'` : ''}:`, users);
    return users;
  } catch (error) {
    console.error("Error fetching users:", error);
    return [];
  }
}