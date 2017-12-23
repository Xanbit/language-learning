package com.xanbit.education.language.model;

import java.util.Set;

public class ExtractedPage {

    private String fileUUID;
    private String fileName;
    private String rawText;
    private Set<String> allWords;
    private int pageNumber;

    public ExtractedPage(String fileUUID, String fileName, String rawText, Set<String> allWords, int pageNumber) {
        this.fileUUID = fileUUID;
        this.fileName = fileName;
        this.rawText = rawText;
        this.allWords = allWords;
        this.pageNumber = pageNumber;
    }

    public String getFileUUID() {
        return fileUUID;
    }

    public void setFileUUID(String fileUUID) {
        this.fileUUID = fileUUID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public void setAllWords(Set<String> allWords) {
        this.allWords = allWords;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getRawText() {
        return rawText;
    }

    public Set<String> getAllWords() {
        return allWords;
    }
}
