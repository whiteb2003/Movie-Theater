function showTab(tabId) {
    // Ẩn tất cả các tab
    var tabs = document.querySelectorAll('.sbox');
    tabs.forEach(function (tab) {
        tab.style.display = 'none';
    });

    // Hiển thị tab được chọn
    document.getElementById(tabId).style.display = 'block';
}
