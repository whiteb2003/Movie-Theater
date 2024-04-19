let loadFile = function (event, id) {
    let output = document.getElementById(id);
    output.style.display = "block";
    output.src = URL.createObjectURL(event.target.files[0]);
    output.onload = function () {
        URL.revokeObjectURL(output.src); // free memory
    }
};