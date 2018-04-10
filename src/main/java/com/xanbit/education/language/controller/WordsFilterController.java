package com.xanbit.education.language.controller;

import com.xanbit.education.language.exception.PDFGenerationException;
import com.xanbit.education.language.extraction.PDFExtractor;
import com.xanbit.education.language.model.ExtractedPage;
import com.xanbit.education.language.model.RestFilterWord;
import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.model.archive.DocumentMetadata;
import com.xanbit.education.language.service.archive.IArchiveService;
import com.xanbit.education.language.service.archive.ebook.ArchiveService;
import com.xanbit.education.language.swedish.dictionary.xml.model.Word;
import com.xanbit.education.language.swedish.lookup.WordLookupService;
import com.xanbit.education.language.writer.PDFGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    IArchiveService archiveService;

    // TODO: 2017-12-23 scope limited to single threaded usage
    private String CURRENT_USER;
    private LinkedList<ExtractedPage> pagesBeingProcessed = new LinkedList<>();
    private Set<String> userWordBank ;
    private static final String OUTPUT_DIR = "generated";

    @RequestMapping(value = "/startFiltering", method = RequestMethod.GET)
    public HttpEntity<ExtractedPage> startFiltering(
            @RequestParam(value="fileName", required=true) String documentUUID,
            @RequestParam(value="user", required=true) String user) throws IOException {

        CURRENT_USER = user;

        System.out.println(documentUUID);


        //save extracted words
        Map<Integer, Set<String>> allWords = pdfExtractor.extractWordsFromStoredDocument(documentUUID)
                .stream().collect(Collectors.toMap(page -> page.getPageNumber(), page -> page.getAllWords()));

        saveDocumentWords(documentUUID, allWords);

        ExtractedPage page = pdfExtractor.extractWordsFromStoredDocument(documentUUID, lastProcessedPage(documentUUID, CURRENT_USER));

        userWordBank = getUserWordBank(user);

        page.setAllWords(page.getAllWords().stream().filter(word -> ! userWordBank.contains(word)).collect(Collectors.toSet()));

        pagesBeingProcessed.add(page);

        ExtractedPage wrapperPage;

        if (page.getAllWords().isEmpty()){
            wrapperPage = new ExtractedPage(page.getFileUUID(), page.getFileName(), page.getRawText(), page.getAllWords(), page.getPageNumber());
            wrapperPage.getAllWords().add("No Words in Page; Go To Next");
        }else {
            wrapperPage = page;
        }

        return new ResponseEntity<ExtractedPage>(wrapperPage, new HttpHeaders(),HttpStatus.OK);
    }

    @RequestMapping(value = "/filterOut", method = RequestMethod.GET)
    public HttpEntity<ExtractedPage> filterOut(
            @RequestParam(value="fileName", required=true) String documentUUID,
            @RequestParam(value="pageNumber", required=true) Integer pageNumber,
            @RequestParam(value="word", required=true) String word) throws IOException {

        pagesBeingProcessed.getLast().getAllWords().remove(word);

        addToUserWordBank(word);

        return new ResponseEntity<ExtractedPage>(pagesBeingProcessed.getLast(), new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/nextPage", method = RequestMethod.GET)
    public HttpEntity<ExtractedPage> nextPage(
            @RequestParam(value="fileName", required=true) String documentUUID,
            @RequestParam(value="pageNumber", required=true) Integer pageNumber) throws IOException {

        ExtractedPage nextPage = pdfExtractor.extractWordsFromStoredDocument(documentUUID, pagesBeingProcessed.getLast().getPageNumber()+1);

        pagesBeingProcessed.add(nextPage);

        nextPage.setAllWords(nextPage.getAllWords().stream().filter(word -> ! userWordBank.contains(word)).collect(Collectors.toSet()));

        ExtractedPage wrapperPage;

        if (nextPage.getAllWords().isEmpty()){
            wrapperPage = new ExtractedPage(nextPage.getFileUUID(), nextPage.getFileName(), nextPage.getRawText(), nextPage.getAllWords(), nextPage.getPageNumber());
            wrapperPage.getAllWords().add("No Words in Page; Go To Next");
        }else {
            wrapperPage = nextPage;
        }

        return new ResponseEntity<ExtractedPage>(wrapperPage, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/finishFiltering", method = RequestMethod.GET)
    public HttpEntity<byte[]> finishFiltering() throws IOException, PDFGenerationException {

        List<Word> lookedUpWords = new ArrayList<>();

        Set<String> missingWordsFromDictionary = new HashSet<>();

        wordLookupService.lookupWords(collectWordsToLookup(), lookedUpWords, missingWordsFromDictionary);

        createDirectory(OUTPUT_DIR);

        String outputFile = OUTPUT_DIR+"/"+pagesBeingProcessed.getLast().getFileName()+"-"+pagesBeingProcessed.getLast().getPageNumber()+".pdf";

        pdfGenerator.generate(lookedUpWords, missingWordsFromDictionary, outputFile);

        File generatedPDF = new File(outputFile);

        byte[] generatedPDFArray = new byte[(int)generatedPDF.length()];

        new FileInputStream(generatedPDF).read(generatedPDFArray);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<byte[]>(generatedPDFArray, httpHeaders, HttpStatus.OK);

    }

    private Set<String> collectWordsToLookup() {
        Set<String> ws = new HashSet<>();
        pagesBeingProcessed.stream().forEach(page -> ws.addAll(page.getAllWords()));
        return ws;
    }

    private int lastProcessedPage(String documentUUID, String user) {
        // TODO: 2017-12-22 implement this
        return 1;
    }


    private void addToUserWordBank(String word) {
        userWordBank.add(word);
        //save user word bank
        archiveService.saveUserWordBank(CURRENT_USER, userWordBank);
    }

    private Set<String> getUserWordBank(String user) {
        return archiveService.getUserWordBank(CURRENT_USER);
    }

    private void saveDocumentWords(String documentUUID, Map<Integer, Set<String>> allWords) {
        archiveService.saveDocumentWords(documentUUID, allWords);
    }

    private void createDirectory(String path) {
        File file = new File(path);
        file.mkdirs();
    }

}