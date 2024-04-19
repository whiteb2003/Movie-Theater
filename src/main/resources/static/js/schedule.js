let date = new Date();
const spawnDate = document.querySelector('.spawn-date');
let movieD = document.querySelector('.release').textContent;
let movie = movieD.split("/");
for (let i = 0; i < movie.length; i++) {
    console.log(movie[i]);
}
let realMovieDate = `${movie[1]}/${movie[0]}/${movie[2]}`;
movieDate = new Date(realMovieDate);

console.log(movieDate);

function spawnAWeek() {
    for (let i = 1; i <= 14; i++) {
        date.setDate(date.getDate() + 1);
        if (date >= movieDate) {
            let id = `${date.getDate()}/${date.getMonth() + 1}`;
            let exist = document.getElementById(id);
            if (exist === null) {
                let label = document.createElement("label");
                label.innerHTML = `<input type="checkbox" id="${id}" value="${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}" name="movieDate"/>
                <span>${date.getDate()}/${date.getMonth() + 1}</span>`;
                spawnDate.append(label);
            }
        }
    }
    if(spawnDate.getElementsByTagName("label").length ===0){
        document.getElementById("dateError").innerHTML=`Too soon to set schedule`;
    }
}

spawnAWeek();

function checkValid() {
    let scheduleElements = document.querySelectorAll("input[name='schedule']:checked");
    let scheduleValues = Array.from(scheduleElements).map(input => input.value);
    let schedule_error = document.getElementById("schedule_error");

    let movieDateElements = document.querySelectorAll("input[name='movieDate']:checked");
    let movieDateValues = Array.from(movieDateElements).map(input => input.value);
    let dateError = document.getElementById("dateError");

    let roomElements = document.querySelectorAll("input[name='room_name']:checked");
    let roomValues = Array.from(roomElements).map(input => input.value);
    let roomError = document.getElementById("room_error");

    console.log("room : ", roomElements);
    schedule_error.textContent = scheduleValues.length === 0 ? "Schedule is required" : "";
    dateError.textContent = movieDateValues.length === 0 ? "Movie date is required" : "";
    roomError.textContent = roomValues.length === 0 ? "Room is required" : "";

    let errorElements = [schedule_error, dateError, roomError];
    errorElements.forEach(element => {
        element.style.color = "red";
    });
    if (
        scheduleValues.length > 0 && movieDateValues.length > 0 && roomValues.length > 0
    ) {
        // Access the form and submit it
        document.getElementById("saveSchedule").submit();
    }
}

let schedules = document.querySelector('.chosen-schedule').getElementsByTagName("input");
let durationText = document.querySelector('.dura').textContent;
let span = document.querySelector('.chosen-schedule').getElementsByTagName("span");
var duration = durationText.replace(/^\D+/g, '');


let boolCheck = Array.apply(0, Array(schedules.length)).map(function () {
});
for (let i = 0; i < boolCheck.length; i++) {
    boolCheck[i] = 0;
}

initSchedule();
validateSchedule();

function initSchedule() {
    for (let i = 0; i < schedules.length; i++) {
        if (schedules[i].checked === true) {
            for (let j = 0; j < schedules.length; j++) {
                if (i !== j) {
                    if (
                        ((schedules[j].value <= parseInt(schedules[i].value) + parseInt(duration) + 10) && (j > i))
                        || ((parseInt(schedules[i].value) - parseInt(schedules[j].value) - parseInt(duration) - 10) <= 0 && j < i)
                    ) {
                        schedules[j].disabled = true;
                        span[j].classList.add("gray");
                        boolCheck[j]++;
                    }
                }
            }

        }
    }
}

function validateSchedule() {

    for (let i = 0; i < schedules.length; i++) {
        schedules[i].addEventListener("click", () => {
            if (schedules[i].checked === true) {
                for (let j = 0; j < schedules.length; j++) {
                    if (i !== j) {
                        if (
                            ((schedules[j].value <= parseInt(schedules[i].value) + parseInt(duration) + 10) && (j > i))
                            || ((parseInt(schedules[i].value) - parseInt(schedules[j].value) - parseInt(duration) - 10) <= 0 && j < i)
                        ) {
                            schedules[j].disabled = true;
                            schedules[j].checked = false;
                            span[j].classList.add("gray");
                            boolCheck[j]++;
                        }
                    }
                }

            } else {
                for (let j = 0; j < schedules.length; j++) {
                    if (i !== j) {
                        if (
                            ((schedules[j].value <= parseInt(schedules[i].value) + parseInt(duration) + 10) && (j > i))
                            || ((parseInt(schedules[i].value) - parseInt(schedules[j].value) - parseInt(duration) - 10) <= 0 && j < i)
                        ) {
                            boolCheck[j]--;
                            if (boolCheck[j] === 0) {
                                schedules[j].disabled = false;
                                span[j].classList.remove("gray");
                            }
                        }
                    }
                }
            }
        });
    }
}