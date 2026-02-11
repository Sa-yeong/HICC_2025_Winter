const postId = new URLSearchParams(window.location.search).get('id');
const myId = localStorage.getItem('userId'); // 로그인 시 저장한 내 ID

document.addEventListener('DOMContentLoaded', async () => {
    if (!postId) return;
    
    const response = await fetch(`http://localhost:8080/posts/${postId}`, {
        method: 'GET',
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });

    if (response.ok) {
        const data = await response.json();
        const post = data.PostObject; 
        
        // 1. 정보 출력 (백엔드 Map의 대문자 키값 사용)
        document.getElementById('detail_writer').innerText = post.Writer_id || '알 수 없음';
        document.getElementById('detail_genre').innerText = post.Genre || '정보 없음';
        document.getElementById('detail_condition').innerText = post.Condition || '정보 없음';
        document.getElementById('detail_content').innerText = post.Content || '내용이 없습니다.';

        // 2. 본인 확인: 내가 쓴 글이 아니면 수정/삭제 버튼 숨기기
        if (String(post.Writer_id) !== String(myId)) {
            const editBtn = document.querySelector('button[onclick="goToEdit()"]');
            const deleteBtn = document.querySelector('button[onclick="deletePost()"]');
            if (editBtn) editBtn.style.display = 'none';
            if (deleteBtn) deleteBtn.style.display = 'none';
        }
    }
});

function goToEdit() {
    location.href = `post.html?mode=edit&id=${postId}`;
}

async function deletePost() {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    const response = await fetch(`http://localhost:8080/posts/${postId}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });
    if (response.ok) {
        alert('삭제되었습니다.');
        location.href = 'whole_post.html';
    } else {
        alert('삭제 권한이 없습니다.');
    }
}

// 스크랩/채팅 함수는 기존과 동일하게 유지
async function toggleScrap() {
    const response = await fetch(`http://localhost:8080/posts/${postId}/scrap`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });
    if (response.ok) {
        const data = await response.json();
        alert(data.Message);
    }
}

async function startChat() {
    const message = prompt('상대방에게 보낼 첫 메시지:');
    if (!message) return;
    const response = await fetch('http://localhost:8080/chats', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify({ postId: parseInt(postId), message: message })
    });
    if (response.ok) {
        const data = await response.json();
        location.href = `chat_room.html?id=${data.chatId}`;
    }
}