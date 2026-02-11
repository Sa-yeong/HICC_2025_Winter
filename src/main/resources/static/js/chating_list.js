document.addEventListener('DOMContentLoaded', async () => {
    const response = await fetch('http://localhost:8080/chats', {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
    });
    if (response.ok) {
        const rooms = await response.json();
        const container = document.getElementById('room_container');
        if (rooms.length === 0) {
            container.innerHTML = '<li>채팅 내역이 없습니다.</li>';
            return;
        }
        container.innerHTML = rooms.map(room => `
            <li class="room_item" onclick="location.href='chat_room.html?id=${room.chatId}'">
                <strong>${room.targetNickname}</strong>
                <p>${room.lastMessage || ''}</p>
                <small>안읽음: ${room.unreadCount}</small>
            </li>
        `).join('');
    }
});