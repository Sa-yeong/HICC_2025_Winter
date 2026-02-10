package com.meetple.domain.post.Controller;

import com.meetple.domain.member.entity.Genre; // 장르 Enum 클래스 임포트
import com.meetple.domain.post.dto.PostRequestDto; // 게시물 요청 DTO 임포트
import com.meetple.domain.post.entity.Post; // 게시물 엔티티 임포트
import com.meetple.domain.post.Service.PostService; // 게시물 서비스 임포트
import com.meetple.global.security.CustomUserDetails; // 인증된 유저 정보 클래스 임포트
import lombok.RequiredArgsConstructor; // final 필드 생성자 자동 생성 [cite: 2025-10-31]
import org.springframework.data.domain.Page; // 페이징 결과 객체 임포트
import org.springframework.data.domain.Pageable; // 페이징 설정 객체 임포트
import org.springframework.data.domain.Sort; // 정렬 설정 객체 임포트
import org.springframework.data.web.PageableDefault; // 페이징 기본값 설정 어노테이션 임포트
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 로그인 유저 추출 어노테이션 [cite: 2026-02-08]
import org.springframework.web.bind.annotation.*; // REST 컨트롤러 관련 어노테이션 임포트

import java.util.HashMap;
import java.util.Map;

@RestController // JSON 형태로 데이터를 반환하는 컨트롤러임을 선언합니다. [cite: 2025-10-31]
@RequiredArgsConstructor // 의존성 주입(Service)을 위한 생성자를 자동으로 만듭니다.
public class PostController {

    private final PostService postService; //서비스 객체 생성

//게시물 작성
    @PostMapping("/posts")
    public Map<String, Object> createPost(
            @RequestBody PostRequestDto requestDto, // json으로 프론트의 데이터 받기
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 현재 로그인한 유저 정보를 가져옵니다.

        // 서비스를 호출하여 게시물을 저장하고 생성된 ID를 받습니다.
        Long postId = postService.createPost(requestDto, userDetails.getUser());

        Map<String, Object> response = new HashMap<>(); // 응답용 맵 객체를 생성합니다.
        response.put("Code", "200"); // 성공 상태 코드를 담습니다.
        response.put("Message", "게시물 작성 성공"); // 성공 메시지를 담습니다.
        response.put("Post_id", String.valueOf(postId)); // 생성된 게시물 ID를 문자열로 담습니다.
        return response; // 결과를 JSON으로 반환합니다.
    }

//게시 물 수정
    @PutMapping("/posts/{post_id}")
    public Map<String, Object> updatePost(
            @PathVariable("post_id") Long postId, // URL 경로에 있는 게시물 ID를 가져옵니다.
            @RequestBody PostRequestDto requestDto, // 수정할 데이터를 DTO로 받습니다.
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 현재 로그인 유저 정보를 확인합니다.

        // 서비스를 호출하여 게시물을 수정합니다. (내부에서 작성자 검증을 수행합니다)
        postService.updatePost(postId, requestDto, userDetails.getUser());

        Map<String, Object> response = new HashMap<>(); // 응답 맵 생성
        response.put("Code", "200");
        response.put("Message", "게시물 수정 성공");
        response.put("Post_id", String.valueOf(postId));
        return response;
    }

    /**
     * 3. 게시물 삭제 (DELETE /posts/{post_id})
     */
    @DeleteMapping("/posts/{post_id}")
    public Map<String, Object> deletePost(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 서비스를 호출하여 게시물을 삭제합니다.
        postService.deletePost(postId, userDetails.getUser());

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", "게시물 삭제 성공");
        return response;
    }

    /**
     * 4. 게시물 상세 조회 (GET /posts/{post_id})
     */
    @GetMapping("/posts/{post_id}")
    public Map<String, Object> getPostDetail(@PathVariable("post_id") Long postId) {
        // 서비스를 통해 특정 ID의 게시글 엔티티를 가져옵니다.
        Post post = postService.getPostDetail(postId);

        Map<String, Object> response = new HashMap<>(); // 전체 응답 맵
        response.put("Code", "200");
        response.put("Message", "게시물 조회 성공");

        Map<String, Object> postObject = new HashMap<>(); // 상세 정보를 담을 객체 맵
        postObject.put("Title", post.getTitle()); // 제목
        postObject.put("Genre", post.getGenre()); // 장르
        postObject.put("Condition", post.getCondition()); // 조건
        postObject.put("Content", post.getContent()); // 내용
        postObject.put("Writer_id", post.getWriter().getLoginId()); // 작성자의 로그인 아이디

        response.put("PostObject", postObject); // 최종 응답에 상세 정보 객체를 포함시킵니다.
        return response;
    }

    /**
     * 5, 6. 목록 조회 (전체/장르별/내 게시물 페이징 처리)
     * @PageableDefault: 한 페이지에 20개, ID 기준 내림차순(최신순)을 기본으로 설정합니다. [cite: 2025-10-31]
     */
    @GetMapping("/posts")
    public Map<String, Object> getPosts(
            @RequestParam(name = "Genre", required = false) Genre genre, // 선택한 장르
            @RequestParam(name = "Type", required = false) String type, // 'all'인 경우 전체 조회
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, // 페이징 설정
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<Post> postPage; // 페이징 결과가 담길 페이지 객체입니다.
        String message; // 응답 메시지 변수입니다.

        if ("all".equals(type)) { // 사이드바에서 '전체보기'를 눌렀을 경우
            postPage = postService.getAllPosts(pageable); // 모든 게시물 조회
            message = "전체 게시물 조회 성공";
        } else if (genre != null) { // 특정 장르를 선택했을 경우
            postPage = postService.getPostsByGenre(genre, pageable); // 해당 장르 게시물 조회
            message = "장르별 게시물 조회 성공";
        } else { // 파라미터가 없으면 '내 게시물'을 기본으로 보여줍니다.
            postPage = postService.getMyPosts(userDetails.getUser(), pageable); // 내 게시물 조회
            message = "내 게시물 조회 성공";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", message);
        response.put("Post_list", postPage.getContent()); // 실제 데이터 리스트만 추출하여 전달합니다.
        response.put("TotalPages", postPage.getTotalPages()); // 프론트엔드 페이징 버튼 생성을 위해 전체 페이지 수를 전달합니다.

        return response;
    }

    /**
     * 7. 내가 스크랩한 목록 조회 (페이징 적용)
     */
    @GetMapping("/posts/scrap")
    public Map<String, Object> getMyScraps(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 서비스를 통해 페이징된 스크랩 목록을 가져옵니다.
        Page<Post> postPage = postService.getMyScraps(userDetails.getUser(), pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", "스크랩 목록 조회 성공");
        response.put("Post_list", postPage.getContent()); // 데이터 리스트
        response.put("TotalPages", postPage.getTotalPages()); // 전체 페이지 수
        return response;
    }

    /**
     * 8. 스크랩 버튼 토글 (POST /posts/{post_id}/scrap)
     */
    @PostMapping("/posts/{post_id}/scrap")
    public Map<String, Object> toggleScrap(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 스크랩을 추가하거나 삭제하는 로직을 수행합니다.
        String message = postService.toggleScrap(postId, userDetails.getUser());

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", message);
        return response;
    }
}