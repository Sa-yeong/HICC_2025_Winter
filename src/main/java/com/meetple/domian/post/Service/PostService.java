package com.meetple.domian.post.Service; // 사용자님의 대문자 Service 경로 유지

import com.meetple.domian.member.entity.Genre;
import com.meetple.domian.member.entity.User;
import com.meetple.domian.member.repository.MemberRepository;
import com.meetple.domian.post.dto.PostRequestDto;
import com.meetple.domian.post.entity.Post;
import com.meetple.domian.post.entity.Scrap;
import com.meetple.domian.post.Repository.PostRepository;
import com.meetple.domian.post.Repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final ScrapRepository scrapRepository;
    private final MemberRepository memberRepository;

    /**
     * 1. 게시물 작성: 토큰 유저의 loginId로 DB에서 실제 유저를 찾아와서 저장합니다. [cite: 2026-02-06]
     */
    @Transactional
    public Long createPost(PostRequestDto requestDto, User writer) {
        User managedWriter = memberRepository.findByLoginId(writer.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Post post = requestDto.toEntity(managedWriter);
        return postRepository.save(post).getId();
    }

    /**
     * 2. 게시물 수정: Dirty Checking을 이용하여 수정합니다.
     */
    @Transactional
    public Long updatePost(Long postId, PostRequestDto requestDto, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID=" + postId));

        validateWriter(post, currentUser);

        post.update(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getCondition(),
                requestDto.getContent(),
                requestDto.getMax_people()
        );

        return post.getId();
    }

    /**
     * 3. 게시물 삭제: 작성자 본인 확인 후 삭제합니다.
     */
    @Transactional
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        validateWriter(post, currentUser);
        postRepository.delete(post);
    }

    /**
     * 4. 상세 조회: ID로 게시글을 찾습니다.
     */
    public Post getPostDetail(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    /**
     * 5. 장르별 조회: 특정 장르의 글들을 가져옵니다.
     */
    public List<Post> getPostsByGenre(Genre genre) {
        return postRepository.findAllByGenre(genre);
    }

    /**
     * 6. 내 글 조회: [수정] 조회 전 유저를 영속화하여 에러를 방지합니다. [cite: 2026-02-06]
     */
    public List<Post> getMyPosts(User writer) {
        User managedWriter = memberRepository.findByLoginId(writer.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return postRepository.findAllByWriter(managedWriter);
    }

    /**
     * 7. 스크랩 토글: [핵심] managedUser를 사용하여 Scrap 객체를 생성합니다. [cite: 2026-02-06]
     */
    @Transactional
    public String toggleScrap(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 토큰 유저를 DB 유저로 교체하여 영속성 컨텍스트에 등록 [cite: 2025-10-31]
        User managedUser = memberRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Optional<Scrap> scrap = scrapRepository.findByUserAndPost(managedUser, post);

        if (scrap.isPresent()) {
            scrapRepository.delete(scrap.get());
            return "스크랩 취소 성공";
        } else {
            // [주의] 반드시 managedUser를 빌더에 넣어야 합니다.
            Scrap newScrap = Scrap.builder()
                    .user(managedUser)
                    .post(post)
                    .build();
            scrapRepository.save(newScrap);
            return "스크랩 성공";
        }
    }

    /**
     * 8. 내가 스크랩한 목록 조회 (GET /posts/scrap)
     * @param user 토큰에서 추출된 임시 유저 정보
     */
    public List<Post> getMyScraps(User user) {
        // 8-1. [핵심] 임시 유저 정보를 바탕으로 DB에서 관리되는 진짜 유저(Managed User)를 찾습니다.
        User managedUser = memberRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 8-2. 관리되는 유저 객체를 사용하여 스크랩 기록을 모두 조회합니다.
        List<Scrap> scraps = scrapRepository.findAllByUser(managedUser);

        // 8-3. 스크랩 기록들에서 실제 '게시글(Post)' 정보만 쏙 뽑아서 리스트로 반환합니다. [cite: 2025-10-31]
        return scraps.stream()
                .map(Scrap::getPost)
                .toList();
    }

    private void validateWriter(Post post, User currentUser) {
        if (!post.getWriter().getLoginId().equals(currentUser.getLoginId())) {
            throw new IllegalStateException("해당 게시글에 대한 권한이 없습니다.");
        }
    }
}