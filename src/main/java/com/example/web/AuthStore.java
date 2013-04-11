package com.example.web;


import java.util.HashMap;
import java.util.Map;

public class AuthStore {

    private static Map<String, String> users = new HashMap<String, String>();

    public void addUser(String username, String password) {
        this.users.put(username, hashOf(password));
    }

    public boolean auth(String username, String password) {
        return this.users.containsKey(username) && this.users.get(username).equals(hashOf(password));
    }

    // TODO woefully insecure
    private String hashOf(String password) {
        return password;
    }
}
