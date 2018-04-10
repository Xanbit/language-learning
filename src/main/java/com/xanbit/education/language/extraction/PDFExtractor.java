package com.xanbit.education.language.extraction;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.xanbit.education.language.model.ExtractedPage;
import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.service.archive.ebook.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PDFExtractor {

	@Autowired
	private ArchiveService archiveService;

    @Autowired
    private EpubExtractor epubExtractor;


	public List<ExtractedPage> extractWords(String pdfPath) throws IOException{

		List<ExtractedPage> extractedPages = new ArrayList<>();

		PdfReader reader = new PdfReader(getClass().getClassLoader().getResource(pdfPath).getFile());
		
		for(int i=0;i<reader.getNumberOfPages();i++) {
			String text = PdfTextExtractor.getTextFromPage(reader, (i+1));
            //String[] words = text.replaceAll("^[.,\\s]+", "").split("[.,\\s]+");
			Set<String> distinctWords = findDistinctWordsInText(text);
            extractedPages.add(new ExtractedPage(pdfPath, pdfPath, text, distinctWords, i+1));
        }
		
		return extractedPages;
	}

	public ExtractedPage extractWordsFromStoredDocument(String uuid, int pageNumber) throws IOException{

		Document doc =  archiveService.getDocumentDao().load(uuid);

        if (doc.getFileName().endsWith(".epub"))
            return epubExtractor.extractWordsFromStoredDocument(uuid, pageNumber);

		PdfReader reader = new PdfReader(doc.getFileData());

        if (pageNumber > reader.getNumberOfPages())
            return new ExtractedPage(doc.getUuid(), doc.getFileName(), "", Collections.emptySet(), pageNumber);

		String text = PdfTextExtractor.getTextFromPage(reader, pageNumber);

		Set<String> distinctWords = findDistinctWordsInText(text);

		return new ExtractedPage(doc.getUuid(), doc.getFileName(), text, distinctWords, pageNumber);
	}

	public List<ExtractedPage> extractWordsFromStoredDocument(String uuid) throws IOException{

		List<ExtractedPage> extractedPages = new ArrayList<>();

		Document doc =  archiveService.getDocumentDao().load(uuid);

        if (doc.getFileName().endsWith(".epub"))
            return epubExtractor.extractWordsFromStoredDocument(uuid);

		PdfReader reader = new PdfReader(doc.getFileData());

		for(int i=0;i<reader.getNumberOfPages();i++) {
			String text = PdfTextExtractor.getTextFromPage(reader, (i+1));
			//String[] words = text.replaceAll("^[.,\\s]+", "").split("[.,\\s]+");
			Set<String> distinctWords = findDistinctWordsInText(text);
			extractedPages.add(new ExtractedPage(doc.getUuid(), doc.getFileName(), text, distinctWords, i+1));
		}

		return extractedPages;
	}

	public static Set<String> findDistinctWordsInText(String text) {

		String[] words = text
				.toLowerCase()
				.replace(".", "")
				.replace(",", " ")
				.replace("\n", " ")
				.split(" ");

		//filter words that contains numbers

		Set<String> filteredWords = Arrays.asList(words)
				.stream()
				.filter(word -> ! word.matches("\\w*\\d\\w*"))
				.collect(Collectors.toSet());

		// TODO: 2017-12-24 fix an empty string being collected

		return filteredWords.stream().filter(word -> ! word.isEmpty()).collect(Collectors.toSet());
	}
	
}
