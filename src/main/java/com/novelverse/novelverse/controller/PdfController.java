package com.novelverse.novelverse.controller;

import com.novelverse.novelverse.service.PdfService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService){
        this.pdfService = pdfService;
    }

    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file,
                         @RequestParam Long novelId)throws IOException {

        File temp = File.createTempFile("upload", ".pdf");
        file.transferTo(temp);

        pdfService.parseAsync(temp, novelId);

    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<Resource> downloadChapterPdf(@PathVariable long chapterId) throws IOException {
        File pdfFile = pdfService.exportChapterToPdf(chapterId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"chapter-" + chapterId + ".pdf\"")
                .body(new FileSystemResource(pdfFile));
    }


}
