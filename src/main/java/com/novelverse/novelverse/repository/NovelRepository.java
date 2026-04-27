package com.novelverse.novelverse.repository;

import com.novelverse.novelverse.domain.Novel;

import java.util.List;
import java.util.Optional;


public interface NovelRepository {
    List<Novel> findAll();
    Optional<Novel> findById(long id);
    void save(Novel novel);
}
