const urlParams = new URLSearchParams(window.location.search);
const postId = urlParams.get('id');
const mode = urlParams.get('mode'); 

document.addEventListener('DOMContentLoaded', async () => {
    const postForm = document.getElementById('post-form');
    if (!postForm) return;

    // [수정 모드] 기존 데이터를 불러와서 입력창에 채웁니다.
    if (mode === 'edit' && postId) {
        const response = await fetch(`http://localhost:8080/posts/${postId}`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
        });

        if (response.ok) {
            const data = await response.json();
            const post = data.PostObject;

            // 백엔드 응답 키값(대문자)을 HTML input의 name 속성과 매칭합니다.
            document.getElementsByName('Title')[0].value = post.Title || '';
            document.getElementsByName('Genre')[0].value = post.Genre || '';
            document.getElementsByName('Max_people')[0].value = post.Max_people || '';
            document.getElementsByName('Condition')[0].value = post.Condition || '';
            document.getElementsByName('Content')[0].value = post.Content || '';
            
            // 버튼 텍스트를 수정 완료로 변경합니다.
            const submitBtn = postForm.querySelector('input[type="submit"]') || postForm.querySelector('button[type="submit"]');
            if (submitBtn) {
                if (submitBtn.tagName === 'INPUT') submitBtn.value = "수정 완료";
                else submitBtn.innerText = "수정 완료";
            }
        }
    }

    // [등록 및 수정 처리] 폼 제출 시 실행됩니다.
    postForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(postForm);
        
        // 백엔드 PostRequestDto 필드명과 대소문자를 정확히 일치시켜 JSON을 구성합니다.
        const postData = {
            Title: formData.get('Title'),
            Genre: formData.get('Genre'),
            Condition: formData.get('Condition'),
            Content: formData.get('Content'),
            Max_people: parseInt(formData.get('Max_people'))
        };

        // 수정 모드면 PUT, 새 글 작성이면 POST 방식을 사용합니다.
        const isEdit = mode === 'edit' && postId;
        const url = isEdit ? `http://localhost:8080/posts/${postId}` : 'http://localhost:8080/posts';
        const method = isEdit ? 'PUT' : 'POST';

        const res = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            },
            body: JSON.stringify(postData)
        });

        if (res.ok) {
            const result = await res.json();
            alert(isEdit ? '수정되었습니다.' : '등록되었습니다.');
            
            // 성공 후 상세 페이지로 이동합니다.
            const targetId = isEdit ? postId : result.Post_id;
            location.href = `post_detail.html?id=${targetId}`;
        } else {
            alert('처리에 실패했습니다.');
        }
    });
});