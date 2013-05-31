package statik.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.UsesRDBMS;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class RDBMSAuthStore extends UsesRDBMS implements AuthStore {

    private static final Logger LOG = LoggerFactory.getLogger(RDBMSAuthStore.class);

    @Override
    public void configure(String configFilename) {
        super.configure(configFilename);
        checkAndCreateAdminUser();
    }

    @Override
    public boolean auth(String username, String password) {
        User user = user(username);
        if (user.isAnonymous()) {
            return false;
        }
        return user.getPassword().equals(password);
    }

    @Override
    public Collection<User> users() {
        LOG.info("Retrieving all users");

        Collection<User> users = new ArrayList<>();
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("select * from statik_user");
            while (resultSet.next()) {
                User u = new User(resultSet.getString("username"), resultSet.getString("password"), resultSet.getBoolean("is_default"));
                users.add(u);
            }
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error checking default user", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
        return users;
    }

    @Override
    public void addUser(String name, String password, boolean isDefault) {
        LOG.info("Adding user [" + name + "], isDefault [" + isDefault + "]");
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement insert = connection.prepareStatement("insert into statik_user(username, password, is_default) values (?, ?, ?)");
            insert.setString(1, name);
            insert.setString(2, password);
            insert.setBoolean(3, isDefault);
            insert.execute();
            insert.close();
        } catch (SQLException e) {
            LOG.error("DB connection error checking default user", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
    }

    @Override
    public void removeUser(String username) {
        LOG.info("Deleting user [" + username + "]");

        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("delete from statik_user where username=?");
            stmt.setString(1, username);
            stmt.execute();
            stmt.close();
            connection.commit();
        } catch (SQLException e) {
            LOG.error("DB connection error deleting user", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }

    }

    @Override
    public void deleteAllUsersExceptDefault() {
        LOG.info("Deleting all users except default");

        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            Statement stmt = connection.createStatement();
            stmt.execute("delete from statik_user where is_default=false");
            stmt.close();
            connection.commit();
        } catch (SQLException e) {
            LOG.error("DB connection error deleting users", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
    }

    @Override
    public void addUser(User user) {
        addUser(user.getUsername(), user.getPassword(), user.isDefault());
    }

    @Override
    public User user(String username) {
        LOG.info("Querying for user [" + username + "]");

        User user = new AnonymousUser();
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("select * from statik_user where username=?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"), rs.getBoolean("is_default"));
            }

            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error checking default user", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }

        return user;
    }

    @Override
    public void updateUser(String existingUsername, User user) {
        LOG.info("Updating user [" + existingUsername + "]");
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement insert = connection.prepareStatement("update statik_user set username=?, password=?, is_default=? where username=?");
            insert.setString(1, user.getUsername());
            insert.setString(2, user.getPassword());
            insert.setBoolean(3, user.isDefault());
            insert.setString(4, existingUsername);
            insert.execute();
            insert.close();
        } catch (SQLException e) {
            LOG.error("DB connection error updating user", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
    }

    private void checkAndCreateAdminUser() {
        LOG.info("Checking for default user");

        User admin = user("admin");
        if (admin.isAnonymous()) {
            addUser("admin", "password", true);
        }
    }

}
