package com.xanbit.education.language.model;

public class RestFilterWord {

    private String fileName;
    private int pageNumber;
    private String word;
    private int userChoice;

    public RestFilterWord() {
    }

    public RestFilterWord(String fileName, int pageNumber, String word, int userChoice) {
        this.fileName = fileName;
        this.pageNumber = pageNumber;
        this.word = word;
        this.userChoice = userChoice;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(int userChoice) {
        this.userChoice = userChoice;
    }
}
