package com.xanbit.education.language.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xanbit.education.language.dictionary.pdf.PDFSplitter;
import com.xanbit.education.language.dictionary.pdf.PDFWriter;
import com.xanbit.education.language.dictionary.xml.model.SvEnDictionaryKTH;
import com.xanbit.education.language.dictionary.xml.model.Word;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.itextpdf.text.DocumentException;

public class WordLookup {

    private static final String SWEDISH_TO_ENGLISH_DICTIONARY_XDXF_PATH = "/Users/markiv/peoples-dictionary-kth-sv-en.xdxf";
    private static final String SWEDISH_TO_ENGLISH_DICTIONARY_XML_PATH = "/Users/markiv/peoples-dictionary-kth-sv-en.xml";
    
    private static final String SAMPLE_PDF_PATH = "/Users/markiv/svenskasamplepdf.pdf";
    private static final String USER_INPUT_TEMP_SAVE_FILE = "/Users/markiv/user-input.txt";
    
    private SvEnDictionaryKTH dictionary;
    
    public void pdfWordsLookup() throws IOException, DocumentException{
    	
    	//read pdf and split into words
    	Set<String> splittedPDFWords = new PDFSplitter().getWordsFromPDF(SAMPLE_PDF_PATH);
    	
    	//filter words
    	Set<String> notKnownWords = getUserInput(splittedPDFWords);
    	
    	//lookup unknown words in dictionary
    	List<Word> lookedUpWords = new ArrayList<Word>();
    	Set<String> unfoundWords = new HashSet<String>();
    	lookupWords(notKnownWords, lookedUpWords, unfoundWords);
    	
    	//print to pdf
    	new PDFWriter().write(lookedUpWords, notKnownWords);
    }

	private void lookupWords(Set<String> notKnownWords, List<Word> lookedUpWords, Set<String> unfoundWords) {
		loadXMLDictionary();
    	
    	for (String s : notKnownWords) {
			Word word = lookup(s);
			if (word != null) {
				lookedUpWords.add(word);
			}else {
				unfoundWords.add(s);
			}
		}
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
    
    private Word lookup(String wordToLookup){
    	for (Word word : dictionary.getWords()) {
			if (word.getValue().equalsIgnoreCase(wordToLookup)) {
				return word;
			}else {
				List<String> inflections = word.getParadigmInflections();
				for (String inf : inflections) {
					if (inf.equalsIgnoreCase(wordToLookup)) {
						return word;
					}
				}
			}
		}
    	return null;
    }
    
    
    private void loadDictionary(){
    	try {
        	
        	File svEnFile = new File(SWEDISH_TO_ENGLISH_DICTIONARY_XDXF_PATH);
        	
        	ObjectMapper objectMapper = new XmlMapper();
        	
        	dictionary = objectMapper.readValue(svEnFile, SvEnDictionaryKTH.class);
        	
        	System.out.println(dictionary);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadXMLDictionary(){
    	try {
        	
        	File svEnFile = new File(SWEDISH_TO_ENGLISH_DICTIONARY_XML_PATH);
        	
        	ObjectMapper objectMapper = new XmlMapper();
        	
        	dictionary = objectMapper.readValue(svEnFile, SvEnDictionaryKTH.class);
        	
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException, DocumentException {
    	WordLookup lookup = new WordLookup();
    	lookup.pdfWordsLookup();
        
    }
}