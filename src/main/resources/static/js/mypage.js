document.addEventListener('DOMContentLoaded', () => {
    const nickname = localStorage.getItem('nickname');
    if (nickname) {
        const welcome = document.getElementById('welcome_text');
        if (welcome) welcome.innerText = `Welcome back, ${nickname}!`;
    }
    loadSummaries();
});

async function loadSummaries() {
    const token = localStorage.getItem('accessToken');
    const headers = { 'Authorization': `Bearer ${token}` };

    const postRes = await fetch('http://localhost:8080/posts?size=3', { headers });
    const scrapRes = await fetch('http://localhost:8080/posts/scrap?size=3', { headers });

    if (postRes.ok) {
        const data = await postRes.json();
        document.getElementById('post_list_summary').innerHTML = data.Post_list.map(p => 
            `<li><a href="post_detail.html?id=${p.id}">${p.title}</a></li>`
        ).join('');
    }
    if (scrapRes.ok) {
        const data = await scrapRes.json();
        document.getElementById('scrap_list_summary').innerHTML = data.Post_list.map(p => 
            `<li><a href="post_detail.html?id=${p.id}">${p.title}</a></li>`
        ).join('');
    }
}

function toggleMenu(event) {
    event.stopPropagation();
    const menu = document.getElementById('header_menu');
    menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
}

function editProfile() {
    location.href = `profile_edit.html?id=${localStorage.getItem('userId')}`;
}

function logout() {
    localStorage.clear();
    location.href = 'login.html';
}

async function deleteAccount() {
    const password = prompt('탈퇴를 위해 비밀번호를 입력해주세요.');
    if (!password) return;

    const response = await fetch('http://localhost:8080/users', {
        method: 'DELETE',
        headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify({ password })
    });

    if (response.ok) {
        localStorage.clear();
        location.href = 'firstpage.html';
    }
}