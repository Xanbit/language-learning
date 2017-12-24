package com.xanbit.education.language.dao;

import com.xanbit.education.language.model.archive.Document;
import com.xanbit.education.language.model.archive.DocumentMetadata;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IUserDao {

    void saveUserWordBank(String current_user, Set<String> userWordBank);

    Set<String> getUserWordBank(String current_user);
    
}