package com.novelverse.service;

import com.novelverse.dto.NovelNotification;
import com.novelverse.model.Novel;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyNovelPublished(Novel novel) {
        messagingTemplate.convertAndSend("/topic/novels", NovelNotification.builder()
                .type("NOVEL_CREATED")
                .novelId(novel.getId())
                .title(novel.getTitle())
                .message("Добавлено новое ранобэ: " + novel.getTitle())
                .build());
    }
}
