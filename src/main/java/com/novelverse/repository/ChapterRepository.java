package com.novelverse.repository;

import com.novelverse.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByNovelIdOrderByNumberAsc(Long novelId);
    List<Chapter> findByNovelIdOrderByNumberDesc(Long novelId);
    Optional<Chapter> findByNovelIdAndNumber(Long novelId, Integer number);
    long countByNovelId(Long novelId);
}