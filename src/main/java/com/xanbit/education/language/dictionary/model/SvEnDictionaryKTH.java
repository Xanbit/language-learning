package com.xanbit.education.language.dictionary.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "xdxf")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class SvEnDictionaryKTH {
	
	@JacksonXmlProperty(localName = "lexicon")
	private Dictionary dictionary;
	
	public Dictionary getDictionary() {
		return dictionary;
	}
	
	@Override
	public String toString() {
		return dictionary.toString();
	}

}

