package com.meetple.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomDetailDto {
    private Long chatId;
    private Long postId;
    private Long myId;
    private List<MessageSendResponseDto> messages;
}
