let seatcol = document.querySelector('.seat-row').querySelectorAll('.seat-column');
let allSeatCol = document.querySelectorAll('.seat-row');
let form = document.querySelectorAll('.seat-form');
let seatSpacingNumber = Math.floor((seatcol.length) / 2);

let userDetail = document.querySelector('.user-details');

function seatSpacing() {
    for (let i = 0; i < allSeatCol.length; i++) {
        let col = allSeatCol[i].querySelectorAll('.seat-column');
        col[seatSpacingNumber - 1].classList.add("space");
        for (let j = 0; j < col.length; j++) {
            if (!col[j].classList.contains("empty-booked")) {
                col[j].addEventListener("click", () => {
                    col[j].classList.toggle("selected");
                    if (col[j].classList.contains("selected")) {
                        showName(col[j], true, col[j].id);
                        addInput(col[j].id);
                    } else {
                        showName(col[j], false, col[j].id);
                        removeInput(col[j].id);
                    }

                });
            }
        }
    }
}

function addInput(id) {
    let input = document.createElement("input");
    input.type = 'checkbox';
    input.name = 'seat';
    input.checked = true;
    input.value = id;
    input.className = `invisible input-${id}`;
    let account = document.createElement("p");
    form[0].append(input);
    if (form.length > 1) {
        let inputClone = input.cloneNode(true);
        form[1].append(inputClone);
    }
}

function removeInput(id) {
    let input = document.querySelectorAll(`.input-${id}`);
    for (let i = 0; i < input.length; i++) {
        input[i].remove();
    }
}



seatSpacing();

function showName(element, bool , id){
    if(bool){
        let div = document.createElement("div");
        div.id = `user-${id}`;
        console.log(element.value);
        div.innerHTML = `<span>${element.textContent} - Info: ${element.dataset.user}</span>`;
        userDetail.prepend(div);
    }else{
        let removeElement = document.getElementById(`user-${id}`);
        removeElement.remove();
    }
}