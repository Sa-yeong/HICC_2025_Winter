package com.meetple.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomDetailDto {
    private Long chatId;
    private Long targetId;
    private String targetNickname;
    private Long postId;
    private String postTitle;
    private List<MessageSendResponseDto> messages;
}
