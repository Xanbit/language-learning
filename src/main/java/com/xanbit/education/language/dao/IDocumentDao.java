package com.xanbit.education.language.dao;

import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.model.archive.DocumentMetadata;

import java.util.Date;
import java.util.List;

public interface IDocumentDao {

    void insert(Document document);

    List<DocumentMetadata> findByPersonNameDate(String personName, Date date);

    Document load(String uuid);
    
}