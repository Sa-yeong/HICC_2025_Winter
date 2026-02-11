document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    if (!loginForm) return;

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(loginForm);
        const loginData = {
            loginId: formData.get('id'),
            password: formData.get('passwd')
        };

        try {
            const response = await fetch('http://localhost:8080/users/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(loginData)
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('userId', data.userId);
                localStorage.setItem('nickname', data.nickname);
                location.href = 'mainpage.html';
            } else {
                alert('아이디 또는 비밀번호를 확인해주세요.');
            }
        } catch (error) {
            console.error(error);
        }
    });
});