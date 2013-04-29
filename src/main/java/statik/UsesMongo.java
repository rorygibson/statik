package statik;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

public class UsesMongo {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UsesMongo.class);

    protected MongoClient mongoClient = null;
    protected String dbName = "";
    protected String mongoHost = "";
    protected int mongoPort = 0;
    protected String mongoUsername = "";
    protected String mongoPassword = "";
    protected boolean mongoAuth = false;
    protected boolean configured = false;
    protected DB db;


    public void configure(String configFilename) {
        if (configured) {
            return;
        }

        loadConfig(configFilename);

        LOG.info("Connecting to MongoDB on " + mongoHost + ":" + mongoPort);
        this.mongoClient = mongoClientFor(mongoHost, mongoPort);
        this.db = mongoClient.getDB(dbName);

        if (mongoAuth) {
            boolean auth = db.authenticate(mongoUsername, mongoPassword.toCharArray());
            if (!auth) {
                throw new RuntimeException("Couldn't authenticate with MongoDB");
            }
        }

        this.configured = true;
    }


    protected void loadConfig(String filename) {
        LOG.info("Loading config");

        CompositeConfiguration config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        try {
            config.addConfiguration(new PropertiesConfiguration(filename));
        } catch (ConfigurationException e) {
            throw new RuntimeException("Couldn't load configuration from " + filename);
        }

        dbName = config.getString("dbName");
        mongoHost = config.getString("mongoHost");
        mongoPort = config.getInt("mongoPort");
        mongoUsername = config.getString("mongoUsername");
        mongoPassword = config.getString("mongoPassword");
        mongoAuth = config.getBoolean("mongoAuth");
    }


    protected MongoClient mongoClientFor(String host, int port) {
        try {
            return new MongoClient(host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Couldn't connect to MongoDB", e);
        }
    }

}
