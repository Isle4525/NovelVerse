package com.novelverse.novelverse.controller;

import com.novelverse.novelverse.domain.Comment;
import com.novelverse.novelverse.dto.comment.CreateCommentDTO;
import com.novelverse.novelverse.repository.CommentRepository;
import com.novelverse.novelverse.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commnent")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public void add(@RequestBody CreateCommentDTO createCommentDTO) {
        commentService.addComment(createCommentDTO.userId, createCommentDTO.novelId, createCommentDTO.text);
    }

    @GetMapping("/novel/{id}")
    public List<Comment> getByNovel(@PathVariable Long id){
        return commentService.getByNovel(id);
    }
}
