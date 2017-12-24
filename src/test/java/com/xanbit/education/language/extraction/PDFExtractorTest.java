package com.xanbit.education.language.extraction;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Set;

public class PDFExtractorTest extends TestCase {

    public void testFindDistinctWordsInText_shouldIgnoreNumericals() throws Exception {

        Set<String> allWords = PDFExtractor.findDistinctWordsInText("titta på 15nde plats, det säger 1200");

        System.out.println(allWords);

        Assert.assertTrue(allWords.contains("titta"));
        Assert.assertTrue(allWords.contains("på"));

        Assert.assertFalse(allWords.contains("15nde"));
        Assert.assertFalse(allWords.contains("1200"));
    }

    public void testFindDistinctWordsInText() throws Exception {

        Set<String> allWords = PDFExtractor.findDistinctWordsInText(getSampleSwedishText());

        System.out.println(allWords);

        Assert.assertTrue("Till not found", allWords.contains("till"));
        Assert.assertTrue("Neda not found", allWords.contains("neda"));
        Assert.assertTrue("Det not found", allWords.contains("det"));
        Assert.assertTrue("är not found", allWords.contains("är"));
        Assert.assertTrue("alltid not found", allWords.contains("alltid"));
        Assert.assertTrue("för not found", allWords.contains("för"));
        Assert.assertTrue("att not found", allWords.contains("att"));
        Assert.assertTrue("få not found", allWords.contains("få"));
        Assert.assertTrue("dig not found", allWords.contains("dig"));
        Assert.assertTrue("skratta not found", allWords.contains("skratta"));

        //only distinct small caps words
        Assert.assertFalse("Alltid upper case found", allWords.contains("Alltid"));
    }

    private String getSampleSwedishText(){
        return "Till Neda. Det är alltid för att få dig att skratta.\n" +
                "Alltid.";
    }

}