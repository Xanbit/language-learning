package com.xanbit.education.language.dao;

import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.model.archive.DocumentMetadata;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDocumentDao {

    void insert(Document document);

    List<DocumentMetadata> findByPersonNameDate(String personName, Date date);

    Document load(String uuid);

    Map<Integer, Set<String>> getDocumentWords(String documentUUID);

    void saveDocumentWords(String documentUUID, Map<Integer, Set<String>> allWords);
    
}