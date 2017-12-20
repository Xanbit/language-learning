package com.aaivee.apps.education.language.dictionary.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Word {

	@JacksonXmlProperty(localName = "k")
	private String word;
	
	@JacksonXmlProperty(localName = "def")
	private Definition definition;
	
	public Word() {
	}

	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}

	public Object getDefinition() {
		return definition;
	}

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	@Override
	public String toString() {
		return "k : "+word+", def : "+definition;
	}
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Definition{
	
	@JacksonXmlProperty(localName = "gr")
	private String type;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="dtrn")
	private List<String> definitions;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="sr")
	private List<RelatedWord> relatedWords;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="ex")
	private List<Example> examples;
	
	@JacksonXmlProperty(localName="def")
	private String localDef;
	
	@Override
	public String toString() {
		return type + ", "+definitions + ", "+relatedWords +", "+examples+""+localDef;
	}
	
}

class RelatedWord {
	
	@JacksonXmlProperty(localName = "kref")
	private RW rw;
	
	public RW getRw() {
		return rw;
	}
	
	public void setRw(RW rw) {
		this.rw = rw;
	}
	
	public RelatedWord() {
	}
	
	@Override
	public String toString() {
		return rw.toString();
	}
}




class RW {
	
	@JacksonXmlProperty(localName = "type", isAttribute = true)
	private String type;
	
	@JacksonXmlText
	private String value;

	public RW() {
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return type + ", "+value;
	}
}


class Example {
	
	@JacksonXmlProperty(localName = "type", isAttribute = true)
	private String type;
	
	@JacksonXmlProperty(localName="ex_orig")
	private String originalEx;
	
	@JacksonXmlProperty(localName="ex_transl")
	private String translatedEx;
	
	public Example() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOriginalEx() {
		return originalEx;
	}

	public void setOriginalEx(String originalEx) {
		this.originalEx = originalEx;
	}

	public String getTranslatedEx() {
		return translatedEx;
	}

	public void setTranslatedEx(String translatedEx) {
		this.translatedEx = translatedEx;
	}

	@Override
	public String toString() {
		return type +", "+originalEx+", "+translatedEx;
	}
}