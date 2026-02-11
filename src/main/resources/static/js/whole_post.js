// 페이지 로드 시 'all' 장르(전체보기)로 게시글을 불러옵니다.
document.addEventListener('DOMContentLoaded', () => loadPosts('all'));

async function loadPosts(genre) {
    // 1. 기본 URL 설정: 페이징 사이즈는 20으로 고정합니다.
    let url = 'http://localhost:8080/posts?size=20';
    
    // 2. 파라미터 분기 처리
    if (genre === 'all') {
        // [중요] 백엔드 PostController 로직에 따라 전체보기를 하려면 Type=all을 명시해야 합니다.
        // 이 파라미터가 없으면 서버는 기본적으로 '내 게시물'만 반환합니다.
        url += '&Type=all';
    } else {
        // 특정 장르를 선택한 경우 Genre 파라미터를 추가합니다.
        url += `&Genre=${genre}`;
    }

    const response = await fetch(url, {
        method: 'GET',
        headers: {
            // 서버의 SecurityConfig 설정에 따라 모든 요청에 토큰이 필요합니다.
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        }
    });

    if (response.ok) {
        const data = await response.json();
        // 현재 선택된 장르 이름을 화면 타이틀에 표시합니다.
        document.getElementById('current_genre_title').innerText = genre === 'all' ? '전체 게시물' : genre;
        
        const grid = document.getElementById('post_grid');
        
        // 데이터가 없는 경우를 대비한 안내 문구 처리입니다.
        if (!data.Post_list || data.Post_list.length === 0) {
            grid.innerHTML = '<div class="post_placeholder">등록된 게시물이 없습니다.</div>';
            return;
        }

        // 서버에서 받은 게시글 목록을 HTML 카드로 변환하여 화면에 그립니다.
        grid.innerHTML = data.Post_list.map(p => `
            <div class="post_item" onclick="location.href='post_detail.html?id=${p.id}'" style="cursor:pointer; border-bottom:1px solid #ddd; padding:15px; margin-bottom:10px;">
                <strong>${p.title}</strong>
                <p>${p.genre} | ${p.maxPeople}명 모집</p>
                <small>작성자 ID: ${p.writer ? p.writer.loginId : '익명'}</small>
            </div>
        `).join('');
    }
}