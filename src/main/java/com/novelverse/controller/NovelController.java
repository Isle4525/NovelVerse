package com.novelverse.controller;

import com.novelverse.model.Novel;
import com.novelverse.repository.NovelRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/novels")
@Tag(name = "Novels", description = "API for reading novels")
public class NovelController {

    @Autowired
    private NovelRepository novelRepository;

    @GetMapping
    @Operation(summary = "Get all novels")
    public List<Novel> getAllNovels() {
        return novelRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Add a new novel")
    public Novel addNovel(@RequestBody Novel novel) {
        return novelRepository.save(novel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get novel by ID")
    public Novel getNovelById(@PathVariable Long id) {
        return novelRepository.findById(id).orElse(null);
    }
}
