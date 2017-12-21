package com.xanbit.education.language.model;

import java.util.Set;

public class ExtractedPage {

    private String rawText;
    private Set<String> allWords;

    public ExtractedPage(String rawText, Set<String> allWords) {
        this.rawText = rawText;
        this.allWords = allWords;
    }

    public String getRawText() {
        return rawText;
    }

    public Set<String> getAllWords() {
        return allWords;
    }
}
