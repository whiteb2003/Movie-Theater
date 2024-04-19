function updateProgress(progressPercentage) {
    const progressBarFill = document.getElementById('progress');
    progressBarFill.style.width = `${progressPercentage}%`;
}

function checkConvert() {
    const progress = document.querySelector('.progress');

    const progressBar = document.createElement("div");
    progressBar.className = "progress-bar";
    progressBar.innerHTML = '<div class="progress-bar-fill" id="progress"></div>';


    document.getElementById("confirm").submit();
    progress.appendChild(progressBar);

    function run() {
        let current = 0;
        const interval = setInterval(() => {
            current += 2;
            updateProgress(current);
            if (current === 100) {
                clearInterval(interval);
                setTimeout(() => {
                    progressBar.style.display = 'none';
                }, 490);
            }
        }, 100);
    }
    run();
}