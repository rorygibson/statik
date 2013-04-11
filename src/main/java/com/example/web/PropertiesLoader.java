package com.example.web;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static final Logger LOG = Logger.getLogger(PropertiesLoader.class);

    public static Properties loadProperties(String propertiesFilename) {
        Properties content = new Properties();
        try {
            ClassLoader contextClassLoader = PropertiesLoader.class.getClassLoader();
            InputStream resourceAsStream = contextClassLoader.getResourceAsStream(propertiesFilename);

            content.load(resourceAsStream);

            LOG.info("Loaded " + content.size() + " properties");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load properties from [" + propertiesFilename + "]", e);
        }
        return content;
    }
}