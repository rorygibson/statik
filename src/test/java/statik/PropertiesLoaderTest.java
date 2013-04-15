package statik;


import junit.framework.Assert;
import org.junit.Test;
import statik.PropertiesLoader;

import java.util.Properties;

public class PropertiesLoaderTest {

    @Test
    public void loadsFromFile() {
        Properties result = PropertiesLoader.loadProperties("test-properties-loader.properties");
        Assert.assertEquals("Should have retrieved property", "valueA", result.get("key1"));
    }

    @Test(expected = RuntimeException.class)
    public void loadingMissingFile() {
        PropertiesLoader.loadProperties("missing.properties");
    }
}
