package com.novelverse.service;

import com.novelverse.model.Novel;
import com.novelverse.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NovelService {

    private final NovelRepository novelRepository;
    private final NotificationService notificationService;

    public Novel createNovel(Novel novel) {
        LocalDateTime now = LocalDateTime.now();
        if (novel.getCreatedAt() == null) {
            novel.setCreatedAt(now);
        }
        novel.setUpdatedAt(now);
        if (novel.getViews() == null) {
            novel.setViews(0L);
        }
        if (novel.getBookmarksCount() == null) {
            novel.setBookmarksCount(0L);
        }
        Novel savedNovel = novelRepository.save(novel);
        notificationService.notifyNovelPublished(savedNovel);
        return savedNovel;
    }
}
