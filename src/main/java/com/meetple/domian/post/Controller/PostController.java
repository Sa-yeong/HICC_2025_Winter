package com.meetple.domian.post.Controller;

import com.meetple.domian.member.entity.Genre;
import com.meetple.domian.post.dto.PostRequestDto;
import com.meetple.domian.post.entity.Post;
import com.meetple.domian.post.Service.PostService;
import com.meetple.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 1. 게시물 작성 (POST /posts)
     * 명세서 응답: Code, Message, Post_id
     */
    @PostMapping("/posts")
    public Map<String, Object> createPost(
            @RequestBody PostRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long postId = postService.createPost(requestDto, userDetails.getUser());

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", "게시물 작성 성공");
        response.put("Post_id", String.valueOf(postId));
        return response;
    }

    /**
     * 2. 게시물 수정 (PUT /posts/{post_id})
     */
    @PutMapping("/posts/{post_id}")
    public Map<String, Object> updatePost(
            @PathVariable("post_id") Long postId,
            @RequestBody PostRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.updatePost(postId, requestDto, userDetails.getUser());

        Map<String, Object> response = new HashMap<>();
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

        postService.deletePost(postId, userDetails.getUser());

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", "게시물 삭제 성공");
        return response;
    }

    /**
     * 4. 게시물 상세 조회 (GET /posts/{post_id})
     * 명세서 응답: PostObject { Title, Genre, Condition, Content, Writer_id }
     */
    @GetMapping("/posts/{post_id}")
    public Map<String, Object> getPostDetail(@PathVariable("post_id") Long postId) {
        Post post = postService.getPostDetail(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", "게시물 조회 성공");

        Map<String, Object> postObject = new HashMap<>();
        postObject.put("Title", post.getTitle());
        postObject.put("Genre", post.getGenre());
        postObject.put("Condition", post.getCondition());
        postObject.put("Content", post.getContent());
        postObject.put("Writer_id", post.getWriter().getLoginId());

        response.put("PostObject", postObject);
        return response;
    }

    /**
     * 5 & 6. 목록 조회 (장르별 또는 내 게시물)
     * 명세서 파라미터: Genre (대문자 주의) [cite: 2026-02-06]
     */
    @GetMapping("/posts")
    public Map<String, Object> getPosts(
            @RequestParam(name = "Genre", required = false) Genre genre, // 명세서의 'Genre'와 매핑
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<Post> postList;
        String message;

        if (genre != null) {
            postList = postService.getPostsByGenre(genre);
            message = "장르별 게시물 조회 성공";
        } else {
            postList = postService.getMyPosts(userDetails.getUser());
            message = "내 게시물 조회 성공";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", message);
        response.put("Post_list", postList);
        return response;
    }

    /**
     * 7. 내가 스크랩한 목록 조회 (GET /posts/scrap)
     */
    @GetMapping("/posts/scrap")
    public Map<String, Object> getMyScraps(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Post> postList = postService.getMyScraps(userDetails.getUser());

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", "스크랩 목록 조회 성공");
        response.put("Post_list", postList);
        return response;
    }

    /**
     * 8. 스크랩 버튼 토글 (POST /posts/{post_id}/scrap)
     */
    @PostMapping("/posts/{post_id}/scrap")
    public Map<String, Object> toggleScrap(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String message = postService.toggleScrap(postId, userDetails.getUser());

        Map<String, Object> response = new HashMap<>();
        response.put("Code", "200");
        response.put("Message", message);
        return response;
    }
}