package com.novelverse.novelverse.repository;

import com.novelverse.novelverse.domain.Chapter;

import java.util.List;

public interface ChapterRepository {
    List<Chapter> findByNovelId(long novelId);
    Chapter findById(long chapterId);
    void save(Chapter chapter);
    void update(Chapter chapter);
    void delete(long chapterId);
}
