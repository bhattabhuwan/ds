/**
 * Helper functions to open the Firebase Console
 */

// Function to open Firebase Console with specific collection view
function openFirestoreConsole(collection = '') {
    const projectId = 'doc-sab'; // Hardcoded from firebase-config.js
    let firestoreConsoleUrl = `https://console.firebase.google.com/project/${projectId}/firestore/data`;
    
    if (collection) {
        firestoreConsoleUrl += `/${collection}`;
    }
    
    window.open(firestoreConsoleUrl, '_blank');
}

// Make function globally available
window.openFirestoreConsole = openFirestoreConsole;
