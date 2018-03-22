package com.xanbit.education.language.extraction;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Set;

public class EpubExtractorTest extends TestCase {

    public void testFindDistinctWordsInText_shouldIgnoreNumericals() throws Exception {

        EpubExtractor extractor = new EpubExtractor();

        extractor.extractWords("ebooks/doktor_glas.epub");
    }

}