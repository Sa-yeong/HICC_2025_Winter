package com.meetple.domain.chat.service;

import com.meetple.domain.chat.dto.ChatCreateRequestDto;
import com.meetple.domain.chat.dto.MessageSendResponseDto;
import com.meetple.domain.chat.dto.MyChatRoomResponseDto;
import com.meetple.domain.chat.entity.Chat;
import com.meetple.domain.chat.entity.Message;
import com.meetple.domain.chat.entity.Participation;
import com.meetple.domain.chat.repository.ChatRepository;
import com.meetple.domain.chat.repository.MessageRepository;
import com.meetple.domain.chat.repository.ParticipationRepository;
import com.meetple.domain.chat.sender.MessageSender;
import com.meetple.domain.member.entity.User;
import com.meetple.domain.member.repository.MemberRepository;
import com.meetple.domain.member.service.MemberService;
import com.meetple.domain.post.entity.Post;
import com.meetple.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ParticipationRepository participationRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final MessageSender messageSender;

    // 1. 채팅방 개설
    private MessageSendResponseDto createChatRoom(User sender, Post post, ChatCreateRequestDto requestDto) {
        // Chat Entity save
        Chat chat = chatRepository.save(Chat.builder().post(post).build());

        // 2 Participate Entity Save
        User target = memberService.getUserById(post.getWriter().getId());
        participationRepository.save(Participation.builder().chat(chat).user(sender).build());
        participationRepository.save(Participation.builder().chat(chat).user(target).build());

        // 메세지 전송 / DB 저장
        Message message = messageRepository.save(Message.builder().user(sender).chat(chat).content(requestDto.getMessage()).build());

        MessageSendResponseDto sendResponseDto = MessageSendResponseDto.builder().chatId(chat.getId()).message(requestDto.getMessage())
                .writerId(sender.getId()).writerNickname(sender.getNickname()).sendTime(message.getCreateTime()).build();

        //messageSender를 이용한 전달
        messageSender.sendMessage(sendResponseDto);

        return sendResponseDto;
    }

    // 2. 채팅방 나가기
    @Transactional
    public void deleteChatRoom(Long chatId, String loginId) {
        // 채팅방에 존재하는 사용자인가
        Participation myParticipation = participationRepository.findByChatIdAndUserLoginId(chatId, loginId)
                .orElseThrow(() -> new IllegalArgumentException("채팅의 참여자가 아닙니다."));

        // 채팅방의 인원이 몇 명인지 확인
        if (participationRepository.countByChatId(chatId) == 1) { // 채팅방의 인원이 자기 자신만 있을 경우
            // chat 자체 삭제
            chatRepository.delete(myParticipation.getChat());
        } else {
            // 사용자의 participate 객체 삭제
            participationRepository.delete(myParticipation);
        }
    }

    // 3. 메세지 보내기
    @Transactional
    public MessageSendResponseDto sendMessage(Long chatId, String loginId, String message) {
        // chat 객체 찾기
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅입니다."));

        // sender 찾기
        User sender = memberService.getUserByLoginId(loginId);

        // 메세지 DB에 저장
        Message content = messageRepository.save(Message.builder().user(sender).chat(chat).content(message).build());

        MessageSendResponseDto sendResponseDto = MessageSendResponseDto.builder().chatId(chat.getId()).message(message)
                .writerId(sender.getId()).writerNickname(sender.getNickname()).sendTime(content.getCreateTime()).build();

        // messageSender 이용
        messageSender.sendMessage(sendResponseDto);

        return sendResponseDto;
    }

    // 4. 내 채팅방 목록 조회
    public List<MyChatRoomResponseDto> getListOfMyChats(String loginId) {
        User user = memberService.getUserByLoginId(loginId);
        List<Chat> listOfChats = chatRepository.findAllChatByUserId(user.getId());
        List<MyChatRoomResponseDto> responseDtoList = new ArrayList<>();

        for (Chat chat : listOfChats) {
            //채팅방의 모든 사용자를 불러와 target 찾기
            User target = null;
            List<Participation> participationList = participationRepository.findAllByChatId(chat.getId());

            for (Participation p : participationList) {
                if (!p.getUser().getLoginId().equals(loginId)) {
                    target = p.getUser();
                    break;
                }
            }

            Message lastMessage = messageRepository.getLastMessageByChatId(chat.getId());
            Integer count = messageRepository.countUnreadMessage(chat.getId(), user.getId());

            // 채팅방의 모든 메세지 불러와서 마지막 메시지 꺼내기
            if (target != null) {
                responseDtoList.add(MyChatRoomResponseDto.builder()
                        .chatId(chat.getId()).targetNickname(target.getNickname())
                        .lastMessage(lastMessage != null ? lastMessage.getContent() : "메시지가 없습니다.")
                        .sentTime(lastMessage != null ? lastMessage.getCreateTime() : chat.getCreateDate())
                        .unreadCount(count).build());
            } else {
                responseDtoList.add(MyChatRoomResponseDto.builder()
                        .chatId(chat.getId()).targetNickname("존재하지 않는 사용자")
                        .lastMessage(lastMessage != null ? lastMessage.getContent() : "메시지가 없습니다.")
                        .sentTime(lastMessage != null ? lastMessage.getCreateTime() : chat.getCreateDate())
                        .unreadCount(count).build());
            }
        }
        return responseDtoList;
    }

    // 5. 채팅방 재입장
    public List<MessageSendResponseDto> openChatRoom(Long chatId) {
        List<Message> messages = messageRepository.getAllMessageByChatId(chatId);

        List<MessageSendResponseDto> responseDtoList = new ArrayList<>();
        Set<Long> participationIds = participationRepository.findAllByChatId(chatId)
                .stream()
                .map(p -> p.getUser().getId())
                .collect(Collectors.toSet());

        for (Message message : messages) {
            User writer = message.getUser();

            String nickname;
            Long writerId;

            if (writer == null) { // 탈퇴하거니 채팅방을 삭제한 유저
                nickname = "존재하지 않는 사용자";
                writerId = null;
            } else if (!participationIds.contains(writer.getId())) {
                nickname = "존재하지 않는 사용자";
                writerId = null;
            } else {
                nickname = writer.getNickname();
                writerId = writer.getId();
            }

            responseDtoList.add(MessageSendResponseDto.builder().chatId(chatId).message(message.getContent())
                    .writerId(writerId)
                    .writerNickname(nickname)
                    .sendTime(message.getCreateTime()).build());
        }

        return responseDtoList;
    }

    // 6. 채팅방 전송 버튼 흐름 관리
    @Transactional
    public MessageSendResponseDto chatFlow(String loginId, ChatCreateRequestDto requestDto) {
        // 사용자 찾기
        User sender = memberService.getUserByLoginId(loginId);

        // 채팅방이 존재하는지 확인
        Chat chat = chatRepository.findByPostAndUser(requestDto.getPostId(), sender.getId()).orElse(null);

        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        if (chat == null) { // 채팅방이 없는 경우
            return createChatRoom(sender, post, requestDto);
        } else { // 채팅방이 있는 경우
           return sendMessage(chat.getId(), sender.getLoginId(), requestDto.getMessage());
        }
    }

    // 7. 채팅방 닫을때
    @Transactional
    public void closeChatRoom(Long chatId, String loginId) {
        User user = memberService.getUserByLoginId(loginId);

        // Participation 객체 불러오기
        Participation target = participationRepository.findByChatAndUser(chatId, user.getId());

        // unread 개수 기준점 새로 고침
        target.updateReadTime();
    }
}
