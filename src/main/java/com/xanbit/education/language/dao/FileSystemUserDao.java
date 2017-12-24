package com.xanbit.education.language.dao;

import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.model.archive.DocumentMetadata;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service("userDao")
public class FileSystemUserDao implements IUserDao {

    private static final Logger LOG = Logger.getLogger(FileSystemUserDao.class);
    
    public static final String DIRECTORY = "archive-user";
    public static final String WORD_BANK_STORE_FILE = "wordbank.wb";
    
    @PostConstruct
    public void init() {
        createDirectory(DIRECTORY);
    }


    @Override
    public void saveUserWordBank(String current_user, Set<String> userWordBank) {
        try {
            createDirectory(getDirectoryPath(current_user));
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(new File(new File(getDirectoryPath(current_user)), WORD_BANK_STORE_FILE)));
            stream.writeObject(userWordBank);
            stream.close();
        } catch (IOException e) {
            String message = "Error while saving user word bank";
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public Set<String> getUserWordBank(String current_user) {
        try {
            return loadFromFileSystem(getDirectoryPath(current_user), current_user);
        } catch (IOException | ClassNotFoundException e) {
            String message = "Error while loading user word bank : " + current_user;
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private Set<String> loadFromFileSystem(String directoryPath, String user) throws IOException, ClassNotFoundException {
       Path path = Paths.get(directoryPath+File.separator+WORD_BANK_STORE_FILE);
       if ( ! path.toFile().exists()) {
           saveUserWordBank(user, new HashSet<String>());
           return new HashSet<>();
       }

        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(path.toFile()));

       Set<String> result = (Set<String>)stream.readObject();
       return result;
    }

    private String getDirectoryPath(String uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append(DIRECTORY).append(File.separator).append(uuid);
        String path = sb.toString();
        return path;
    }

    private void createDirectory(String path) {
        File file = new File(path);
        file.mkdirs();
    }

}