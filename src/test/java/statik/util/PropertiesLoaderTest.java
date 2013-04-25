package statik.util;


import org.junit.Test;
import statik.PropertiesLoader;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class PropertiesLoaderTest {

    @Test
    public void loadsFromFile() {
        Properties result = PropertiesLoader.loadProperties("test-properties-loader.properties");
        assertEquals("Should have retrieved property", "valueA", result.get("key1"));
    }

    @Test(expected = RuntimeException.class)
    public void loadingMissingFile() {
        PropertiesLoader.loadProperties("missing.properties");
    }
}
