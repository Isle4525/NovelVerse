package com.novelverse.novelverse.repository;

import com.novelverse.novelverse.domain.Comment;

import java.util.List;

public interface CommentRepository {
    void save(Comment comment);
    List<Comment> findByNovelId(Long novelId);
}
