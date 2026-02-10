package com.meetple.domain.chat.controller;

import com.meetple.domain.chat.dto.*;
import com.meetple.domain.chat.sender.MessageSender;
import com.meetple.domain.chat.service.ChatService;
import com.meetple.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final JwtProvider jwtProvider;
    private final MessageSender messageSender;

    // 전체적인 채팅방 흐름
    @PostMapping(value = "/chats")
    public MessageSendResponseDto chatFlow(@RequestHeader("Authorization") String authHeader, @RequestBody ChatCreateRequestDto requestDto) {
        // 토큰 인증
        String token = authHeader.replace("Bearer ", "");
        jwtProvider.validateJwt(token); //토큰 검사

        return chatService.chatFlow(jwtProvider.getLoginId(token), requestDto);
    }

    // 특정 채팅방에 메시지 보내기
    @PostMapping(value = "/chats/{chat_id}")
    public MessageSendResponseDto sendMessage(@PathVariable("chat_id") Long chatId, @RequestHeader("Authorization") String authHeader, @RequestBody MessageRequestDto requestDto) {
        String token = authHeader.replace("Bearer ", "");
        jwtProvider.validateJwt(token);

        String message = requestDto.getMessage();

        return chatService.sendMessage(chatId, jwtProvider.getLoginId(token), message);
    }

    // 채팅방 삭제하기
    @DeleteMapping(value = "/chats/{chat_id}")
    public ResponseEntity<String> deleteChatRoom(@PathVariable("chat_id") Long chatId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        jwtProvider.validateJwt(token);

        chatService.deleteChatRoom(chatId, jwtProvider.getLoginId(token));

        return ResponseEntity.ok("채팅방이 삭제되었습니다.");
    }

    // 채팅방 나가기
    @PostMapping(value = "/chats/{chat_id}/close")
    public ResponseEntity<String> closeChatRoom(@PathVariable("chat_id") Long chatId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        jwtProvider.validateJwt(token);

        chatService.closeChatRoom(chatId, jwtProvider.getLoginId(token));

        return ResponseEntity.ok("채팅방을 닫았습니다.");
    }

    // 모든 내 채팅방 조회하기
    @GetMapping(value = "/chats")
    public List<MyChatRoomResponseDto> getMyChatRooms(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        jwtProvider.validateJwt(token);

        return chatService.getListOfMyChats(jwtProvider.getLoginId(token));
    }

    // 채팅방 재입장
    @GetMapping(value = "/chats/{chat_id}")
    public ChatRoomDetailDto openChatRoom(@PathVariable("chat_id") Long chatId, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        jwtProvider.validateJwt(token);

        return chatService.openChatRoom(chatId, jwtProvider.getLoginId(token));
    }

    // longPolling 요청
    @GetMapping(value = "/chats/{chat_id}/polling")
    public DeferredResult<MessageSendResponseDto> pollingMessage(@PathVariable("chat_id") Long chatId) {
        return messageSender.getRequest(chatId);
    }
}
