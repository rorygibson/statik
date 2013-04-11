package com.example.web;

public interface Database {
    boolean isEmpty();

    String get(String id);

    void insertOrUpdate(String id, String content);

    void configure(String configFilename);
}
