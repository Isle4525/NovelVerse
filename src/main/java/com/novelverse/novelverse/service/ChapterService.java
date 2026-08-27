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

    public void create(Long novelId, String title, String content, String imageUrl){
        Chapter chapter = new Chapter();

        chapter.setNovelId(novelId);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setImageUrl(imageUrl);
        chapterRepository.save(chapter);
    }


    public List<Chapter> getChapters(Long novelId){
        return chapterRepository.findByNovelId(novelId);
    }

    public Chapter getChapter(long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId);
        if (chapter == null) {
            throw new RuntimeException("Chapter not found");
        }
        return chapter;
    }

    public void update(long chapterId, String title, String content, String imageUrl) {
        Chapter chapter = getChapter(chapterId);
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setImageUrl(imageUrl);
        chapterRepository.update(chapter);
    }

    public void delete(long chapterId) {
        getChapter(chapterId);
        chapterRepository.delete(chapterId);
    }
}
