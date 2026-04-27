package com.novelverse.novelverse.repository;

import com.novelverse.novelverse.domain.Chapter;

import java.util.List;

public interface ChapterRepository {
    List<Chapter> findByNovelId(long novelId);
    void save(Chapter chapter);

}
