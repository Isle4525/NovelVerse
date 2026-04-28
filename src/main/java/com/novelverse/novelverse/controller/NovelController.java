package com.novelverse.novelverse.controller;


import com.novelverse.novelverse.domain.Novel;
import com.novelverse.novelverse.dto.novel.CreateNovelDTO;
import com.novelverse.novelverse.dto.novel.UpdateNovelDTO;
import com.novelverse.novelverse.service.NovelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/novels")
public class NovelController {

    private final NovelService novelService;
    public NovelController(NovelService novelService) {
        this.novelService = novelService;
    }

    @PostMapping
    public void createNovel(@RequestBody CreateNovelDTO createNovelDTO) {
        novelService.createNovel(createNovelDTO.title, createNovelDTO.description, createNovelDTO.coverUrl);
    }

    @GetMapping
    public List<Novel> getAll(){
        return novelService.getAllNovels();
    }

    @GetMapping("/{id}")
    public Novel getById(@PathVariable long id) {
        return novelService.getNovelById(id);
    }

    @PutMapping("/{id}")
    public void updateNovel(@PathVariable long id, @RequestBody UpdateNovelDTO updateNovelDTO) {
        novelService.updateNovel(id, updateNovelDTO.title, updateNovelDTO.description, updateNovelDTO.coverUrl);
    }

    @DeleteMapping("/{id}")
    public void deleteNovel(@PathVariable long id) {
        novelService.deleteNovel(id);
    }
}
