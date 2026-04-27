package com.novelverse.novelverse.repository;

import com.novelverse.novelverse.domain.Bookmark;

import java.util.List;

public interface BookmarkRepository {
    void save(Bookmark bookmark);
    List<Bookmark> findByUserId(Long userId);
    void delete(Long userId, Long chapterId);
}
