package statik;

import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);

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
}