package com.xanbit.education.language.extraction;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.xanbit.education.language.model.ExtractedPage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PDFExtractor {

	public List<ExtractedPage> extractWords(String pdfPath) throws IOException{

		List<ExtractedPage> extractedPages = new ArrayList<>();

		PdfReader reader = new PdfReader(pdfPath);
		
		for(int i=0;i<reader.getNumberOfPages();i++) {
			String text = PdfTextExtractor.getTextFromPage(reader, (i+1));
            //String[] words = text.replaceAll("^[.,\\s]+", "").split("[.,\\s]+");
			String[] words = text.split("\\W");
            Set<String> distinctWords = Arrays.asList(words).stream().filter(str -> !str.isEmpty()).collect(Collectors.toSet());
            extractedPages.add(new ExtractedPage(text, distinctWords));
        }
		
		return extractedPages;
	}
	
}
