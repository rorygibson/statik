package statik.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.UsesRDBMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RDBMSContentStore extends UsesRDBMS implements ContentStore {

    private static final Logger LOG = LoggerFactory.getLogger(RDBMSContentStore.class);


    @Override
    public void insertOrUpdate(ContentItem contentItem) {
        LOG.debug("Inserting or updating content item with domain [" + contentItem.domain() + "], path [" + contentItem.path() + "] and selector [" + contentItem.selector() + "]");
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("insert into statik_content(domain,path,selector,content,is_copy,is_live) values(?,?,?,?,?,?)");
            stmt.setString(1, contentItem.domain());
            stmt.setString(2, contentItem.path());
            stmt.setString(3, contentItem.selector());
            stmt.setString(4, contentItem.content());
            stmt.setBoolean(5, contentItem.isCopy());
            stmt.setBoolean(6, contentItem.live());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error inserting content", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
    }

    @Override
    public Map<String, ContentItem> findForDomainAndPath(String domain, String path) {
        LOG.info("Retrieving content for domain [" + domain + "] and path [" + path + "]");

        Map<String, ContentItem> map = new HashMap<>();
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("select domain,path,selector,content,is_copy,is_live from statik_content where domain=? and path=?");
            stmt.setString(1, domain);
            stmt.setString(2, path);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ContentItem item = contentItemFrom(rs);
                map.put(item.selector(), item);
            }
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error finding content", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
        return map;
    }

    @Override
    public void clearContentItems() {
        LOG.debug("Clearing ALL content items");
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("delete from statik_content");
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error deleting content", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
    }


    @Override
    public ContentItem findBy(String domain, String path, String selector) {
        LOG.info("Retrieving content for domain [" + domain + "], path [" + path + "] and selector [" + selector + "]");

        ContentItem item = null;
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("select domain,path,selector,content,is_copy,is_live from statik_content where domain=? and path=? and selector=?");
            stmt.setString(1, domain);
            stmt.setString(2, path);
            stmt.setString(3, selector);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                item = contentItemFrom(rs);
            }
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error finding content", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
        return item;
    }

    @Override
    public void makeContentLiveFor(String domain, String path) {
        LOG.debug("Making content items for domain [" + domain + "] and path [" + path + "] live");
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("update statik_content set is_live=true where domain=? and path=?");
            stmt.setString(1, domain);
            stmt.setString(2, path);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            LOG.error("DB connection error making content live", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOG.error("Problem closing connection", e);
            }
        }
    }

    private ContentItem contentItemFrom(ResultSet rs) throws SQLException {
        return contentItemFrom(rs.getString(ContentItem.DOMAIN), rs.getString(ContentItem.PATH), rs.getString(ContentItem.SELECTOR), rs.getString(ContentItem.CONTENT), rs.getBoolean(ContentItem.IS_COPY), rs.getBoolean(ContentItem.LIVE));
    }

    private ContentItem contentItemFrom(String domain, String path, String selector, String content, boolean copy, boolean live) {
        return new ContentItem(domain, path, selector, content, copy, live);
    }
}
