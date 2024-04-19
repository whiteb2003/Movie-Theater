let pathName = window.location.pathname;
let navi = document.querySelector('.movie-navigate');
let arr = navi.getElementsByTagName("a");

console.log(pathName);


if (pathName === "/movies/manage/hidden") {
    arr[1].className = "current";
    let movies = document.querySelectorAll('.movie');
    for (let i = 0; i < movies.length; i++) {
        movies[i].querySelector('.add-schedule').remove();
        let status = movies[i].querySelector('.status').innerHTML = 'Active';
        let href = movies[i].querySelector('.status').getAttribute("href");
        movies[i].querySelector('.status').href = href.replace("hidden", "active");
    }

} else {
    arr[0].className = "current";
}

// if (document.querySelectorAll('.movie').length <= 2) {
//     document.querySelector('.paging-wrapper').style.display = 'none';
// }
// if (document.querySelectorAll('.movie').length === 0) {
//     document.querySelector('.movie-manage').innerHTML = `<p style="color: var(--red); text-align: center">No movie currently </p>`
//     document.querySelector('.paging-wrapper').style.display = 'none';
// }
