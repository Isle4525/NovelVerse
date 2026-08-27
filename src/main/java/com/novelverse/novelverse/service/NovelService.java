package com.novelverse.novelverse.service;


import com.novelverse.novelverse.domain.Novel;
import com.novelverse.novelverse.repository.NovelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NovelService {

    private final NovelRepository novelRepository;
    public NovelService(NovelRepository novelRepository) {this.novelRepository = novelRepository;}


    public void createNovel(String name, String description, String coverUrl){
        Novel novel = new Novel();
        novel.setTitle(name);
        novel.setDescription(description);
        novel.setCoverUrl(coverUrl);
        novelRepository.save(novel);
    }

    public List<Novel> getAllNovels(){
        return novelRepository.findAll();
    }

    public Novel getNovelById(long id) {
        return novelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Novel not found"));
    }

    public void updateNovel(long id, String title, String description, String coverUrl) {
        Novel novel = getNovelById(id);
        novel.setTitle(title);
        novel.setDescription(description);
        novel.setCoverUrl(coverUrl);
        novelRepository.update(novel);
    }

    public void deleteNovel(long id) {
        getNovelById(id);
        novelRepository.delete(id);
    }
}
