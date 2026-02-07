package com.meetple.domain.chat.dto;

import com.meetple.domain.chat.entity.Chat;
import com.meetple.domain.post.entity.Post;
import com.meetple.domain.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCreateRequestDto {
    private Long postId;
    private String message;
}
