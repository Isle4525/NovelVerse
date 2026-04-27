package com.novelverse.novelverse.controller;


import com.novelverse.novelverse.domain.Novel;
import com.novelverse.novelverse.dto.novel.CreateNovelDTO;
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
        novelService.createNovel(createNovelDTO.title,createNovelDTO.description);
    }

    @GetMapping
    public List<Novel> getAll(){
        return novelService.getAllNovels();
    }
}
