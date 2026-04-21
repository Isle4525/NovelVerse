package com.novelverse.service;

import com.novelverse.model.Novel;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RanobeLibImportService {

    private static final Pattern YEAR_PATTERN = Pattern.compile("(19|20)\\d{2}");

    private final NovelService novelService;
    private final NovelImportService novelImportService;

    public Map<String, Object> previewByUrl(String url) {
        String normalizedUrl = normalizeUrl(url);
        Document document = fetchDocument(normalizedUrl);

        String rawTitle = firstNonBlank(
                selectMeta(document, "meta[property=og:title]"),
                selectMeta(document, "meta[name=twitter:title]"),
                document.title()
        );

        if (rawTitle == null || rawTitle.isBlank()) {
            throw new RuntimeException("Не удалось определить название ранобэ");
        }

        String cleanedTitle = cleanTitle(rawTitle);
        String description = firstNonBlank(
                selectMeta(document, "meta[property=og:description]"),
                selectMeta(document, "meta[name=description]"),
                extractTextSnippet(document),
                "Импортировано с RanobeLIB"
        );

        Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("title", cleanedTitle);
        preview.put("titleOriginal", extractSlug(normalizedUrl));
        preview.put("author", guessAuthor(document));
        preview.put("description", description);
        preview.put("genre", guessGenre(document));
        preview.put("status", guessStatus(document));
        preview.put("coverEmoji", "🔗");
        preview.put("coverColor", "#4f46e5");
        preview.put("rating", 0.0);
        preview.put("year", guessYear(document));
        preview.put("sourceUrl", normalizedUrl);
        return preview;
    }

    public Novel importByUrl(
            String url,
            String title,
            String titleOriginal,
            String author,
            String genre,
            String status,
            Integer year,
            Double rating,
            String coverEmoji,
            String coverColor,
            String description,
            MultipartFile file) {

        Map<String, Object> preview = previewByUrl(url);
        String normalizedUrl = (String) preview.get("sourceUrl");

        Novel novel = Novel.builder()
                .title(prefer(title, (String) preview.get("title")))
                .titleOriginal(prefer(titleOriginal, (String) preview.get("titleOriginal")))
                .author(prefer(author, (String) preview.get("author")))
                .description(prefer(description, (String) preview.get("description")))
                .content("Источник: " + normalizedUrl)
                .genre(prefer(genre, (String) preview.get("genre")))
                .status(prefer(status, (String) preview.get("status")))
                .year(year != null ? year : (Integer) preview.get("year"))
                .rating(rating != null ? rating : (Double) preview.get("rating"))
                .coverEmoji(prefer(coverEmoji, (String) preview.get("coverEmoji")))
                .coverColor(prefer(coverColor, (String) preview.get("coverColor")))
                .chaptersCount(0)
                .views(0L)
                .bookmarksCount(0L)
                .createdAt(LocalDateTime.now())
                .build();

        if (file != null && !file.isEmpty()) {
            return novelImportService.importFromTxt(novel, file);
        }
        return novelService.createNovel(novel);
    }

    private String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new RuntimeException("Вставьте ссылку на RanobeLIB");
        }
        try {
            URI uri = URI.create(url.trim());
            String host = uri.getHost();
            if (host == null || !(host.contains("ranobelib.me") || host.contains("dev-front.ranobelib.me"))) {
                throw new RuntimeException("Поддерживаются только ссылки RanobeLIB");
            }
            return uri.toString();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Некорректная ссылка");
        }
    }

    private Document fetchDocument(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 NovelVerseBot/1.0")
                    .timeout(15000)
                    .get();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось получить страницу RanobeLIB");
        }
    }

    private String selectMeta(Document document, String cssQuery) {
        Element element = document.selectFirst(cssQuery);
        return element != null ? element.attr("content").trim() : null;
    }

    private String cleanTitle(String value) {
        return value.replace("– RanobeLIB", "")
                .replace("- RanobeLIB", "")
                .trim();
    }

    private String extractSlug(String url) {
        try {
            String path = URI.create(url).getPath();
            if (path == null || path.isBlank()) {
                return null;
            }
            String[] parts = path.split("/");
            return parts.length == 0 ? null : parts[parts.length - 1];
        } catch (Exception e) {
            return null;
        }
    }

    private String guessAuthor(Document document) {
        String text = document.text();
        Matcher matcher = Pattern.compile("(?iu)(?:автор|author)\\s*[:\\-]?\\s*([^\\|,;]{2,80})").matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "RanobeLIB import";
    }

    private String guessGenre(Document document) {
        String text = document.text().toLowerCase();
        if (text.contains("романтика")) return "Романтика";
        if (text.contains("комедия")) return "Комедия";
        if (text.contains("фэнтези") || text.contains("fantasy")) return "Фэнтези";
        if (text.contains("экшен") || text.contains("боевик") || text.contains("action")) return "Экшен";
        if (text.contains("киберпанк") || text.contains("cyberpunk")) return "Киберпанк";
        return "Импорт";
    }

    private String guessStatus(Document document) {
        String text = document.text().toLowerCase();
        if (text.contains("завершено") || text.contains("completed")) {
            return "Завершено";
        }
        return "Выпускается";
    }

    private Integer guessYear(Document document) {
        Matcher matcher = YEAR_PATTERN.matcher(document.text());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return null;
    }

    private String extractTextSnippet(Document document) {
        String text = document.text().trim();
        if (text.isBlank()) {
            return null;
        }
        return text.length() > 280 ? text.substring(0, 280) + "..." : text;
    }

    private String prefer(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary.trim();
        }
        return fallback;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
