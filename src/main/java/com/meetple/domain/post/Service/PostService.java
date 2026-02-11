package com.meetple.domain.post.Service;

import com.meetple.domain.member.entity.Genre; // 장르 Enum
import com.meetple.domain.member.entity.User; // 유저 엔티티
import com.meetple.domain.member.repository.MemberRepository; // 유저 리포지토리
import com.meetple.domain.post.dto.PostRequestDto; // 요청 DTO
import com.meetple.domain.post.entity.Post; // 게시물 엔티티
import com.meetple.domain.post.entity.Scrap; // 스크랩 엔티티
import com.meetple.domain.post.Repository.PostRepository; // 게시물 리포지토리
import com.meetple.domain.post.Repository.ScrapRepository; // 스크랩 리포지토리
import lombok.RequiredArgsConstructor; // 생성자 자동 생성
import org.springframework.data.domain.Page; // 페이지 객체 임포트
import org.springframework.data.domain.Pageable; // 페이징 설정 객체 임포트
import org.springframework.stereotype.Service; // 서비스 컴포넌트 선언
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리

import java.util.Optional;

@Service // 서비스임
@RequiredArgsConstructor // final필드들 생성자 주입
@Transactional(readOnly = true) // 기본적으로 읽기 전용
public class PostService {

    private final PostRepository postRepository;
    private final ScrapRepository scrapRepository;
    private final MemberRepository memberRepository;

//게시물 작성
    @Transactional //쓰기 작업을 위해 Transactional
    public Long createPost(PostRequestDto requestDto, User writer) {
        //작성자=작성자의 아이디로, 존재하지 않으면 넘김
        User managedWriter = memberRepository.findByLoginId(writer.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 데이터 저장
        Post post = requestDto.toEntity(managedWriter);
        return postRepository.save(post).getId(); // 저장 후 고유 ID를 반환합니다.
    }

//게시물 수정
    @Transactional
    public Long updatePost(Long postId, PostRequestDto requestDto, User currentUser) {
        // 게시물 찾기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        //현재 로그인 해 있는 사람이 작성자인지
        validateWriter(post, currentUser);

        //값들을 없데이트
        post.update(
                requestDto.getTitle(),
                requestDto.getGenre(),
                requestDto.getCondition(),
                requestDto.getContent(),
                requestDto.getMax_people()
        );

        return post.getId();
    }

//게시물 삭제
    @Transactional
    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 본인 확인 후 삭제 로직을 수행합니다.
        validateWriter(post, currentUser);
        postRepository.delete(post);
    }

//게시물 상세 조회
    public Post getPostDetail(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

//전체 게시물 조회
    public Page<Post> getAllPosts(Pageable pageable) {

        return postRepository.findAll(pageable);
    }

//장르별 조회
    public Page<Post> getPostsByGenre(Genre genre, Pageable pageable) {

        return postRepository.findAllByGenre(genre, pageable);
    }

//내 게시물 조회
    public Page<Post> getMyPosts(User writer, Pageable pageable) {

        User managedWriter = memberRepository.findByLoginId(writer.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return postRepository.findAllByWriter(managedWriter, pageable);
    }

//스크랩
    public Page<Post> getMyScraps(User user, Pageable pageable) {
        // DB에서 실제 유저를 찾습니다.
        User managedUser = memberRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 스크랩 테이블을 조회하여 해당 유저가 가진 기록들을 가져옵니다.
        Page<Scrap> scraps = scrapRepository.findAllByUser(managedUser, pageable);

        // 스크랩된 기록(Scrap)에서 실제 게시물(Post) 정보만 추출하여 페이지 객체로 변환합니다. [cite: 2025-10-31]
        return scraps.map(Scrap::getPost);
    }

//스크랩 추가, 취소
    @Transactional
    public String toggleScrap(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User managedUser = memberRepository.findByLoginId(user.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 스크랩 전적이 있는지 확인
        Optional<Scrap> scrap = scrapRepository.findByUserAndPost(managedUser, post);

        if (scrap.isPresent()) {
            // 이미 있으면 취소(삭제) 처리합니다.
            scrapRepository.delete(scrap.get());
            return "스크랩 취소 성공";
        } else {
            // 없으면 새로 생성하여 저장합니다.
            Scrap newScrap = Scrap.builder()
                    .user(managedUser)
                    .post(post)
                    .build();
            scrapRepository.save(newScrap);
            return "스크랩 성공";
        }
    }

//작성자와 로그인 한 사람 맞춰보기
    private void validateWriter(Post post, User currentUser) {
        // 아이디 비교
        if (!post.getWriter().getLoginId().equals(currentUser.getLoginId())) {
            throw new IllegalStateException("해당 게시글에 대한 권한이 없습니다.");
        }
    }
}