function checkPassword() {
    const passwordRegex = /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$/;
    let password = document.getElementById("password").value;
    let cnfrmPassword = document.getElementById("cnfrm-password").value;
    console.log("Password:", password, '\n', "Confirm Password:", cnfrmPassword);
    let message = document.getElementById("message");

    if (!password.match(passwordRegex)) {
        message.textContent = "Password has at least 8 characters, at least 1 number, 1 character, 1 special charater";
        message.style.backgroundColor = "#ff4d4d";

    } else {
        if (password === cnfrmPassword) {
            document.getElementById("resetForm").submit();
        } else {
            message.textContent = "Passwords don't match";
            message.style.backgroundColor = "#ff4d4d";
        }
    }
}