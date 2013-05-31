package statik.content;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.UsesMongo;
import statik.util.Language;

import java.util.HashMap;
import java.util.Map;

public class MongoContentStore extends UsesMongo implements ContentStore {
    private static final Logger LOG = LoggerFactory.getLogger(MongoContentStore.class);
    public static final String COLLECTION_NAME = "contentItems";

    private DBCollection items;


    @Override
    public void insertOrUpdate(ContentItem contentItem) {
        LOG.debug("Updating with content, size [" + contentItem.size() + "] and selector [" + contentItem.selector() + "]");

        BasicDBObject queryObject = new BasicDBObject(ContentItem.DOMAIN, contentItem.domain()).append(ContentItem.SELECTOR, contentItem.selector()).append(ContentItem.LANGUAGE, contentItem.language().code());
        BasicDBObject updateObject = new BasicDBObject(ContentItem.SELECTOR, contentItem.selector()).append(ContentItem.DOMAIN, contentItem.domain()).append(ContentItem.CONTENT, contentItem.content()).append(ContentItem.PATH
                , contentItem.path()).append(ContentItem.LANGUAGE, contentItem.language().code());
        WriteResult update = items.update(queryObject, updateObject);

        if (update.getN() == 0) {
            LOG.debug("No rows updated, trying insert");
            items.insert(updateObject);
        } else {
            LOG.debug("Updated in DB");
        }
    }

    @Override
    public void configure(String configFilename) {
        super.configure(configFilename);
        this.items = db.getCollection(COLLECTION_NAME);
    }

    @Override
    public Map<String, ContentItem> findForDomainAndPath(String domain, String path, String language) {
        BasicDBObject query = new BasicDBObject(ContentItem.DOMAIN, domain).append(ContentItem.PATH, path).append(ContentItem.LANGUAGE, language);
        DBCursor cursor = this.items.find(query);

        Map<String, ContentItem> items = new HashMap<>();
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            ContentItem contentItem = contentItemFrom(dbObject);
            items.put(contentItem.selector(), contentItem);
        }
        return items;
    }

    @Override
    public void clearContentItems() {
        LOG.warn("Dropping " + COLLECTION_NAME + " collection");
        this.items.drop();
    }

    @Override
    public ContentItem findBy(String domain, String path, String selector, Language language) {
        DBObject q = new BasicDBObject(ContentItem.DOMAIN, domain).append(ContentItem.PATH, path).append(ContentItem.SELECTOR, selector).append(ContentItem.LANGUAGE, language.code());
        DBObject dbObject = items.findOne(q);
        if (dbObject == null) {
            return null;
        }
        return contentItemFrom(dbObject);
    }

    @Override
    public void makeContentLiveFor(String domain, String path) {
        LOG.debug("Setting content with domain [" + domain + "], path [" + path + "] live");
        DBCursor dbObjects = this.items.find(new BasicDBObject(ContentItem.DOMAIN, domain).append(ContentItem.PATH, path));
        int ctr = 0;
        while (dbObjects.hasNext()) {
            DBObject obj = dbObjects.next();
            obj.put(ContentItem.LIVE, true);
            this.items.save(obj);
            ctr++;
        }
        LOG.debug("Updated page with domain [" + domain + "] and path [" + path + "], setting " + ctr + " content items live");
    }


    private ContentItem contentItemFrom(DBObject dbObject) {
        String domain = dbObject.get(ContentItem.DOMAIN).toString();
        String content = dbObject.get(ContentItem.CONTENT).toString();
        String selector = dbObject.get(ContentItem.SELECTOR).toString();
        String itemPath = dbObject.get(ContentItem.PATH).toString();
        Object liveObj = dbObject.get(ContentItem.LIVE);
        boolean live = liveObj == null ? false : (Boolean) liveObj;

        return new ContentItem(domain, itemPath, selector, content, live);
    }

}