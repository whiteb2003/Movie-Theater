function openModal(url) {
    var modal = document.getElementById("myModal");
    var img = document.getElementById("img01");
    img.src = "/qr/" + url;
    modal.style.display = "block";
}

function closeModal() {
    var modal = document.getElementById("myModal");
    modal.style.display = "none";
}