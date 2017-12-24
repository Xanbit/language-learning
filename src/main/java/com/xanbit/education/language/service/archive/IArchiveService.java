package com.xanbit.education.language.service.archive;

import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.model.archive.DocumentMetadata;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IArchiveService {

    DocumentMetadata save(Document document);

    List<DocumentMetadata> findDocuments(String personName, Date date);

    byte[] getDocumentFile(String id);

    void saveUserWordBank(String current_user, Set<String> userWordBank);

    Set<String> getUserWordBank(String current_user);
}