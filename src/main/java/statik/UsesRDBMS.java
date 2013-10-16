package statik;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.util.jdbc.DriverDataSource;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class UsesRDBMS {

    private static Logger LOG = LoggerFactory.getLogger(UsesRDBMS.class);
    protected BoneCP connectionPool;
    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcUsername;
    private String jdbcPassword;

    public void configure(String configFilename) {
        loadConfig(configFilename);
        initMigrations();
        createPool();
    }

    private void initMigrations() {
        LOG.info("Performing any DB migrations against " + this.jdbcDriver);
        Flyway flyway = new Flyway();
        DataSource ds = new DriverDataSource(this.jdbcDriver, this.jdbcUrl, this.jdbcUsername, this.jdbcPassword);
        flyway.setDataSource(ds);
        flyway.migrate();
    }

    private void createPool() {
        LOG.info("Creating connection pool");
        try {
            Class.forName(this.jdbcDriver);    // load the DB driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't load JDBC driver class");
        }

        BoneCPConfig config = new BoneCPConfig();    // create a new configuration object
        config.setJdbcUrl(this.jdbcUrl);    // set the JDBC url
        config.setUsername(this.jdbcUsername);
        config.setPassword(this.jdbcPassword);

        try {
            connectionPool = new BoneCP(config);
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't establish BoneCP connection pool", e);
        }
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

        jdbcDriver = config.getString("jdbc.driver");
        jdbcUrl = config.getString("jdbc.url");
        jdbcUsername = config.getString("jdbc.username");
        jdbcPassword = config.getString("jdbc.password");
    }
}
