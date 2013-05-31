package statik.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.UsesRDBMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RDBMSSessionStore extends UsesRDBMS implements SessionStore {

    private static final Logger LOG = LoggerFactory.getLogger(RDBMSSessionStore.class);

    @Override
    public String createSession(String username) {
        LOG.debug("creating session for [" + username + "]");

        String sessionId = null;
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("insert into statik_session(session_id, username) values (?,?)");
            sessionId = generateSessionId();
            stmt.setString(1, sessionId);
            stmt.setString(2, username);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error creating session", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
        return sessionId;
    }

    @Override
    public boolean hasSession(String sessionId) {
        LOG.info("Checking for existing session for [" + sessionId + "]");

        Connection connection = null;
        boolean found = false;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("select username from statik_session where session_id=?");
            stmt.setString(1, sessionId);;
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                found = true;
            }
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error finding session", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
        return found;
    }

    @Override
    public void deleteSession(String sessionId) {
        LOG.info("Deleting session with sessionId [" + sessionId + "]");

        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("delete from statik_session where session_id=?");
            stmt.setString(1, sessionId);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error deleting session", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
    }

    @Override
    public String usernameFor(String sessionId) {
        LOG.info("Retrieving username for sessionId [" + sessionId + "]");

        Connection connection = null;
        String username = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("select username from statik_session where session_id=?");
            stmt.setString(1, sessionId);;
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            }
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error finding username", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
        return username;
    }

    @Override
    public void deleteAllSessions() {
        LOG.debug("Clearing ALL sessions");
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("delete from statik_session");
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error deleting sessions", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
