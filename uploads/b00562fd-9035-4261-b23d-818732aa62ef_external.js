console.log("External JS loaded: Validation functions ready");

function validateForm() {
  // Variables and data types
  let name = document.getElementById("name").value.trim(); // string
  let email = document.getElementById("email").value.trim();
  let pass = document.getElementById("password").value;
  let confirmPass = document.getElementById("confirmPass").value;
  let isValid = true; // boolean

  // Clear old errors
  clearErrors();

  // Control statements + operators for validation
  if (name === "") {
    showError("nameError", "Name is required");
    isValid = false;
  } else if (name.length < 3) {
    showError("nameError", "Name must be 3+ characters");
    isValid = false;
  }

  if (email === "") {
    showError("emailError", "Email is required");
    isValid = false;
  } else if (!email.includes("@") ||!email.includes(".")) {
    showError("emailError", "Enter valid email");
    isValid = false;
  }

  if (pass === "") {
    showError("passError", "Password is required");
    isValid = false;
  } else if (pass.length < 6) {
    showError("passError", "Password must be 6+ characters");
    isValid = false;
  }

  if (confirmPass!== pass) {
    showError("confirmError", "Passwords do not match");
    isValid = false;
  }

  return isValid;
}

// Helper function
function showError(id, message) {
  document.getElementById(id).textContent = message;
}

// Helper function
function clearErrors() {
  let errors = document.querySelectorAll(".error");
  for (let err of errors) {
    err.textContent = "";
  }
  document.getElementById("msg").textContent = "";
}