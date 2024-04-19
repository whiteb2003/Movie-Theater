function closePage() {
    window.close(); // Close the current web page
}
function changePassword() {
    const passwordRegex = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$/;
    const currentPassword = document.getElementById('currentPassword').value;
    let newPassword = document.getElementById('newPassword').value;
    let confirmPassword = document.getElementById('confirmPassword').value;
    const successDiv = document.getElementById('success-message');
    let message = document.getElementById("message");
    let shouldSubmitForm = true;
    // Check if current password and new password fields are not empty

    if (currentPassword.trim() === '' || newPassword.trim() === '' || confirmPassword.trim() === '') {
        alert('Please fill in all fields.');
        shouldSubmitForm = false;
    }
    // Check if new password and confirm password match
    else if (newPassword !== confirmPassword) {
        alert('New password and confirm password do not match.');
        return false;
    }
    // Check if new password and current password match
    if (currentPassword === newPassword) {
        alert('New password and current password must not match');
        return false;
    }
    // if (shouldSubmitForm) {
    //     document.querySelector('.edit-profile').submit();
    //     alert('Change password successful');
    // } else {
    //     // Clear the password fields if validation failed
    //     // document.getElementById('currentPassword').value = '';
    //     // document.getElementById('newPassword').value = '';
    //     // document.getElementById('confirmPassword').value = '';
    //     alert('Change password fail');
    // }
}
function togglePasswordField(fieldId) {
    const passwordField = document.getElementById(fieldId);
    const toggleIcon = document.getElementById(fieldId + 'Toggle');

    if (passwordField.type === "password") {
        passwordField.type = "text";
        toggleIcon.classList.remove("eye-slash");
        toggleIcon.classList.add("eye");
    } else {
        passwordField.type = "password";
        toggleIcon.classList.remove("eye");
        toggleIcon.classList.add("eye-slash");
    }
}
function togglePasswordVisibility(fieldId, toggleId) {
    const passwordField = document.getElementById(fieldId);
    const toggleIcon = document.getElementById(toggleId);

    if (passwordField.value.trim() !== '') {
        toggleIcon.style.display = 'block';
    } else {
        toggleIcon.style.display = 'none';
    }
}