package com.meetple.domain.chat.sender;

import com.meetple.domain.chat.dto.MessageSendResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class LongPollingSender implements MessageSender {
    // 메세지 응답을 기다리고 있는 클라이언트
    private Map<Long, List<DeferredResult<MessageSendResponseDto>>> waitingClients = new ConcurrentHashMap<>();

    @Override
    public void sendMessage(MessageSendResponseDto messageDto) {
        // 해당하는 채팅방 대기자 아이디 불러오기
        List<DeferredResult<MessageSendResponseDto>> targets = waitingClients.get(messageDto.getChatId());

        // 해당 채팅 방 유저에게 메세지 보내기 & 연결 끊기
        if (targets != null) {
            for (DeferredResult<MessageSendResponseDto> target : targets){
                target.setResult(messageDto);
            }
        }

        // waitingClients 삭제
        waitingClients.remove(messageDto.getChatId());
    }

    @Override
    public DeferredResult<MessageSendResponseDto> getRequest(Long chatId) { // 클라이언트를 waitingClients에 등록
        DeferredResult<MessageSendResponseDto> deferredResult = new DeferredResult<>(30000L);

        // waitingClients에 등록
        waitingClients.computeIfAbsent(chatId, k -> new CopyOnWriteArrayList<>()).add(deferredResult);

        // 스레드 방출
        // 메세지 응답 성공
        deferredResult.onCompletion(() -> removeClient(chatId, deferredResult));
        // 타임아웃시
        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult("TimeOut");
            removeClient(chatId, deferredResult);
        });

        return deferredResult;
    }

    @Override
    public void removeClient(Long chatId, DeferredResult<MessageSendResponseDto> deferredResult) {
        //list가 널인지 확인
        List<DeferredResult<MessageSendResponseDto>> targets = waitingClients.get(chatId);

        if (targets != null) {
            targets.remove(deferredResult);
        }
    }
}
