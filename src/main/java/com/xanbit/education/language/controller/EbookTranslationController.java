package com.xanbit.education.language.controller;

import com.itextpdf.text.DocumentException;
import com.xanbit.education.language.extraction.PDFExtractor;
import com.xanbit.education.language.model.ExtractedPage;
import com.xanbit.education.language.swedish.dictionary.WordLookup;
import com.xanbit.education.language.swedish.dictionary.pdf.PDFWriter;
import com.xanbit.education.language.swedish.dictionary.xml.model.Word;
import com.xanbit.education.language.swedish.generator.PDFGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class EbookTranslationController {

    @Autowired
    private PDFExtractor pdfExtractor;

    @Autowired
    private WordLookup wordLookup;

    @Autowired
    private PDFGenerator pdfGenerator;

    private static final String INPUT_FILE = "C:\\Users\\MKUVC6\\Documents\\GitHub\\language-learning\\src\\main\\resources\\ebooks\\vision-for-sverige-2025.pdf";
    private static final String OUTPUT_FILE = "C:\\Users\\MKUVC6\\Documents\\GitHub\\language-learning\\generated\\test.pdf";

    @RequestMapping("/ebooklookup")
    public String lookupWordsFromEbook() throws IOException, DocumentException {

        List<ExtractedPage> extractedPages = pdfExtractor.extractPDF(INPUT_FILE);

        ExtractedPage page = extractedPages.get(5);
        System.out.println("page 7 : ");
        page.getExtractedWords().stream().forEach(System.out::println);
        Set<String> userWords = getUserInput(page);

        List<Word> lookedUpWords = new ArrayList<>();

        Set<String> missingWordsFromDictionary = new HashSet<>();

        wordLookup.lookupWords(userWords, lookedUpWords, missingWordsFromDictionary);

        pdfGenerator.writePDF(OUTPUT_FILE, lookedUpWords, missingWordsFromDictionary);

        return "In Progress...";
    }

    private Set<String> getUserInput(ExtractedPage page) throws IOException {

        Set<String> userWords = new HashSet<>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        for (String w : page.getExtractedWords()) {
            System.out.println("Checkpoint 1 : " + w);
            String input = bufferedReader.readLine();
            if (input.equalsIgnoreCase("y")){
                userWords.add(w);
            }
        }

        bufferedReader.close();

        return userWords;
    }

}
