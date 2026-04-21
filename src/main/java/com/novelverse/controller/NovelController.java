package com.novelverse.controller;

import com.novelverse.model.Chapter;
import com.novelverse.model.Novel;
import com.novelverse.repository.ChapterRepository;
import com.novelverse.repository.NovelRepository;
import com.novelverse.service.NovelImportService;
import com.novelverse.service.NovelService;
import com.novelverse.service.RanobeLibImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/novels")
@Tag(name = "Novels", description = "API for reading novels")
public class NovelController {

    @Autowired private NovelRepository novelRepository;
    @Autowired private ChapterRepository chapterRepository;
    @Autowired private NovelService novelService;
    @Autowired private NovelImportService novelImportService;
    @Autowired private RanobeLibImportService ranobeLibImportService;

    @GetMapping
    @Operation(summary = "Get all novels")
    public List<Novel> getAllNovels() {
        return novelRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get novel by ID")
    public ResponseEntity<?> getNovelById(@PathVariable Long id) {
        return novelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/chapters")
    @Operation(summary = "Get chapters list for novel")
    public ResponseEntity<?> getChapters(
            @PathVariable Long id,
            @RequestParam(defaultValue = "asc") String order) {
        if (!novelRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<Chapter> chapters = order.equals("desc")
                ? chapterRepository.findByNovelIdOrderByNumberDesc(id)
                : chapterRepository.findByNovelIdOrderByNumberAsc(id);

        return ResponseEntity.ok(chapters.stream().map(c -> Map.of(
                "id", c.getId(),
                "number", c.getNumber(),
                "title", c.getTitle() != null ? c.getTitle() : "Том " + c.getNumber(),
                "createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : ""
        )).toList());
    }

    @GetMapping("/{id}/chapters/{number}")
    @Operation(summary = "Get chapter content")
    public ResponseEntity<?> getChapter(
            @PathVariable Long id,
            @PathVariable Integer number) {
        return chapterRepository.findByNovelIdAndNumber(id, number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/similar")
    @Operation(summary = "Get similar novels")
    public ResponseEntity<?> getSimilar(@PathVariable Long id) {
        return novelRepository.findById(id).map(novel -> {
            List<Novel> similar = novelRepository.findAll().stream()
                    .filter(n -> !n.getId().equals(id) && novel.getGenre() != null
                            && novel.getGenre().equals(n.getGenre()))
                    .limit(4)
                    .toList();
            return ResponseEntity.ok(similar);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Add a new novel")
    public Novel addNovel(@RequestBody Novel novel) {
        return novelService.createNovel(novel);
    }

    @PostMapping("/import-text")
    @Operation(summary = "Import a novel from TXT")
    public Novel importNovelFromText(
            @RequestParam String title,
            @RequestParam(required = false) String titleOriginal,
            @RequestParam String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) String coverEmoji,
            @RequestParam(required = false) String coverColor,
            @RequestParam(required = false) String description,
            @RequestParam("file") MultipartFile file) {
        Novel novel = Novel.builder()
                .title(title)
                .titleOriginal(titleOriginal)
                .author(author)
                .genre(genre)
                .status(status)
                .year(year)
                .rating(rating)
                .coverEmoji(coverEmoji)
                .coverColor(coverColor)
                .description(description)
                .build();
        return novelImportService.importFromTxt(novel, file);
    }

    @GetMapping("/preview-ranobelib")
    @Operation(summary = "Preview a novel from RanobeLIB URL")
    public Map<String, Object> previewRanobeLib(@RequestParam String url) {
        return ranobeLibImportService.previewByUrl(url);
    }

    @PostMapping("/import-ranobelib")
    @Operation(summary = "Import a novel from RanobeLIB URL")
    public Novel importFromRanobeLib(
            @RequestParam String url,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String titleOriginal,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) String coverEmoji,
            @RequestParam(required = false) String coverColor,
            @RequestParam(required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        return ranobeLibImportService.importByUrl(
                url,
                title,
                titleOriginal,
                author,
                genre,
                status,
                year,
                rating,
                coverEmoji,
                coverColor,
                description,
                file
        );
    }

    @PostMapping("/{id}/chapters")
    @Operation(summary = "Add chapter to novel")
    public ResponseEntity<?> addChapter(@PathVariable Long id, @RequestBody Chapter chapter) {
        return novelRepository.findById(id).map(novel -> {
            chapter.setNovel(novel);
            if (chapter.getCreatedAt() == null) {
                chapter.setCreatedAt(java.time.LocalDateTime.now());
            }
            return ResponseEntity.ok(chapterRepository.save(chapter));
        }).orElse(ResponseEntity.notFound().build());
    }
}
