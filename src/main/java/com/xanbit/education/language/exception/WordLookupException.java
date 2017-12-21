package com.xanbit.education.language.exception;

public class WordLookupException  extends Exception{

    @Override
    public String getMessage() {
        return "Problem while lookup the words from dictionary : "+super.getMessage();
    }
}
