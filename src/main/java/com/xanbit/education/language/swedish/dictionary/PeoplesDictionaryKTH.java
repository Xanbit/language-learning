package com.xanbit.education.language.swedish.dictionary;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class PeoplesDictionaryKTH {

    private static final String SWEDISH_TO_ENGLISH_DICTIONARY_URL = "http://folkets-lexikon.csc.kth.se/folkets/folkets_sv_en_public.xml";

    private static final String ENGLISH_TO_SWEDISH_DICTIONARY_URL = "http://folkets-lexikon.csc.kth.se/folkets/folkets_en_sv_public.xml";

    private static final String SWEDISH_TO_ENGLISH_DICTIONARY_XDXF_URL = "http://folkets-lexikon.csc.kth.se/folkets/folkets_sv_en_public.xdxf";

    private static final String ENGLISH_TO_SWEDISH_DICTIONARY_XDXF_URL = "http://folkets-lexikon.csc.kth.se/folkets/folkets_en_sv_public.xdxf";
    
    private static final String SWEDISH_TO_ENGLISH_DICTIONARY_XDXF_PATH = "/Users/markiv/peoples-dictionary-kth-sv-en.xdxf";

    private static final String ENGLISH_TO_SWEDISH_DICTIONARY_XDXF_PATH = "/Users/markiv/peoples-dictionary-kth-en-sv.xdxf";
    
    private static final String SWEDISH_TO_ENGLISH_DICTIONARY_XML_PATH = "/Users/markiv/peoples-dictionary-kth-sv-en.x";
    
    public static void main(String[] args) throws IOException {

        downloadDictionaries();
    }

	private static void downloadDictionaries() throws IOException {
		
		downloadUsingStream(SWEDISH_TO_ENGLISH_DICTIONARY_URL, "/Users/markiv/peoples-dictionary-kth-sv-en.xml");
		
		downloadUsingStream(ENGLISH_TO_SWEDISH_DICTIONARY_URL, "/Users/markiv/peoples-dictionary-kth-en-sv.xml");
		
		downloadUsingStream(SWEDISH_TO_ENGLISH_DICTIONARY_XDXF_URL, "/Users/markiv/peoples-dictionary-kth-sv-en.xdxf");
		
		downloadUsingStream(ENGLISH_TO_SWEDISH_DICTIONARY_XDXF_URL, "/Users/markiv/peoples-dictionary-kth-en-sv.xdxf");
	}

    private static void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    private static void downloadUsingNIO(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

}