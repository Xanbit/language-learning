package com.xanbit.education.language.extraction;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.xanbit.education.language.model.ExtractedPage;
import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.service.archive.ebook.ArchiveService;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EpubExtractor {

	@Autowired
	private ArchiveService archiveService;

    static final List<String> xhtmlTags = Arrays.asList("xml", "utf", "standalone", "encoding", "version",
            "doctype", "public", "dtd", "en", "html", "xhtml", "org", "www", "tr", "http","xmlns",
            "head", "title", "body", "StartFragment", "heading", "id");

	public List<ExtractedPage> extractWords(String path) throws IOException{

        List<ExtractedPage> extractedPages = new ArrayList<>();

        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(this.getClass().getClassLoader().getResourceAsStream(path));

        for(int i=0;i<book.getContents().size();i++) {
            BufferedReader br = new BufferedReader(book.getContents().get(i).getReader());

            Set<String> distinctWords = new HashSet<>();

            br.lines().forEach(l -> distinctWords.addAll(findDistinctWordsInText(l)));
            extractedPages.add(new ExtractedPage(path, path, "", distinctWords, i+1));
        }
        return extractedPages;
	}

    public List<ExtractedPage> extractWordsFromStoredDocument(String uuid) throws IOException{

        Document doc =  archiveService.getDocumentDao().load(uuid);

        EpubReader epubReader = new EpubReader();

        Book book = epubReader.readEpub(new ByteArrayInputStream(doc.getFileData()));

        List<ExtractedPage> allPages = new ArrayList<>();

        for (int i = 0; i< book.getContents().size(); i++){

            BufferedReader br = new BufferedReader(book.getContents().get(i).getReader());

            Set<String> distinctWords = new HashSet<>();

            br.lines().forEach(l -> distinctWords.addAll(findDistinctWordsInText(l)));

            ExtractedPage page = new ExtractedPage(doc.getUuid(), doc.getFileName(), "", distinctWords, i);

            allPages.add(page);
        }

        return allPages;
    }

    public ExtractedPage extractWordsFromStoredDocument(String uuid, int pageNumber) throws IOException{

        Document doc =  archiveService.getDocumentDao().load(uuid);

        EpubReader epubReader = new EpubReader();

        Book book = epubReader.readEpub(new ByteArrayInputStream(doc.getFileData()));

        if (pageNumber > book.getContents().size())
            return new ExtractedPage(doc.getUuid(), doc.getFileName(), "", Collections.emptySet(), pageNumber);

        BufferedReader br = new BufferedReader(book.getContents().get(pageNumber).getReader());

        Set<String> distinctWords = new HashSet<>();

        br.lines().forEach(l -> distinctWords.addAll(findDistinctWordsInText(l)));

        return new ExtractedPage(doc.getUuid(), doc.getFileName(), "", distinctWords, pageNumber);
    }

	public static Set<String> findDistinctWordsInText(String text) {

		String[] words = text
				.toLowerCase()
				.replaceAll("[^a-zA-ZåäöÅÄÖ]", " ")
                .replace("\n", " ")
                .split(" ");

		//filter words that contains numbers

		Set<String> filteredWords = Arrays.asList(words)
				.stream()
				.filter(word -> ! word.matches("\\w*\\d\\w*"))
				.collect(Collectors.toSet());

		return filteredWords.stream()
                .filter(word -> !word.isEmpty() && !xhtmlTags.contains(word) && word.length() > 2)
                .collect(Collectors.toSet());
	}
	
}
