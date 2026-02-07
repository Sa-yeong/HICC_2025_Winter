package com.meetple.domain.chat.sender;

import com.meetple.domain.chat.dto.MessageSendResponseDto;
import org.springframework.web.context.request.async.DeferredResult;

public interface MessageSender {
    // 메세지를 있을때
    void sendMessage(MessageSendResponseDto messageDto);

    //클라이언트 요청이 들어왔을 경우 & 메세지가 없는 경우
    void getRequest(Long chatId);

    //클라이언트 삭제
    void removeClient(Long chatId, DeferredResult<MessageSendResponseDto> deferredResult);

}
