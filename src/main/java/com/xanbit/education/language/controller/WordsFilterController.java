package com.xanbit.education.language.controller;

import com.xanbit.education.language.extraction.PDFExtractor;
import com.xanbit.education.language.model.ExtractedPage;
import com.xanbit.education.language.model.RestFilterWord;
import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.model.archive.DocumentMetadata;
import com.xanbit.education.language.service.archive.IArchiveService;
import com.xanbit.education.language.service.archive.ebook.ArchiveService;
import com.xanbit.education.language.swedish.lookup.WordLookupService;
import com.xanbit.education.language.writer.PDFGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/words")
public class WordsFilterController {

    private static final Logger LOG = Logger.getLogger(WordsFilterController.class);

    @Autowired
    private PDFExtractor pdfExtractor;

    @Autowired
    private WordLookupService wordLookupService;

    @Autowired
    private PDFGenerator pdfGenerator;

    @RequestMapping(value = "/startFiltering", method = RequestMethod.GET)
    public HttpEntity<ExtractedPage> startFiltering(
            @RequestParam(value="fileName", required=true) String documentUUID,
            @RequestParam(value="user", required=true) String user) throws IOException {

        ExtractedPage page = pdfExtractor.extractWordsFromStoredDocument(documentUUID, lastProcessedPage(documentUUID, user)+1);

        return new ResponseEntity<ExtractedPage>(page, new HttpHeaders(),HttpStatus.OK);
    }

    @RequestMapping(value = "/filterOut", method = RequestMethod.GET)
    public HttpEntity<ExtractedPage> filterOut(
            @RequestParam(value="fileName", required=true) String documentUUID,
            @RequestParam(value="pageNumber", required=true) Integer pageNumber,
            @RequestParam(value="word", required=true) String word) throws IOException {


        ExtractedPage page = pdfExtractor.extractWordsFromStoredDocument(documentUUID, pageNumber);
        page.getAllWords().remove(word);

        System.out.println("Word filtered : "+word);
        return new ResponseEntity<ExtractedPage>(page, new HttpHeaders(), HttpStatus.OK);
    }

    private int lastProcessedPage(String documentUUID, String user) {
        // TODO: 2017-12-22 implement this
        return 0;
    }

}