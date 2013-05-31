package statik.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.UsesRDBMS;
import statik.util.Language;

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
        LOG.debug("Inserting or updating content item with domain [" + contentItem.domain() + "], path [" + contentItem.path() + "], language [" + contentItem.language().code() + "] and selector [" + contentItem.selector() + "]");
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("insert into statik_content(domain,path,selector,content,is_copy,is_live,language) values(?,?,?,?,?,?,?)");
            stmt.setString(1, contentItem.domain());
            stmt.setString(2, contentItem.path());
            stmt.setString(3, contentItem.selector());
            stmt.setString(4, contentItem.content());
            stmt.setBoolean(5, contentItem.isCopy());
            stmt.setBoolean(6, contentItem.live());
            stmt.setString(7, contentItem.language().code());
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
    public Map<String, ContentItem> findForDomainAndPath(String domain, String path, String language) {
        LOG.info("Retrieving content for domain [" + domain + "], language [" + language + "] and path [" + path + "]");

        Map<String, ContentItem> map = new HashMap<>();
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("select domain,path,selector,content,is_copy,is_live, language from statik_content where domain=? and path=? and language=?");
            stmt.setString(1, domain);
            stmt.setString(2, path);
            stmt.setString(3, language);
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
    public ContentItem findBy(String domain, String path, String selector, Language language) {
        LOG.info("Retrieving content for domain [" + domain + "], path [" + path + "], selector [" + selector + "] and language [" + language.code() + "]");

        ContentItem item = null;
        Connection connection = null;
        try {
            connection = this.connectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement("select domain,path,selector,content,is_copy,is_live,language from statik_content where domain=? and path=? and selector=? and language=?");
            stmt.setString(1, domain);
            stmt.setString(2, path);
            stmt.setString(3, selector);
            stmt.setString(4, language.code());
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
        return contentItemFrom(rs.getString(ContentItem.DOMAIN), rs.getString(ContentItem.PATH), rs.getString(ContentItem.SELECTOR), rs.getString(ContentItem.CONTENT), rs.getBoolean(ContentItem.IS_COPY), rs.getBoolean(ContentItem.LIVE), rs.getString(ContentItem.LANGUAGE));
    }

    private ContentItem contentItemFrom(String domain, String path, String selector, String content, boolean copy, boolean live, String lang) {
        return new ContentItem(domain, path, selector, content, copy, live, Language.from(lang));
    }
}
