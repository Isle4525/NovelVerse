package com.novelverse.novelverse.service;


import com.novelverse.novelverse.domain.Bookmark;
import com.novelverse.novelverse.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    public void add(Long user_id, Long chapter_id){
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(user_id);
        bookmark.setChapterId(chapter_id);
        bookmarkRepository.save(bookmark);
    }

    public void remove(Long user_id, Long chapter_id){
        bookmarkRepository.delete(user_id, chapter_id);
    }

    public List<Bookmark> getUserBookmarks(Long user_id){
        return bookmarkRepository.findByUserId(user_id);
    }
}
