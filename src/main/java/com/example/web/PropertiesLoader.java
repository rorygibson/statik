package com.example.web;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
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

            LOG.debug("Loaded " + content.size() + " properties");
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load properties from [" + propertiesFilename + "]", e);
        }
        return content;
    }


    public static Properties loadPropertiesFrom(File file) {
        Properties props = new Properties();
        try {
            props.load(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load from " + file.getAbsolutePath());
        }
        return props;
    }
}