package com.example.demo.domain.post.controller;

import com.example.demo.domain.member.entity.Genre;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // 1. 게시물 작성
    @PostMapping
    public ResponseEntity<Map<String, Object>> writePost(
            @RequestBody PostRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PostResponseDto responseDto = postService.writePost(requestDto, userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "게시물 작성 성공");
        response.put("post_id", responseDto.getPostId());

        return ResponseEntity.ok(response);
    }

    // 2. 게시물 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PostResponseDto responseDto = postService.updatePost(postId, requestDto, userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "게시물 수정 성공");
        response.put("post_id", responseDto.getPostId());

        return ResponseEntity.ok(response);
    }

    // 3. 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        postService.deletePost(postId, userDetails.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "게시물 삭제 성공");

        return ResponseEntity.ok(response);
    }

    // 4. 게시물 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Long postId) {
        PostResponseDto postDto = postService.getPost(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "게시물 조회 성공");
        response.put("post_object", postDto);

        return ResponseEntity.ok(response);
    }

    // 5. 게시물 목록 조회 (전체/장르별/내글)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPostList(
            @RequestParam(required = false) Genre genre,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<PostResponseDto> postList;
        String message;

        if (genre != null) {
            postList = postService.getPostList(genre);
            message = "장르별 게시물 조회 성공";
        } else {
            postList = postService.getMyPostList(userDetails.getUsername());
            message = "내 게시물 조회 성공";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", message);
        response.put("post_list", postList);

        return ResponseEntity.ok(response);
    }

    // 6. 스크랩 토글 (POST /posts/{post_id}/scrap)
    @PostMapping("/{postId}/scrap")
    public ResponseEntity<Map<String, String>> toggleScrap(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String message = postService.toggleScrap(postId, userDetails.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    // 7. 내 스크랩 목록 조회 (GET /posts/scraps)
    @GetMapping("/scraps")
    public ResponseEntity<Map<String, Object>> getScrapList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<PostResponseDto> postList = postService.getMyScrapList(userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "스크랩 조회 성공");
        response.put("post_list", postList);

        return ResponseEntity.ok(response);
    }
}