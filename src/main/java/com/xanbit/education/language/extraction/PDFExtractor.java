package com.xanbit.education.language.extraction;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.xanbit.education.language.model.ExtractedPage;
import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.service.archive.ebook.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PDFExtractor {

	@Autowired
	private ArchiveService archiveService;

	public List<ExtractedPage> extractWords(String pdfPath) throws IOException{

		List<ExtractedPage> extractedPages = new ArrayList<>();

		PdfReader reader = new PdfReader(getClass().getClassLoader().getResource(pdfPath).getFile());
		
		for(int i=0;i<reader.getNumberOfPages();i++) {
			String text = PdfTextExtractor.getTextFromPage(reader, (i+1));
            //String[] words = text.replaceAll("^[.,\\s]+", "").split("[.,\\s]+");
			String[] words = text.split("\\W");
            Set<String> distinctWords = Arrays.asList(words).stream().filter(str -> !str.isEmpty()).collect(Collectors.toSet());
            extractedPages.add(new ExtractedPage(pdfPath, pdfPath, text, distinctWords, i+1));
        }
		
		return extractedPages;
	}

	public ExtractedPage extractWordsFromStoredDocument(String uuid, int pageNumber) throws IOException{

		Document doc =  archiveService.getDocumentDao().load(uuid);

		PdfReader reader = new PdfReader(doc.getFileData());

		String text = PdfTextExtractor.getTextFromPage(reader, pageNumber);
		String[] words = text.split("\\W");
		Set<String> distinctWords = Arrays.asList(words).stream().filter(str -> !str.isEmpty()).collect(Collectors.toSet());
		return new ExtractedPage(doc.getUuid(), doc.getFileName(), text, distinctWords, pageNumber);
	}

	public List<ExtractedPage> extractWordsFromStoredDocument(String uuid) throws IOException{

		List<ExtractedPage> extractedPages = new ArrayList<>();

		Document doc =  archiveService.getDocumentDao().load(uuid);

		PdfReader reader = new PdfReader(doc.getFileData());

		for(int i=0;i<reader.getNumberOfPages();i++) {
			String text = PdfTextExtractor.getTextFromPage(reader, (i+1));
			//String[] words = text.replaceAll("^[.,\\s]+", "").split("[.,\\s]+");
			String[] words = text.split("\\W");
			Set<String> distinctWords = Arrays.asList(words).stream().filter(str -> !str.isEmpty()).collect(Collectors.toSet());
			extractedPages.add(new ExtractedPage(doc.getUuid(), doc.getFileName(), text, distinctWords, i+1));
		}

		return extractedPages;
	}
	
}
