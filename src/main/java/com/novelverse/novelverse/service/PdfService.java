package com.novelverse.novelverse.service;

import com.novelverse.novelverse.domain.Chapter;
import com.novelverse.novelverse.domain.Novel;
import com.novelverse.novelverse.repository.ChapterRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class PdfService {

    private final ExecutorService executorService;
    private final ChapterRepository chapterRepository;
    private final ChapterService chapterService;
    private final NovelService novelService;

    public PdfService(
            ExecutorService executorService,
            ChapterRepository chapterRepository,
            ChapterService chapterService,
            NovelService novelService
    ) {
        this.executorService = executorService;
        this.chapterRepository = chapterRepository;
        this.chapterService = chapterService;
        this.novelService = novelService;
    }

    public void parseAsync(File file, Long novelId) {
        executorService.submit(() -> parse(file, novelId));
    }

    private void parse(File file, Long novelId) {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            String[] chapters = text.split("(?i)(?:Глава|Chapter)\\s+\\d+");
            int index = 1;

            for (String chText : chapters) {
                if (chText.isBlank()) {
                    continue;
                }

                Chapter chapter = new Chapter();
                chapter.setNovelId(novelId);
                chapter.setTitle("Глава " + index++);
                chapter.setContent(chText.trim());

                chapterRepository.save(chapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File exportChapterToPdf(long chapterId) throws IOException {
        Chapter chapter = chapterService.getChapter(chapterId);
        Novel novel = novelService.getNovelById(chapter.getNovelId());

        Path tempPath = Files.createTempFile("chapter-" + chapterId + "-", ".pdf");
        File pdfFile = tempPath.toFile();
        pdfFile.deleteOnExit();

        try (PDDocument document = new PDDocument()) {
            List<String> lines = buildChapterLines(chapter, novel);
            writeLinesToDocument(document, lines, loadPdfFont(document));
            document.save(pdfFile);
        }

        return pdfFile;
    }

    private List<String> buildChapterLines(Chapter chapter, Novel novel) throws IOException {
        List<String> lines = new ArrayList<>();

        lines.add(safeText(chapter.getTitle(), "Chapter"));
        lines.add("");
        lines.add("Novel: " + safeText(novel.getTitle(), "Untitled"));
        lines.add("Chapter ID: " + chapter.getId());

        if (chapter.getImageUrl() != null && !chapter.getImageUrl().isBlank()) {
            lines.add("Image URL: " + chapter.getImageUrl());
        }

        lines.add("");
        lines.addAll(wrapParagraphs(safeText(chapter.getContent(), "[Empty chapter text]"), 495, 12f));
        return lines;
    }

    private void writeLinesToDocument(PDDocument document, List<String> lines, PDFont font) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        float margin = 50f;
        float y = page.getMediaBox().getHeight() - margin;
        float leading = 16f;
        int lineIndex = 0;

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, y);

        for (String line : lines) {
            if (y <= margin + leading) {
                contentStream.endText();
                contentStream.close();

                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                y = page.getMediaBox().getHeight() - margin;

                contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, y);
            }

            if (lineIndex == 0) {
                contentStream.setFont(font, 18);
            } else {
                contentStream.setFont(font, 12);
            }

            contentStream.showText(sanitizePdfText(line));
            contentStream.newLineAtOffset(0, -leading);
            y -= leading;
            lineIndex++;
        }

        contentStream.endText();
        contentStream.close();
    }

    private List<String> wrapParagraphs(String text, float width, float fontSize) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] paragraphs = text.replace("\r", "").split("\n");

        for (String paragraph : paragraphs) {
            if (paragraph.isBlank()) {
                lines.add("");
                continue;
            }

            StringBuilder currentLine = new StringBuilder();
            for (String word : paragraph.split("\\s+")) {
                String candidate = currentLine.length() == 0 ? word : currentLine + " " + word;
                float candidateWidth = estimateTextWidth(candidate, fontSize);

                if (candidateWidth > width && currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(candidate);
                }
            }

            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }

        return lines;
    }

    private String sanitizePdfText(String value) {
        return safeText(value, "").replace("\t", "    ");
    }

    private PDFont loadPdfFont(PDDocument document) {
        String[] candidatePaths = {
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/calibri.ttf",
            "C:/Windows/Fonts/segoeui.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/dejavu-sans-fonts/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf",
            "/usr/share/fonts/liberation-sans-fonts/LiberationSans-Regular.ttf",
            "/usr/share/fonts/TTF/DejaVuSans.ttf",
            "/usr/share/fonts/adwaita-sans-fonts/AdwaitaSans-Regular.ttf",
            "/usr/share/fonts/cantarell-fonts/Cantarell-Regular.otf",
            "/System/Library/Fonts/Helvetica.ttc",
            "/Library/Fonts/Arial.ttf"
        };

        for (String path : candidatePaths) {
            File fontFile = new File(path);
            if (fontFile.exists() && fontFile.canRead()) {
                try {
                    return PDType0Font.load(document, fontFile);
                } catch (Exception ignored) {
                }
            }
        }

        return org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
    }

    private float estimateTextWidth(String text, float fontSize) {
        return text.length() * fontSize * 0.5f;
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
