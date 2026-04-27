package com.novelverse.novelverse.service;


import com.novelverse.novelverse.domain.Chapter;
import com.novelverse.novelverse.repository.ChapterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;
    public ChapterService(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }

    public void create(Long userId, String title, String content){
        Chapter chapter = new Chapter();

        chapter.setNovelId(userId);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapterRepository.save(chapter);
    }


    public List<Chapter> getChapters(Long novelId){
        return chapterRepository.findByNovelId(novelId);
    }
}
