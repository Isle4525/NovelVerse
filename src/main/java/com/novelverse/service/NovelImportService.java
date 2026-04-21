package com.novelverse.service;

import com.novelverse.model.Chapter;
import com.novelverse.model.Novel;
import com.novelverse.repository.ChapterRepository;
import com.novelverse.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class NovelImportService {

    private static final Pattern CHAPTER_PATTERN = Pattern.compile("(?im)^(?:chapter|глава|том)\\s*(\\d+)?[\\s\\-:]*([^\\r\\n]*)$");

    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;
    private final NotificationService notificationService;

    @Transactional
    public Novel importFromTxt(Novel novel, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Выберите TXT-файл для импорта");
        }

        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (!fileName.endsWith(".txt")) {
            throw new RuntimeException("Поддерживается только формат .txt");
        }

        String text = readText(file);
        if (text.isBlank()) {
            throw new RuntimeException("Файл пустой");
        }

        LocalDateTime now = LocalDateTime.now();
        novel.setCreatedAt(now);
        novel.setUpdatedAt(now);
        if (novel.getViews() == null) {
            novel.setViews(0L);
        }
        if (novel.getBookmarksCount() == null) {
            novel.setBookmarksCount(0L);
        }
        if (novel.getDescription() == null || novel.getDescription().isBlank()) {
            novel.setDescription(extractDescription(text));
        }

        Novel savedNovel = novelRepository.save(novel);
        List<Chapter> chapters = parseChapters(savedNovel, text, now);
        chapterRepository.saveAll(chapters);

        savedNovel.setChaptersCount(chapters.size());
        if ((savedNovel.getContent() == null || savedNovel.getContent().isBlank()) && !chapters.isEmpty()) {
            savedNovel.setContent(chapters.get(0).getContent());
        }
        Novel result = novelRepository.save(savedNovel);
        notificationService.notifyNovelPublished(result);
        return result;
    }

    private String readText(MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8).replace("\uFEFF", "").trim();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать TXT-файл", e);
        }
    }

    private List<Chapter> parseChapters(Novel novel, String text, LocalDateTime createdAt) {
        Matcher matcher = CHAPTER_PATTERN.matcher(text);
        List<ChapterBoundary> boundaries = new ArrayList<>();

        while (matcher.find()) {
            String rawTitle = matcher.group(0).trim();
            String numberGroup = matcher.group(1);
            Integer number = numberGroup != null && !numberGroup.isBlank()
                    ? Integer.parseInt(numberGroup)
                    : boundaries.size() + 1;
            boundaries.add(new ChapterBoundary(number, rawTitle, matcher.start(), matcher.end()));
        }

        List<Chapter> chapters = new ArrayList<>();
        if (boundaries.isEmpty()) {
            chapters.add(Chapter.builder()
                    .novel(novel)
                    .number(1)
                    .title("Глава 1")
                    .content(text)
                    .createdAt(createdAt)
                    .build());
            return chapters;
        }

        for (int i = 0; i < boundaries.size(); i++) {
            ChapterBoundary current = boundaries.get(i);
            int contentStart = current.end;
            int contentEnd = i + 1 < boundaries.size() ? boundaries.get(i + 1).start : text.length();
            String content = text.substring(contentStart, contentEnd).trim();
            if (content.isBlank()) {
                continue;
            }

            chapters.add(Chapter.builder()
                    .novel(novel)
                    .number(current.number)
                    .title(current.title)
                    .content(content)
                    .createdAt(createdAt)
                    .build());
        }

        if (chapters.isEmpty()) {
            chapters.add(Chapter.builder()
                    .novel(novel)
                    .number(1)
                    .title("Глава 1")
                    .content(text)
                    .createdAt(createdAt)
                    .build());
        }
        return chapters;
    }

    private String extractDescription(String text) {
        String[] parts = text.split("\\R\\R+");
        for (String part : parts) {
            String cleaned = part.trim();
            if (!cleaned.isBlank() && cleaned.length() > 40) {
                return cleaned.length() > 280 ? cleaned.substring(0, 280) + "..." : cleaned;
            }
        }
        return text.length() > 280 ? text.substring(0, 280) + "..." : text;
    }

    private record ChapterBoundary(Integer number, String title, int start, int end) { }
}
