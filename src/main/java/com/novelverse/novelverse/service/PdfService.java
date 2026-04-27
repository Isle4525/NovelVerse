package com.novelverse.novelverse.service;

import com.novelverse.novelverse.domain.Chapter;
import com.novelverse.novelverse.repository.ChapterRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PdfService {

    private final ExecutorService executorService;
    private final ChapterRepository chapterRepository;

    public  PdfService(ExecutorService executorService,ChapterRepository chapterRepository){
        this.executorService = executorService;
        this.chapterRepository = chapterRepository;
    }

    public void parseAsync(File file, Long novelId){
        executorService.submit(()->{
            parse(file, novelId);
        });
    }

    private void parse(File file, Long novelId){

        try {
            PDDocument document = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(document);

            document.close();

            String[] chapters = text.split("Глава \\d+");

            int index = 0;

            for (String chText:  chapters){
                if (chText.isBlank()) continue;

                Chapter chapter = new Chapter();
                chapter.setNovelId(novelId);
                chapter.setTitle("Глава " + index++);
                chapter.setContent(chText.trim());

                chapterRepository.save(chapter);
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
