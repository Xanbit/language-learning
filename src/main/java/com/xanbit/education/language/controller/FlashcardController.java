package com.xanbit.education.language.controller;

import com.xanbit.education.language.exception.PDFGenerationException;
import com.xanbit.education.language.model.ExtractedPage;
import com.xanbit.education.language.service.archive.IArchiveService;
import com.xanbit.education.language.writer.PDFGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/flashcards")
public class FlashcardController {

    @Autowired
    IArchiveService archiveService;

    @Autowired
    PDFGenerator pdfGenerator;

    private static final String OUTPUT_DIR = "generated/flashcards";

    private static final List<String> wordstoBeFiltered = Arrays.asList("div", "img", "cover", "jpg", "images", "src", "alt", "height");

    @RequestMapping(value = "/document", method = RequestMethod.GET)
    public HttpEntity<byte[]> printFlashcards(
            @RequestParam(value="fileName", required=true) String documentUUID,
            @RequestParam(value="user", required=true) String user) throws IOException, PDFGenerationException {

        Set<String> userKnownWords = archiveService.getUserWordBank(user);

        Map<Integer, Set<String>> wordsByPage = archiveService.getDocumentWords(documentUUID);

        //remove user known words
        wordsByPage.entrySet().forEach(e -> e.getValue().removeAll(userKnownWords));

        //remove unwanted words
        wordsByPage.entrySet().forEach(e -> e.getValue().removeAll(wordstoBeFiltered));

        //generate pdf
        createDirectory(OUTPUT_DIR+"/"+user);

        String outputFile = OUTPUT_DIR+"/"+user+"/"+documentUUID+".pdf";

        pdfGenerator.generateFlashcards(wordsByPage, outputFile);

        File generatedPDF = new File(outputFile);

        byte[] generatedPDFArray = new byte[(int)generatedPDF.length()];

        new FileInputStream(generatedPDF).read(generatedPDFArray);

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.IMAGE_JPEG);

        //all words
        System.out.println(wordsByPage.values().stream().flatMap(s -> s.stream()).collect(Collectors.toSet()));

        return new ResponseEntity<byte[]>(generatedPDFArray, httpHeaders, HttpStatus.OK);
    }

    private void createDirectory(String path) {
        File file = new File(path);
        file.mkdirs();
    }
}
