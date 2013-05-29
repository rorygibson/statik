package statik.auth;


import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InMemoryAuthStore implements AuthStore {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(InMemoryAuthStore.class);
    private Map<String, User> users = new HashMap<>();
    private boolean configured = false;

    @Override
    public boolean auth(String username, String password) {
        return this.users.containsKey(username) && this.users.get(username).getPassword().equals(password);
    }

    @Override
    public Collection<User> users() {
        return this.users.values();
    }

    @Override
    public void addUser(String name, String password, boolean isDefault) {
        User u = new User(name, password, isDefault);
        addUser(u);
    }

    @Override
    public void removeUser(String username) {
        this.users.remove(username);
    }

    public void configure(String usersFile) {
        if (!configured) {
            this.users = usersFrom(configurationFrom(usersFile));
            this.configured = true;
        }
    }

    @Override
    public void deleteAllUsersExceptDefault() {
        Set<String> usernames = this.users.keySet();
        for (String username : usernames) {
            User user = this.users.get(username);
            if (!user.isDefault()) {
                this.users.remove(username);
            }
        }
    }

    @Override
    public void addUser(User user) {
        this.users.put(user.getUsername(), user);
    }

    @Override
    public User user(String username) {
        return this.users.get(username);
    }

    @Override
    public void updateUser(String username, User user) {
        this.users.remove(username);
        this.users.put(user.getUsername(), user);
    }

    private Map<String, User> usersFrom(CompositeConfiguration config) {
        LOG.info("Loading users");

        Map<String, User> map = new HashMap<>();
        Iterator keys = config.getKeys("users");

        while (keys.hasNext()) {
            String key = keys.next().toString();
            String username = key.replace("users.", "");
            String password = config.getString(key);
            User u = new User(username, password);
            map.put(username, u);
        }

        LOG.info("Loaded " + map.size() + " users");
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
