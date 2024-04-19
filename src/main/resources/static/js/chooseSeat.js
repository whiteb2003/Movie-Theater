let seatcol = document.querySelector('.seat-row').querySelectorAll('.seat-column');
let allSeatCol = document.querySelectorAll('.seat-row');
let form = document.querySelectorAll('.seat-form');
let seatSpacingNumber = Math.floor(seatcol.length / 2);

function seatSpacing() {
    for (let i = 0; i < allSeatCol.length; i++) {
        let col = allSeatCol[i].querySelectorAll('.seat-column');
        for (let j = 0; j < col.length; j++) {
            col[j].addEventListener("click", () => {
                if (!col[j].classList.contains("booked")) {
                    col[j].classList.toggle("selected");
                    if(col[j].classList.contains("selected")){
                        addInput(col[j].id);
                    }else{
                        removeInput(col[j].id);
                    }
                }
            });
        }
        col[seatSpacingNumber - 1].classList.add("space");
    }
}

function addInput(id){
    let input = document.createElement("input");
    input.type='checkbox';
    input.name = 'seat';
    input.checked = true;
    input.value =id;
    input.className = `invisible input-${id}`;
    form[0].append(input);
    if(form.length >1){
        let inputClone = input.cloneNode(true);
        form[1].append(inputClone);
    }
}

function removeInput(id){
    let input = document.querySelectorAll(`.input-${id}`);
    for (let i = 0; i < input.length; i++) {
        input[i].remove();
    }
}



seatSpacing();