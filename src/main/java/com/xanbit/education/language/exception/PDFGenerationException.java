package com.xanbit.education.language.exception;

public class PDFGenerationException extends Exception{

    @Override
    public String getMessage() {
        return "Problem while generating the pdf : "+super.getMessage();
    }
}
