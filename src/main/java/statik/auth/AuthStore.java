package statik.auth;


import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AuthStore {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AuthStore.class);
    private Map<String, String> users = new HashMap<>();
    private boolean configured = false;

    public boolean auth(String username, String password) {
        return this.users.containsKey(username) && this.users.get(username).equals(password);
    }

    public List<User> users() {
        List<User> theUsers = new ArrayList<>();
        for (String username : this.users.keySet()) {
            String password = this.users.get(username);
            User u = new User(username, password);
            theUsers.add(u);
        }
        return theUsers;
    }

    public void addUser(String name, String password) {
        this.users.put(name, password);
    }

    public void configure(String usersFile) {
        if (!configured) {
            this.users = usersFrom(configurationFrom(usersFile));
            this.configured = true;
        }
    }

    private Map<String, String> usersFrom(CompositeConfiguration config) {
        LOG.info("Loading users");

        Map<String, String> map = new HashMap<>();
        Iterator keys = config.getKeys("users");

        while (keys.hasNext()) {
            String key = keys.next().toString();
            String username = key.replace("users.", "");
            String password = config.getString(key);
            map.put(username, password);
        }

        LOG.info("Loaded " + users.size() + " users");
        return map;
    }

    private CompositeConfiguration configurationFrom(String usersFile) {
        CompositeConfiguration config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        try {
            config.addConfiguration(new PropertiesConfiguration(usersFile));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Couldn't load configuration from " + usersFile);
        }
        return config;
    }
}
