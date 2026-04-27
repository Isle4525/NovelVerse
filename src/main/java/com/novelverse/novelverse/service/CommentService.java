package com.novelverse.novelverse.service;

import com.novelverse.novelverse.domain.Comment;
import com.novelverse.novelverse.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void addComment(Long userId, Long chapterId, String text) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setNovelId(chapterId);
        comment.setText(text);

        commentRepository.save(comment);
    }


    public List<Comment> getByNovel(Long novelId) {
        return commentRepository.findByNovelId(novelId);
    }


}
