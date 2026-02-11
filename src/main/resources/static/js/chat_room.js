const chatId = new URLSearchParams(window.location.search).get('id');
const myId = localStorage.getItem('userId');

document.addEventListener('DOMContentLoaded', async () => {
    if (!chatId) return;
    
    const response = await fetch(`http://localhost:8080/chats/${chatId}`, {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });

    if (response.ok) {
        const data = await response.json();
        document.getElementById('opponent_nickname').innerText = data.targetNickname;
        
        const area = document.getElementById('message_area');
        area.innerHTML = data.messages.map(m => renderMessage(m)).join('');
        scrollToBottom();
        startPolling(); // 실시간 수신 시작
    }
});

// 메시지를 HTML로 변환하는 공통 함수
function renderMessage(m) {
    const isMine = m.writerId == myId;
    return `
        <div class="msg_box ${isMine ? 'mine' : 'other'}" style="margin: 10px; text-align: ${isMine ? 'right' : 'left'};">
            <div style="display: inline-block; padding: 10px; border-radius: 10px; background: ${isMine ? '#dcf8c6' : '#fff'}; border: 1px solid #ddd;">
                <small style="display: block; font-size: 10px; color: #666;">${m.writerNickname}</small>
                <span>${m.message}</span>
                <small style="display: block; font-size: 9px; color: #999; margin-top: 5px;">${m.sendTime}</small>
            </div>
        </div>
    `;
}

// 메시지 전송 함수 (전송 즉시 화면에 추가)
async function sendMessage() {
    const input = document.getElementById('msg_input');
    const messageText = input.value.trim();
    if (!messageText) return;

    const response = await fetch(`http://localhost:8080/chats/${chatId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify({ message: messageText })
    });

    if (response.ok) {
        const newMessage = await response.json(); // 서버에서 생성된 메시지 정보 받기
        const area = document.getElementById('message_area');
        
        // 중요: 서버 응답을 받은 즉시 화면에 메시지 추가
        area.insertAdjacentHTML('beforeend', renderMessage(newMessage));
        input.value = ''; // 입력창 비우기
        scrollToBottom(); // 스크롤 하단 이동
    }
}

// 실시간 수신 (Long Polling)
async function startPolling() {
    try {
        const res = await fetch(`http://localhost:8080/chats/${chatId}/polling`);
        if (res.ok) {
            const newMessage = await res.json();
            const area = document.getElementById('message_area');
            
            // 내가 보낸 메시지가 아닐 때만 화면에 추가 (내가 보낸 건 sendMessage에서 이미 추가함)
            if (newMessage.writerId != myId) {
                area.insertAdjacentHTML('beforeend', renderMessage(newMessage));
                scrollToBottom();
            }
        }
    } catch (e) {
        console.error("Polling error:", e);
    } finally {
        setTimeout(startPolling, 500); // 다시 요청
    }
}

function scrollToBottom() {
    const area = document.getElementById('message_area');
    area.scrollTop = area.scrollHeight;
}

window.addEventListener('beforeunload', () => {
    fetch(`http://localhost:8080/chats/${chatId}/close`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });
});