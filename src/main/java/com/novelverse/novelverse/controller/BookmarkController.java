package com.novelverse.novelverse.controller;

import com.novelverse.novelverse.domain.Bookmark;
import com.novelverse.novelverse.dto.bookmark.CreateBookmarkDTO;
import com.novelverse.novelverse.repository.BookmarkRepository;
import com.novelverse.novelverse.service.BookmarkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public  BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping
    public void add(@RequestBody CreateBookmarkDTO createBookmarkDTO){
        bookmarkService.add(createBookmarkDTO.userId, createBookmarkDTO.chapterId);
    }

    @DeleteMapping
    public void delete(@RequestBody CreateBookmarkDTO createBookmarkDTO){
        bookmarkService.remove(createBookmarkDTO.userId, createBookmarkDTO.chapterId);
    }

    @GetMapping("/{userId}")
    public List<Bookmark> get(@PathVariable  Long userId){
        return bookmarkService.getUserBookmarks(userId);
    }

}
