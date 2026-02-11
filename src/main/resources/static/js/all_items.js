let currentPage = 1;
const type = new URLSearchParams(window.location.search).get('type');

document.addEventListener('DOMContentLoaded', fetchItems);

async function fetchItems() {
    let url = '';
    if (type === 'post') {
        url = `http://localhost:8080/posts?Type=mine&page=${currentPage - 1}&size=20`; //내가 쓴 글 가져오기
    } else {
        url = `http://localhost:8080/posts/scrap?page=${currentPage - 1}&size=20`; //스크한것 가져오기
    }

    const response = await fetch(url, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });

    if (response.ok) {
        const data = await response.json();
        
        const pageTitle = document.getElementById('page_title');
        pageTitle.innerText = type === 'post' ? 'Mypost' : 'Myscrap';

        const listContainer = document.getElementById('full_list');
        if (data.Post_list && data.Post_list.length > 0) {
            listContainer.innerHTML = data.Post_list.map(p => 
                `<li><a href="post_detail.html?id=${p.id}">${p.title}</a></li>`
            ).join('');
        } else {
            listContainer.innerHTML = '<li>데이터가 없습니다.</li>';
        }

        document.getElementById('page_num').innerText = currentPage;
    }
}

function changePage(step) {
    currentPage += step;
    if (currentPage < 1) currentPage = 1;
    fetchItems();
}