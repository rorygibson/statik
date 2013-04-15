package com.example.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionStore {

    private Map<String,String> map = new HashMap<String, String>();

    public String createSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        map.put(sessionId, username);
        return sessionId;
    }

    public boolean hasSession(String sessionId) {
        return map.containsKey(sessionId);
    }

    public void deleteSession(String sessionId) {
        map.remove(sessionId);
    }

    public String usernameFor(String sessionId) {
        return map.get(sessionId);
    }
}
