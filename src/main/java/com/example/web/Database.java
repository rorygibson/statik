package com.example.web;

import java.util.Map;

public interface Database {
    boolean isEmpty();

    void insertOrUpdate(ContentItem contentItem);

    void configure(String configFilename);

    Map<String,ContentItem> findForPath(String path);
}
