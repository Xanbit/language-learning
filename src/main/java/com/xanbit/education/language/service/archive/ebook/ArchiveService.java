package com.xanbit.education.language.service.archive.ebook;

import com.xanbit.education.language.dao.IDocumentDao;
import com.xanbit.education.language.dao.IUserDao;
import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.model.archive.DocumentMetadata;
import com.xanbit.education.language.service.archive.IArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("archiveService")
public class ArchiveService implements IArchiveService, Serializable {

    private static final long serialVersionUID = 8119784722798361327L;
    
    @Autowired
    private IDocumentDao documentDao;

    @Autowired
    private IUserDao userDao;

    @Override
    public DocumentMetadata save(Document document) {
        getDocumentDao().insert(document);
        return document.getMetadata();
    }

    @Override
    public List<DocumentMetadata> findDocuments(String personName, Date date) {
        return getDocumentDao().findByPersonNameDate(personName, date);
    }

    @Override
    public byte[] getDocumentFile(String id) {
        Document document = getDocumentDao().load(id);
        if(document!=null) {
            return document.getFileData();
        } else {
            return null;
        }
    }

    @Override
    public void saveUserWordBank(String current_user, Set<String> userWordBank) {
        userDao.saveUserWordBank(current_user, userWordBank);
    }

    @Override
    public Set<String> getUserWordBank(String current_user) {
        return userDao.getUserWordBank(current_user);
    }

    @Override
    public void saveDocumentWords(String documentUUID, Map<Integer, Set<String>> words) {
        documentDao.saveDocumentWords(documentUUID, words);
    }

    @Override
    public Map<Integer, Set<String>> getDocumentWords(String documentUUID) {
        return documentDao.getDocumentWords(documentUUID);
    }

    public IDocumentDao getDocumentDao() {
        return documentDao;
    }

    public void setDocumentDao(IDocumentDao documentDao) {
        this.documentDao = documentDao;
    }


}