const wrapper = document.querySelector('.paging-wrapper');
const pageList = wrapper.querySelector('.page-list');

let isFirstPage = true;
function createItem( pageNum, showPage){
    for (let i = 1; i <= pageNum; i++) {
        let li = document.createElement("li");
        li.className="page-list-item";
        li.id=`${i}`;
        li.innerHTML=`<a th:href="@{/home/${i}">${i}</a>`
        if(i> showPage){
            li.style.display = 'none';
        }
        pageList.append(li);
    }

}

createItem(7,4);