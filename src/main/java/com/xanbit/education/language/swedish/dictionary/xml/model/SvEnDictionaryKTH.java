package com.xanbit.education.language.swedish.dictionary.xml.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "dictionary")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SvEnDictionaryKTH {
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="word")
	private List<Word> words;
	
	public List<Word> getWords() {
		return words;
	}

}

