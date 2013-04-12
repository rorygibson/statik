package com.example.web;

import java.util.Map;

public interface Database {
    boolean isEmpty();

    void insertOrUpdate(String content, String selector);

    void configure(String configFilename);

    Map<String, ContentItem> findAll();
}
