document.addEventListener('DOMContentLoaded', () => {
    const signupForm = document.getElementById('signup-form');
    if (!signupForm) return;

    signupForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(signupForm);

        if (formData.get('passwd') !== formData.get('passwd_confirm')) {
            alert('비밀번호가 서로 다릅니다.');
            return;
        }

        const signupData = {
            loginId: formData.get('id'),
            password: formData.get('passwd'),
            nickname: formData.get('nickname'),
            birthDate: formData.get('birthDate'),
            gender: formData.get('gender'),
            preferences: formData.getAll('preferences')
        };

        try {
            const response = await fetch('http://localhost:8080/users', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(signupData)
            });

            if (response.ok) {
                alert('회원가입 완료!');
                location.href = 'login.html';
            }
            else{
                const errorData = await response.json();
                alert(errorData.message || '회원가입에 실패했습니다.');
            }
        } catch (error) {
            console.error(error);
        }
    });
});