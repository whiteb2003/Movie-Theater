function checkMovie() {
    let title_en = document.getElementById("title_en").value;
    let title_vn = document.getElementById("title_vn").value;
    let description = document.getElementById("des").value;
    let short_description = document.getElementById("short_des").value;
    let duration = document.getElementById("duration").value;
    let release_date = document.getElementById("release_date").value;
    let actor = document.getElementById("actor").value;
    let company = document.getElementById("company").value;
    let director = document.getElementById("director").value;
    let genreElements = document.querySelectorAll("input[name='genre']:checked");
    let genreValues = Array.from(genreElements).map(input => input.value);
    let statusElements = document.querySelectorAll('input[name="status"]');
    let status = Array.from(statusElements).find(input => input.checked)?.value;
    let imagePoster = document.getElementById("imagePoster");
    let imageBanner = document.getElementById("imageBanner");
    let posterPath = document.getElementById("poster").value;
    let bannerPath = document.getElementById("banner").value;
    console.log(imagePoster.files.length);
    console.log(posterPath.length);

    let title_en_error = document.getElementById("title_en_error");
    let title_vn_error = document.getElementById("title_vn_error");
    let description_error = document.getElementById("description_error");
    let short_description_error = document.getElementById("short_description_error");
    let duration_error = document.getElementById("duration_error");
    let release_date_error = document.getElementById("release_date_error");
    let actor_error = document.getElementById("actor_error");
    let company_error = document.getElementById("company_error");
    let director_error = document.getElementById("director_error");
    let genre_error = document.getElementById("genre_error");
    let status_error = document.getElementById("status_error");
    let poster_error = document.getElementById("poster_error");
    let banner_error = document.getElementById("banner_error");

    title_en_error.textContent = !title_en ? "Title English is required" : "";
    title_vn_error.textContent = !title_vn ? "Title Vietnamese is required" : "";
    description_error.textContent = !description ? "Description is required" : "";
    short_description_error.textContent = !short_description ? "Short Description is required" : "";
    duration_error.textContent = !duration ? "Duration is required" : "";
    release_date_error.textContent = !release_date ? "Release date is required" : "";
    actor_error.textContent = !actor ? "Actor is required" : "";
    company_error.textContent = !company ? "Company is required" : "";
    director_error.textContent = !director ? "Director is required" : "";
    genre_error.textContent = genreValues.length === 0 ? "Genre is required" : "";
    status_error.textContent = !status ? "Status is required" : "";

    poster_error.textContent = (posterPath.length <= 8 && imagePoster.files.length === 0) ? "Import poster" : ""
    banner_error.textContent = (bannerPath.length <= 8 && imageBanner.files.length === 0) ? "Import banner" : ""
    let errorElements = [title_en_error, title_vn_error, description_error, short_description_error, duration_error, release_date_error, actor_error, company_error, director_error, genre_error, status_error, poster_error, banner_error];
    errorElements.forEach(element => {
        element.style.color = "red";
    });

    if (
        title_en && title_vn && description && short_description && duration &&
        release_date && actor && company && director && genreValues.length > 0 && status
        && (imagePoster.files.length !== 0 || posterPath.length > 8) && (imageBanner.files.length !== 0 || bannerPath.length > 8)
    ) {
        document.getElementById("saveMovie").submit();
    }
}