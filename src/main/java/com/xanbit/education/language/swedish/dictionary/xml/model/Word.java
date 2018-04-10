package com.xanbit.education.language.swedish.dictionary.xml.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Word {

	@JacksonXmlProperty(localName = "class", isAttribute = true)
	private String wordClass;
	
	@JacksonXmlProperty(localName = "comment", isAttribute = true)
	private String comment;
	
	@JacksonXmlProperty(localName = "lang", isAttribute = true)
	private String language;
	
	@JacksonXmlProperty(localName = "value", isAttribute = true)
	private String value;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName = "translation")
	private List<Translation> translations;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="example")
	private List<Example> examples;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="definition")
	private List<Example> definitions;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="idiom")
	private List<Example> idioms;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="paradigm")
	private List<Paradigm> paradigms;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="synonym")
	private List<Synonym> synonyms;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="related")
	private List<RelatedWord> relatedWords;

	public String getWordClass() {
		return wordClass;
	}

	public String getComment() {
		return comment;
	}

	public String getLanguage() {
		return language;
	}

	public String getValue() {
		return value;
	}

	public List<Translation> getTranslations() {
		return translations;
	}

	public List<Example> getExamples() {
		return examples;
	}

	public List<Example> getDefinitions() {
		return definitions;
	}

	public List<Example> getIdioms() {
		return idioms;
	}

	public List<Paradigm> getParadigms() {
		return paradigms;
	}
	
	public List<Synonym> getSynonyms() {
		return synonyms;
	}
	
	public List<RelatedWord> getRelatedWords() {
		return relatedWords;
	}
	
	public List<String> getDefinitionValues() {
		if (definitions == null)
			return Collections.emptyList();

		return definitions
				.stream()
				.map(def -> def.getValue() + " ")
				.collect(Collectors.toList());
	}
	
	public List<String> getParadigmInflections() {
		List<String> inflections = new ArrayList<String>();
		if (paradigms == null) {
			return inflections;
		}
		for (Paradigm p : paradigms) {
			for (Example example : p.getInflections()) {
				inflections.add(example.getValue());
			}
		}
		return inflections;
	}

    public String getInflectionsString(){
        StringBuilder builder = new StringBuilder();

        getParadigmInflections().stream().forEach(str -> builder.append(str + " ,"));

        return builder.toString();
    }

	public String getTranslationsString() {

		StringBuilder builder = new StringBuilder();

		if (translations != null)
			translations.stream().forEach(tr -> builder.append(tr.getValue() + " ,"));

		return builder.toString();
	}

	public String getSynonymsString() {

		StringBuilder builder = new StringBuilder();

		if (synonyms != null)
			synonyms.stream().forEach(syn -> builder.append(syn.getValue() + " ,"));

		return builder.toString();
	}



	public Set<String> getExamplesTranslations() {
		if (getExamples() == null)
			return Collections.emptySet();

		return examples
				.stream()
				.map(ex -> ex.getValue()+ (ex.getTranslation() != null ? " : "+ex.getTranslation().getValue() : ""))
				.collect(Collectors.toSet());

	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(" Word : "+value);
		builder.append(System.getProperty("line.separator"));
		
		if (paradigms != null) {
			for (Paradigm p : paradigms) {
				builder.append(" Inflections : ");
				for (Example example : p.getInflections()) {
					builder.append(" "+example.getValue()+" ");
				}
				builder.append(System.getProperty("line.separator"));
			}
		}
		
		if (definitions != null) {
			builder.append(" Definitions : ");
			for (Example e : definitions) {
					builder.append(" "+e.getValue()+" ");
			}
			builder.append(System.getProperty("line.separator"));
		}
		
		
		if (translations != null) {
			builder.append(" Translations : ");
			for (Translation t : translations) {
					builder.append(" "+t.getValue()+" ");
			}
			builder.append(System.getProperty("line.separator"));
		}
		
		if (examples != null) {
			builder.append(" Examples : ");
			for (Example e : examples) {
				builder.append(System.getProperty("line.separator"));
				builder.append("	"+e.getValue());
				builder.append(" : ");
				if (e.getTranslation() != null) {
					builder.append(e.getTranslation().getValue());
				}
			}
			builder.append(System.getProperty("line.separator"));
		}
		
		if (synonyms != null) {
			builder.append(" Synonyms : ");
			for (Synonym s : synonyms) {
					builder.append(" "+s.getValue()+" ");
			}
			builder.append(System.getProperty("line.separator"));
		}
		
		if (relatedWords != null) {
			builder.append(" Related : ");
			for (RelatedWord r : relatedWords) {
					builder.append(" ("+r.getType()+")"+r.getValue()+"");
					if (r.getTranslation() != null) {
						builder.append(" :- "+r.getTranslation().getValue());
					}
			}
			builder.append(System.getProperty("line.separator"));
		}
		
		
		return builder.toString();
	}
}


@JsonIgnoreProperties(ignoreUnknown = true)
class Paradigm{
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="inflection")
	private List<Example> inflections;
	
	public List<Example> getInflections() {
		return inflections;
	}
}


@JsonIgnoreProperties(ignoreUnknown = true)
class Example{
	
	@JacksonXmlProperty(localName = "value", isAttribute = true)
	private String value;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="translation")
	private List<Translation> translation;

	public String getValue() {
		return value;
	}

	public Translation getTranslation() {
		return translation != null ? translation.get(0) : null;
	}
	
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Translation{
	
	@JacksonXmlProperty(localName = "comment")
	private String comment;
	
	@JacksonXmlProperty(localName="value")
	private String value;
	
	public String getComment() {
		return comment;
	}
	
	public String getValue() {
		return value;
	}
	
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Synonym {
	
	@JacksonXmlProperty(localName = "level")
	private String level;

	@JacksonXmlProperty(localName = "value")
	private String value;
	
	public String getLevel() {
		return level;
	}
	
	public String getValue() {
		return value;
	}
}

@JsonIgnoreProperties(ignoreUnknown = true)
class RelatedWord{
	
	@JacksonXmlProperty(localName = "type", isAttribute = true)
	private String type;
	
	@JacksonXmlProperty(localName = "value", isAttribute = true)
	private String value;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(localName="translation")
	private List<Translation> translation;

	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}

	public Translation getTranslation() {
		return translation != null ? translation.get(0) : null;
	}
	
}