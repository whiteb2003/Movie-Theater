let nav = document.querySelector('.navbar-wrapper');

function scrollDetect(){
    var lastScroll = 0;

    window.onscroll = function() {
        let currentScroll = document.documentElement.scrollTop || document.body.scrollTop; // Get Current Scroll Value

        if (currentScroll > 0 && lastScroll <= currentScroll){
            lastScroll = currentScroll;
            nav.classList.add("sticky");
        }else{
            lastScroll = currentScroll;
            nav.classList.remove("sticky");
        }
    };
}


scrollDetect();