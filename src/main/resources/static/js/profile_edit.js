const userId = localStorage.getItem('userId');

document.addEventListener('DOMContentLoaded', async () => {
    const response = await fetch(`http://localhost:8080/users/${userId}`, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });
    if (response.ok) {
        const data = await response.json();
        document.getElementById('edit_nickname').placeholder = data.nickname;
    }
});

document.getElementById('edit-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const updateData = {
        password: formData.get('password'),
        nickname: formData.get('nickname'),
        preferences: formData.getAll('preferences')
    };

    const response = await fetch(`http://localhost:8080/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify(updateData)
    });

    if (response.ok) {
        alert('수정 완료!');
        location.href = 'mypage.html';
    }
});