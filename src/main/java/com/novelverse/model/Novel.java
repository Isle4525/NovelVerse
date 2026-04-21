package com.novelverse.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "novels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String titleOriginal;   // название на языке оригинала
    private String author;
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;         // текст первой/тестовой главы

    private String genre;
    private Double rating;
    private Integer chaptersCount;
    private String status;          // "Выпускается" / "Завершено"
    private Integer year;
    private String coverEmoji;      // 🔥 временно вместо картинки
    private String coverColor;      // "#8e44ad"
    private Long views;
    private Long bookmarksCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}