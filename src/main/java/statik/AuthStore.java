package statik;


import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AuthStore {

    private static final Logger LOG = Logger.getLogger(AuthStore.class);
    private Map<String, String> users = new HashMap<String, String>();
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

            Properties config = PropertiesLoader.loadPropertiesFrom(new File(usersFile));
            for (Object key : config.keySet()) {
                String username = key.toString();
                String password = config.getProperty(username);
                this.addUser(username, password);
            }

            this.configured = true;
            LOG.info("Loaded " + users.size() + " users");
        }
    }
}
