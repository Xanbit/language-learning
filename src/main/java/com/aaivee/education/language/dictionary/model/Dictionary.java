package com.aaivee.apps.education.language.dictionary.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public final class Dictionary {
	
	@JacksonXmlElementWrapper(localName = "ar", useWrapping = false)
	@JacksonXmlProperty(localName="ar")
    private Word[] words;
	
	public Dictionary() {
	}

	public Dictionary(Word[] words) {
		super();
		this.words = words;
	}

	public Word[] getWords() {
		return words;
	}

	public void setWords(Word[] words) {
		this.words = words;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Words ("+words.length+" ) [");
		for (int i = 0; i < 100; i++) {
			builder.append(System.getProperty("line.separator"));
			builder.append(words[i].toString());
		}
		builder.append(" ]");
		return builder.toString();
	}
}

