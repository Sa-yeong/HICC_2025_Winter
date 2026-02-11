let currentIndex = 0;
const totalImages = 10;
const visibleImages = 4;

document.addEventListener('DOMContentLoaded', () => {
    const nickname = localStorage.getItem('nickname');
    if (nickname) {
        const welcomeText = document.querySelector('.header_text h3');
        if (welcomeText) welcomeText.innerText = `Welcome back, ${nickname}!`;
    }

    const nextBtn = document.getElementById('genre_next');
    if (nextBtn) {
        nextBtn.addEventListener('click', () => moveSlider('next'));
    }
});

