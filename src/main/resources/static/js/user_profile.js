$(document).ready(function () {
    $("#togglePassword").click(function () {
        var passwordField = $("#passwordField");
        var fieldType = passwordField.attr("type");
        if (fieldType === "password") {
            passwordField.attr("type", "text");
        } else {
            passwordField.attr("type", "password");
        }
    });
});
function closePage() {
    window.close(); // Đóng trang web hiện tại
}
function showSuccessMessage() {
    var successDiv = document.getElementById('success-message');
    successDiv.style.display = 'block';
    setTimeout(function () {
        successDiv.style.display = 'none';
    }, 3000); // Sau 3 giây, tự động ẩn cửa sổ thông báo
}
function togglePasswordField() {
    var passwordField = document.getElementById("passwordField");
    var eyeIcon = document.getElementById("eyeIcon");

    if (passwordField.type === "password") {
        passwordField.type = "text";
        eyeIcon.classList.remove("fa-eye-slash");
        eyeIcon.classList.add("fa-eye");
    } else {
        passwordField.type = "password";
        eyeIcon.classList.remove("fa-eye");
        eyeIcon.classList.add("fa-eye-slash");
    }
}
$(document).ready(function () {
    $("#togglePassword").click(function () {
        var passwordField = $("#passwordField");
        var fieldType = passwordField.attr("type");
        if (fieldType === "password") {
            passwordField.attr("type", "text");
        } else {
            passwordField.attr("type", "password");
        }
    });
});

var posterPath = document.getElementById("user").value;
if (posterPath) {
    let output = document.getElementById('output');
    output.style.display = "block";
    output.src = posterPath;
}
