package com.example.demo.domain.post.service;

import com.example.demo.domain.member.entity.Genre;
import com.example.demo.domain.member.entity.User;
import com.example.demo.domain.member.repository.UserRepository;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.entity.Post;
import com.example.demo.domain.post.entity.Scrap;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.post.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;

    // 1. 게시글 작성
    @Transactional
    public PostResponseDto writePost(PostRequestDto requestDto, String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = requestDto.toEntity(user);
        postRepository.save(post);

        return new PostResponseDto(post);
    }

    // 2. 게시글 상세 조회
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return new PostResponseDto(post);
    }

    // 3. 게시글 목록 조회 (전체 or 장르별)
    public List<PostResponseDto> getPostList(Genre genre) {
        List<Post> posts;
        if (genre == null) {
            posts = postRepository.findAllByOrderByCreatedAtDesc();
        } else {
            posts = postRepository.findByGenreOrderByCreatedAtDesc(genre);
        }
        return posts.stream().map(PostResponseDto::new).collect(Collectors.toList());
    }

    // 4. 내 게시물 조회
    public List<PostResponseDto> getMyPostList(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return postRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(PostResponseDto::new).collect(Collectors.toList());
    }

    // 5. 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, String loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(requestDto.getTitle(), requestDto.getGenre(), requestDto.getCondition(),
                requestDto.getContent(), requestDto.getMaxPeople());

        return new PostResponseDto(post);
    }

    // 6. 게시글 삭제
    @Transactional
    public void deletePost(Long postId, String loginId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getLoginId().equals(loginId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    // 7. 스크랩 토글 (추가)
    @Transactional
    public String toggleScrap(Long postId, String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Optional<Scrap> scrap = scrapRepository.findByUserAndPost(user, post);

        if (scrap.isPresent()) {
            scrapRepository.delete(scrap.get());
            return "스크랩 취소 성공";
        } else {
            scrapRepository.save(Scrap.builder().user(user).post(post).build());
            return "스크랩 성공";
        }
    }

    // 8. 내 스크랩 목록 조회 (추가)
    public List<PostResponseDto> getMyScrapList(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return scrapRepository.findByUser(user).stream()
                .map(scrap -> new PostResponseDto(scrap.getPost()))
                .collect(Collectors.toList());
    }
}