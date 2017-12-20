package com.xanbit.education.language.swedish.dictionary.pdf;

import java.io.IOException;
import java.util.*;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class PDFSplitter {

	public Set<String> getWordsFromPDF(String pdfPath) throws IOException{
		
		Map<String, Integer> wordsMap = new HashMap<String, Integer>();
		PdfReader reader = new PdfReader(pdfPath);
		
		for(int i=0;i<reader.getNumberOfPages();i++) {
			String text = PdfTextExtractor.getTextFromPage(reader, (i+1));
            //String[] words = text.replaceAll("^[.,\\s]+", "").split("[.,\\s]+");
			String[] words = text.split("\\W");
            for (String s : words) {
				if (wordsMap.containsKey(s)) {
					wordsMap.put(s, wordsMap.get(s)+1);
				}else {
					wordsMap.put(s, 1);
				}
			}   
        }
		
		System.out.println("text extracted from PDF : total distict words : "+wordsMap.keySet().size());
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(wordsMap.entrySet());
	    Collections.sort( list, new Comparator<Map.Entry<String, Integer>>() {
	        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
	            return (o2.getValue()).compareTo(o1.getValue());
	        }
	    });
	    
	    System.out.println(list);
		
		return wordsMap.keySet();
	}
	
}
