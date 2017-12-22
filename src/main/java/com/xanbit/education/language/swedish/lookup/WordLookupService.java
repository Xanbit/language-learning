package com.xanbit.education.language.swedish.lookup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.xanbit.education.language.swedish.dictionary.xml.model.SvEnDictionaryKTH;
import com.xanbit.education.language.swedish.dictionary.xml.model.Word;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class WordLookupService {

    private static final String SWEDISH_TO_ENGLISH_DICTIONARY_XML_PATH = "dictionary/peoples-dictionary-kth-sv-en.xml";

    private static SvEnDictionaryKTH dictionary = loadXMLDictionary();

	public void lookupWords(Set<String> notKnownWords, List<Word> lookedUpWords, Set<String> unfoundWords) {
    	
    	for (String s : notKnownWords) {
			Word word = lookup(s);
			if (word != null) {
				lookedUpWords.add(word);
			}else {
				unfoundWords.add(s);
			}
		}

		System.out.println("Words lookup done. Total looked up words : "+lookedUpWords.size()+" , words not found in dictionary : "+unfoundWords.size());
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
    

    private static SvEnDictionaryKTH loadXMLDictionary(){
    	try {
        	
        	File svEnFile = new File(WordLookupService.class.getClassLoader().getResource(SWEDISH_TO_ENGLISH_DICTIONARY_XML_PATH).getFile());
        	
        	ObjectMapper objectMapper = new XmlMapper();
        	
        	return objectMapper.readValue(svEnFile, SvEnDictionaryKTH.class);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't load SvEn Dictionary....");
        }
    }
}