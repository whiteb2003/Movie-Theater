let date = new Date();
const spawnDate = document.querySelector('.spawn-date');
let toggle = document.querySelector('.toggle');
let turn = true;

function spawnAWeek() {
    for (let i = 1; i <= 14; i++) {
        let id = `${date.getDate()}/${date.getMonth() + 1}`;
        let exist = document.getElementById(id);
        if (exist === null) {
            let label = document.createElement("label");
            let month = (date.getMonth() + 1).toString().length === 1 ? `0${date.getMonth() + 1}` : (date.getMonth() + 1);
            let day = (date.getDate()).toString().length === 1 ? `0${date.getDate()}` : (date.getDate());
            let link = `/bookingTicket/${date.getFullYear()}-${month}-${day}`;
            label.innerHTML = `<a class="single-date" href="${link}" id="${date.getFullYear()}-${month}-${day}">${date.getDate()}/${date.getMonth() + 1}</a>`;
            spawnDate.append(label);
        }
        date.setDate(date.getDate() + 1);
    }
}

let url = window.location.pathname;
let date_id = url;
var start = url.lastIndexOf("/") + 1;
var len = url.indexOf("?");
if (len > 0)
    url = url.substring(start, len);
else
    url = url.substring(start);

spawnAWeek();

let dates = document.querySelectorAll('.single-date');


let path = date_id.split("/");


if (!isNaN(url)) {
    for (let index = 0; index < dates.length; index++) {
        dates[index].href = dates[index].href.concat("/", url);
    }
    let film = document.getElementById(`film-${url}`);
    film.classList.add('chose');
}

for (let i = 0; i < dates.length; i++) {

    if(i >= 7 && turn){

        turn = false;
        spawnDate.classList.toggle("open");
        toggle.classList.toggle("rotate");
    }

    if (dates[i].id=== path[2]) {
        dates[i].classList.add('chose');
        break;
    }

}


toggle.onclick = () => {
    spawnDate.classList.toggle("open");
    toggle.classList.toggle("rotate");
}
