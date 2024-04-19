let movieListContainers = document.querySelectorAll('.movie-list-container');
for (let i = 0; i <  movieListContainers.length; i++) {
    let numMovies = movieListContainers[i].querySelectorAll('.movie-list-item').length;
    if(numMovies ===0){
        movieListContainers[i].querySelector('.paging-wrapper').style.display = 'none';
        movieListContainers[i].querySelector('.movie-list-wrapper').innerHTML='<p style="color: var(--red); font-size: 22px">No movie at the moment!</p>';
    }

    // if(numMovies <=4){
    //     movieListContainers[i].querySelector('.paging-wrapper').style.display = 'none';
    // }
}
let slideiIndex = 0;
let banner = document.querySelectorAll('.banner-holder');
let prev = document.querySelector('.prev');
let next = document.querySelector('.next');
let interval;
function slideshow() {
    for (let i = 0; i < banner.length; i++) {
        banner[i].style.display = 'none';
    }
    slideiIndex++;
    if(slideiIndex > banner.length){
        slideiIndex = 1;}
    if(slideiIndex < 1){
        slideiIndex = banner.length;}
    banner[slideiIndex-1].style.display = 'block';
    interval = setTimeout(slideshow, 5000);
}

slideshow();

function changeImage(num){
    slideiIndex+= num;
    clearTimeout(interval);
    slideshow();
}

prev.onclick = () => {
    changeImage(-2);
}

next.onclick = () => {
    changeImage(0);
}

let info = document.querySelectorAll('.info-short');
let des = document.querySelectorAll('.short-des');

for (let i = 0; i < info.length ; i++) {
    des[i].innerHTML= info[i].value;
}