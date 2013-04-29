package statik.auth;


import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AuthStore {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AuthStore.class);
    private final Map<String, String> users = new HashMap<>();
    private boolean configured = false;

    public void addUser(String username, String password) {
        this.users.put(username, password);
    }

    public boolean auth(String username, String password) {
        return this.users.containsKey(username) && this.users.get(username).equals(password);
    }

    public void configure(String usersFile) {
        if (!configured) {
            LOG.info("Loading users");

            CompositeConfiguration config = new CompositeConfiguration();
            config.addConfiguration(new SystemConfiguration());
            try {
                config.addConfiguration(new PropertiesConfiguration(usersFile));
            } catch (ConfigurationException e) {
                throw new RuntimeException("Couldn't load configuration from " + usersFile);
            }

            Iterator keys = config.getKeys("users");
            while (keys.hasNext()) {
                String key = keys.next().toString();
                String username = key.replace("users.", "");
                String password = config.getString(key);
                this.addUser(username, password);
            }

            this.configured = true;
            LOG.info("Loaded " + users.size() + " users");
        }
    }
}
