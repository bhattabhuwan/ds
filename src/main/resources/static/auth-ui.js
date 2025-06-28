// Auth UI Handler for DoctorSab
// This file handles the UI interactions for the authentication forms

// Main function to setup authentication UI components (declared as a global window function)
window.setupAuthUI = function() {
  console.log("Setting up Auth UI components");
  
  // Check if Firebase is available
  if (!window.firebase) {
    console.error("Firebase is not available. Auth UI won't work properly.");
    document.getElementById('loginMessage').textContent = "Firebase authentication isn't available. Check your internet connection.";
    document.getElementById('loginMessage').className = 'auth-message error';
    return;
  }

  // Setup tab switching
  const authTabs = document.querySelectorAll('.auth-tab');
  authTabs.forEach(tab => {
    tab.addEventListener('click', function() {
      // Toggle active class on tabs
      authTabs.forEach(t => t.classList.remove('active'));
      this.classList.add('active');
      
      // Toggle active class on forms
      const formType = this.getAttribute('data-form');
      document.querySelectorAll('.auth-form').forEach(form => {
        form.classList.remove('active');
      });
      document.getElementById(`${formType}Form`).classList.add('active');
    });
  });
  
  // Password strength indicator
  const signupPassword = document.getElementById('signupPassword');
  const passwordStrength = document.getElementById('passwordStrength');
  
  if (signupPassword && passwordStrength) {
    signupPassword.addEventListener('input', function() {
      const password = this.value;
      let strength = 0;
      
      // Length check
      if (password.length >= 6) {
        strength += 1;
      }
      
      // Complexity checks
      if (password.match(/[A-Z]/)) {
        strength += 1;
      }
      
      if (password.match(/[0-9]/)) {
        strength += 1;
      }
      
      if (password.match(/[^A-Za-z0-9]/)) {
        strength += 1;
      }
      
      // Update UI
      passwordStrength.className = 'password-strength';
      if (password.length === 0) {
        passwordStrength.className = 'password-strength';
      } else if (strength < 2) {
        passwordStrength.className = 'password-strength weak';
      } else if (strength < 4) {
        passwordStrength.className = 'password-strength medium';
      } else {
        passwordStrength.className = 'password-strength strong';
      }
    });
  }
    // Handle login form submission
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', async function(e) {
      e.preventDefault();
      
      const email = document.getElementById('loginEmail').value;
      const password = document.getElementById('loginPassword').value;
      const messageDiv = document.getElementById('loginMessage');
      const submitBtn = this.querySelector('button[type="submit"]');
      const originalBtnText = submitBtn.innerHTML;
      
      // Show loading state
      messageDiv.textContent = 'Logging in...';
      messageDiv.className = 'auth-message info';
      submitBtn.disabled = true;
      submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Logging in...';
        try {
        const result = await window.loginWithEmailPassword(email, password);
        if (result.success) {
          messageDiv.textContent = 'Login successful!';
          messageDiv.className = 'auth-message success';
          submitBtn.innerHTML = '<i class="fas fa-check"></i> Success!';
            setTimeout(() => {
            // Close the modal on successful login
            document.getElementById('authModal').style.display = 'none';
            // Redirect to dashboard instead of reloading
            window.location.href = '/dashboard/user';
          }, 1000);
        } else {
          messageDiv.textContent = result.error || 'Login failed. Please try again.';
          messageDiv.className = 'auth-message error';
          submitBtn.disabled = false;
          submitBtn.innerHTML = originalBtnText;
        }
      } catch (error) {
        messageDiv.textContent = error.message || 'An unexpected error occurred.';
        messageDiv.className = 'auth-message error';
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalBtnText;
      }
    });
  }
  
  // Handle signup form submission
  const signupForm = document.getElementById('signupForm');
  if (signupForm) {
    signupForm.addEventListener('submit', async function(e) {
      e.preventDefault();
      
      const name = document.getElementById('signupName').value;
      const email = document.getElementById('signupEmail').value;
      const password = document.getElementById('signupPassword').value;
      const confirmPassword = document.getElementById('signupConfirmPassword').value;
      const role = document.querySelector('input[name="userRole"]:checked').value;
      const messageDiv = document.getElementById('signupMessage');
      const submitBtn = this.querySelector('button[type="submit"]');
      const originalBtnText = submitBtn.innerHTML;
      const termsAgreement = document.getElementById('termsAgreement');
      
      // Validate inputs
      if (!name.trim()) {
        messageDiv.textContent = 'Please enter your name!';
        messageDiv.className = 'auth-message error';
        return;
      }
      
      if (!email.trim()) {
        messageDiv.textContent = 'Please enter your email!';
        messageDiv.className = 'auth-message error';
        return;
      }
      
      if (password !== confirmPassword) {
        messageDiv.textContent = 'Passwords do not match!';
        messageDiv.className = 'auth-message error';
        return;
      }
      
      if (password.length < 6) {
        messageDiv.textContent = 'Password must be at least 6 characters long!';
        messageDiv.className = 'auth-message error';
        return;
      }
      
      if (!termsAgreement.checked) {
        messageDiv.textContent = 'Please agree to the Terms of Service and Privacy Policy';
        messageDiv.className = 'auth-message error';
        return;
      }
      
      if (role === 'doctor') {
        const specialization = document.getElementById('specialization').value;
        const licenseNumber = document.getElementById('licenseNumber').value;
        
        if (!specialization.trim()) {
          messageDiv.textContent = 'Please enter your medical specialization!';
          messageDiv.className = 'auth-message error';
          return;
        }
        
        if (!licenseNumber.trim()) {
          messageDiv.textContent = 'Please enter your medical license number!';
          messageDiv.className = 'auth-message error';
          return;
        }
      }
      
      // Show loading state
      messageDiv.textContent = 'Creating your account...';
      messageDiv.className = 'auth-message info';
      submitBtn.disabled = true;
      submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Creating account...';
      
      try {
        // Prepare user data
        const userData = {
          name,
          email,
          role,
          createdAt: new Date().toISOString()
        };
        
        // Add doctor specific fields if role is doctor
        if (role === 'doctor') {
          userData.specialization = document.getElementById('specialization').value;
          userData.licenseNumber = document.getElementById('licenseNumber').value;
          userData.approvalStatus = 'pending'; // Doctors need approval
        }
          const result = await window.registerWithEmailPassword(email, password, userData);
        if (result.success) {
          messageDiv.textContent = 'Account created successfully!';
          messageDiv.className = 'auth-message success';
          submitBtn.innerHTML = '<i class="fas fa-check"></i> Success!';
          
          setTimeout(() => {
            // Close the modal on successful signup
            document.getElementById('authModal').style.display = 'none';
            // Redirect to dashboard instead of reloading
            window.location.href = '/dashboard/user';
          }, 1500);
        } else {
          messageDiv.textContent = result.error || 'Signup failed. Please try again.';
          messageDiv.className = 'auth-message error';
          submitBtn.disabled = false;
          submitBtn.innerHTML = originalBtnText;
        }
      } catch (error) {
        messageDiv.textContent = error.message || 'An unexpected error occurred.';
        messageDiv.className = 'auth-message error';
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalBtnText;
      }
    });
    
    // Toggle doctor fields visibility based on role selection
    const roleDoctor = document.getElementById('roleDoctor');
    const rolePatient = document.getElementById('rolePatient');
    const doctorFields = document.querySelector('.doctor-fields');
    
    if (roleDoctor && doctorFields) {
      roleDoctor.addEventListener('change', function() {
        doctorFields.style.display = this.checked ? 'block' : 'none';
        if (this.checked) {
          doctorFields.classList.add('show');
        } else {
          doctorFields.classList.remove('show');
        }
      });
    }
    
    if (rolePatient && doctorFields) {
      rolePatient.addEventListener('change', function() {
        doctorFields.style.display = this.checked ? 'none' : 'block';
        if (!this.checked) {
          doctorFields.classList.add('show');
        } else {
          doctorFields.classList.remove('show');
        }
      });
    }
  }
    // Handle Google login
  const googleLoginBtn = document.getElementById('googleLogin');
  if (googleLoginBtn) {
    googleLoginBtn.addEventListener('click', async function() {
      const messageDiv = document.getElementById('loginMessage');
      const btnText = this.innerHTML;
      
      messageDiv.textContent = 'Logging in with Google...';
      messageDiv.className = 'auth-message info';
      this.disabled = true;
      this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Connecting...';
      
      try {
        const result = await window.loginWithGoogle();
        if (result.success) {
          messageDiv.textContent = 'Login successful!';
          messageDiv.className = 'auth-message success';
          this.innerHTML = '<i class="fas fa-check"></i> Success!';
          
          setTimeout(() => {
            document.getElementById('authModal').style.display = 'none';
            // Redirect to dashboard instead of reloading
            window.location.href = '/dashboard/user';
          }, 1000);
        } else {
          messageDiv.textContent = result.error || 'Google login failed. Please try again.';
          messageDiv.className = 'auth-message error';
          this.disabled = false;
          this.innerHTML = btnText;
        }
      } catch (error) {
        messageDiv.textContent = error.message || 'An unexpected error occurred.';
        messageDiv.className = 'auth-message error';
        this.disabled = false;
        this.innerHTML = btnText;
      }
    });
  }
  
  // Handle Google signup
  const googleSignupBtn = document.getElementById('googleSignup');
  if (googleSignupBtn) {
    googleSignupBtn.addEventListener('click', async function() {
      const messageDiv = document.getElementById('signupMessage');
      const btnText = this.innerHTML;
      const role = document.querySelector('input[name="userRole"]:checked').value;
      
      // Validate doctor fields if doctor role selected
      if (role === 'doctor') {
        const specialization = document.getElementById('specialization').value;
        const licenseNumber = document.getElementById('licenseNumber').value;
        
        if (!specialization.trim()) {
          messageDiv.textContent = 'Please enter your medical specialization!';
          messageDiv.className = 'auth-message error';
          return;
        }
        
        if (!licenseNumber.trim()) {
          messageDiv.textContent = 'Please enter your medical license number!';
          messageDiv.className = 'auth-message error';
          return;
        }
      }
      
      // Check terms agreement
      if (!document.getElementById('termsAgreement').checked) {
        messageDiv.textContent = 'Please agree to the Terms of Service and Privacy Policy';
        messageDiv.className = 'auth-message error';
        return;
      }
      
      messageDiv.textContent = 'Signing up with Google...';
      messageDiv.className = 'auth-message info';
      this.disabled = true;
      this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Connecting...';
        try {
        const result = await window.loginWithGoogle();
        if (result.success) {
          // Check if the user role is selected and update Firestore accordingly
          
          // Prepare additional user data
          let additionalData = {
            role: role,
            updatedAt: new Date().toISOString(),
            name: result.user.displayName || '',
            email: result.user.email,
            photoURL: result.user.photoURL || ''
          };
          
          if (role === 'doctor') {
            const specialization = document.getElementById('specialization').value;
            const licenseNumber = document.getElementById('licenseNumber').value;
            
            // Add doctor specific fields
            additionalData.specialization = specialization;
            additionalData.licenseNumber = licenseNumber;
            additionalData.approvalStatus = 'pending'; // Doctors need approval
          }
          
          // Update user data in Firestore
          await window.storeUserData(result.user.uid, additionalData);
          
          messageDiv.textContent = 'Signup successful!';
          messageDiv.className = 'auth-message success';
          this.innerHTML = '<i class="fas fa-check"></i> Success!';
            setTimeout(() => {
            document.getElementById('authModal').style.display = 'none';
            window.location.href = '/dashboard/user';
          }, 1000);
        } else {
          messageDiv.textContent = result.error || 'Google signup failed. Please try again.';
          messageDiv.className = 'auth-message error';
          this.disabled = false;
          this.innerHTML = btnText;
        }      } catch (error) {
        messageDiv.textContent = error.message || 'An unexpected error occurred.';
        messageDiv.className = 'auth-message error';
        this.disabled = false;
        this.innerHTML = btnText;
      }
    });
  }
}
