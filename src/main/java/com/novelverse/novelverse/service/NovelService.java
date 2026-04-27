package com.novelverse.novelverse.service;


import com.novelverse.novelverse.domain.Novel;
import com.novelverse.novelverse.repository.NovelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NovelService {

    private final NovelRepository novelRepository;
    public NovelService(NovelRepository novelRepository) {this.novelRepository = novelRepository;}


    public void createNovel(String name, String description){
        Novel novel = new Novel();
        novel.setTitle(name);
        novel.setDescription(description);
        novelRepository.save(novel);
    }

    public List<Novel> getAllNovels(){
        return novelRepository.findAll();
    }

}
