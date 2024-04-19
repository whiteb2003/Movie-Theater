document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("addEmployeeBtn").addEventListener("click", function () {
        const formData = new FormData(document.getElementById("employeeForm"));
        console.log('a')
        fetch('/employee/manage/search-add', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    response.text().then(text => {
                        if (text.trim() === "Thành công") { // Kiểm tra nội dung của phản hồi
                            const myModal = new bootstrap.Modal(document.getElementById('myModal'));
                            myModal.hide();
                            window.location.reload();
                        }
                    });
                } else {
                    // Hiển thị thông báo lỗi nếu cần
                    response.text().then(data => {
                        console.log(data,"log");
                        document.getElementById("error").innerText = data;
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                // Hiển thị thông báo lỗi nếu cần
                document.getElementById("error").innerText = "Error occurred while adding employee.";
            });
    });
});