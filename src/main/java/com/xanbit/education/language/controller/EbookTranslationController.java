package com.xanbit.education.language.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class EbookTranslationController {

    @RequestMapping("/ebooklookup")
    public String lookupWordsFromEbook() {
        return "In Progress...";
    }

}
