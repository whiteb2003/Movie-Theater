function openTrailerModal(element) {
    url = element.getAttribute('trailer');
    movieId = element.dataset.id;
    var modal = document.getElementById(`trailerModal`);
    var iframe = modal.querySelector('iframe');
    url = changeURL(url);
    iframe.src = url;
    modal.style.display = 'block';
}

function closeTrailerModal() {
    var modal = document.querySelectorAll('.modal');
    for (let i = 0; i < modal.length ; i++) {
        modal[i].querySelector('iframe').src = '';
        modal[i].style.display = 'none';
    }
}

function changeURL(trailer) {
    var video_id = trailer.match(/(youtu\.be\/|youtube\.com\/(watch\?(.*&)?v=|(embed|v)\/))([^?&"'>]+)/);
    return video_id ? `https://www.youtube.com/embed/${video_id[5]}` : null;
}
// function changeURL(trailer) {
//         trailer = trailer.split("?v=")[1];
//         return `https://www.youtube.com/embed/${trailer}`;
// }
