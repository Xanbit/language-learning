package com.xanbit.education.language.controller;

import com.xanbit.education.language.exception.PDFGenerationException;
import com.xanbit.education.language.extraction.PDFExtractor;
import com.xanbit.education.language.model.ExtractedPage;
import com.xanbit.education.language.swedish.dictionary.xml.model.Word;
import com.xanbit.education.language.swedish.lookup.WordLookupService;
import com.xanbit.education.language.writer.PDFGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class EbookTranslationController {

    @Autowired
    private PDFExtractor pdfExtractor;

    @Autowired
    private WordLookupService wordLookupService;

    @Autowired
    private PDFGenerator pdfGenerator;

    private static final String SAMPLE_PDF_PATH = "ebooks/vision-for-sverige-2025.pdf";
    private static final String USER_INPUT_TEMP_SAVE_FILE = "/Users/markiv/user-input.txt";
    private static final String OUTPUT_DIR = "/Users/markiv/";

    @RequestMapping("/scanbook/{ebook}")
    public String lookupWordsFromEbook(@PathVariable String ebook) throws IOException, PDFGenerationException {

        System.out.println("Reached Here : "+ebook);

        int pageNumber = 1;

        System.out.println("Request for ebook words lookup. Ebook : "+ebook+" , pageNumber : "+pageNumber);

        List<ExtractedPage> extractedPages = pdfExtractor.extractWords(SAMPLE_PDF_PATH);

        System.out.println("Pages Extracted. Total Pages : "+extractedPages.size());

        ExtractedPage page = extractedPages.get(pageNumber);

        Set<String> userWords = getUserInput(page);

        List<Word> lookedUpWords = new ArrayList<>();

        Set<String> missingWordsFromDictionary = new HashSet<>();

        wordLookupService.lookupWords(userWords, lookedUpWords, missingWordsFromDictionary);

        String outputFile = OUTPUT_DIR+ebook+"-"+pageNumber+".pdf";

        pdfGenerator.generate(lookedUpWords, missingWordsFromDictionary, outputFile);

        return "Request completed : Generated PDF is kept at : "+outputFile;
    }

    private Set<String> getUserInput(ExtractedPage page) throws IOException {

        Set<String> userWords = new HashSet<>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please provide input to filter words. Once a word is flashed, press 'n' if you don't know the word.");
        System.out.println("Else press enter/return. Press 'q' at any time to finish the input process.");

        for (String w : page.getAllWords()) {
            System.out.println("Word :- "+w);
            String input = bufferedReader.readLine();
            if (input.equalsIgnoreCase("n")){
                userWords.add(w);
            } else if (input.equalsIgnoreCase("q")){
                bufferedReader.close();
                System.out.println("User input process stopped by user, total words filtered : "+userWords.size());
                return userWords;
            }
        }

        bufferedReader.close();
        System.out.println("User input process completed, total words filtered : "+userWords.size());
        return userWords;
    }

    private Set<String> getUserInput(Set<String> splittedWords) throws IOException {
        Set<String> notKnownWords = new HashSet<String>();

        System.out.println("Press n if you don't know the word, followed by enter or else just enter !!");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for (String string : splittedWords) {
            System.out.println(string);
            String userInput = reader.readLine();
            if (userInput.equalsIgnoreCase("n")) {
                notKnownWords.add(string);
                saveUserInput(notKnownWords);
            }else if (userInput.equalsIgnoreCase("q")){
                return notKnownWords;
            }
        }
        reader.close();
        return notKnownWords;
    }

    private void saveUserInput(Set<String> notKnownWords) throws IOException {
        FileOutputStream fos = new FileOutputStream(USER_INPUT_TEMP_SAVE_FILE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(notKnownWords);
        oos.close();
    }

    private List<String> readUserInput() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("t.tmp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<String> res = (List<String>) ois.readObject();
        ois.close();
        return res;
    }

}
