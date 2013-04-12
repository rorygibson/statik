package com.example.web;

/**
* Created with IntelliJ IDEA.
* User: rory
* Date: 12/04/2013
* Time: 17:12
* To change this template use File | Settings | File Templates.
*/
class ContentItem {
    private final String content;
    private final String id;

    public ContentItem(String id, String content) {
        this.content = content;
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public String content() {
        return this.content;
    }
}
