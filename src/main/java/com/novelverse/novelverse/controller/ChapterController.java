package com.novelverse.novelverse.controller;

import com.novelverse.novelverse.domain.Chapter;
import com.novelverse.novelverse.dto.chapter.CreateChapterDTO;
import com.novelverse.novelverse.dto.chapter.UpdateChapterDTO;
import com.novelverse.novelverse.service.ChapterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapters")
public class ChapterController {
    private final ChapterService chapterService;
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @PostMapping
    public void createChapter(@RequestBody CreateChapterDTO createChapterDTO) {
        chapterService.create(
                createChapterDTO.novelId,
                createChapterDTO.title,
                createChapterDTO.content,
                createChapterDTO.imageUrl
        );
    }

    @GetMapping("/novel/{id}")
    public List<Chapter> getChapters(@PathVariable Long id){
        return chapterService.getChapters(id);
    }

    @GetMapping("/{id}")
    public Chapter getChapter(@PathVariable Long id) {
        return chapterService.getChapter(id);
    }

    @PutMapping("/{id}")
    public void updateChapter(@PathVariable Long id, @RequestBody UpdateChapterDTO updateChapterDTO) {
        chapterService.update(id, updateChapterDTO.title, updateChapterDTO.content, updateChapterDTO.imageUrl);
    }

    @DeleteMapping("/{id}")
    public void deleteChapter(@PathVariable Long id) {
        chapterService.delete(id);
    }
}
