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
import java.util.Set;

@Service("archiveService")
public class ArchiveService implements IArchiveService, Serializable {

    private static final long serialVersionUID = 8119784722798361327L;
    
    @Autowired
    private IDocumentDao DocumentDao;

    @Autowired
    private IUserDao UserDao;

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
        UserDao.saveUserWordBank(current_user, userWordBank);
    }

    @Override
    public Set<String> getUserWordBank(String current_user) {
        return UserDao.getUserWordBank(current_user);
    }

    public IDocumentDao getDocumentDao() {
        return DocumentDao;
    }

    public void setDocumentDao(IDocumentDao documentDao) {
        DocumentDao = documentDao;
    }


}