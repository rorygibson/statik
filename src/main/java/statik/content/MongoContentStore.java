package statik.content;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.UsesMongo;

import java.util.HashMap;
import java.util.Map;

public class MongoContentStore extends UsesMongo implements ContentStore {
    private static final Logger LOG = LoggerFactory.getLogger(MongoContentStore.class);
    public static final String COLLECTION_NAME = "contentItems";

    private DBCollection items;


    @Override
    public void insertOrUpdate(ContentItem contentItem) {
        LOG.debug("Updating with content, size [" + contentItem.size() + "] and selector [" + contentItem.selector() + "]");

        BasicDBObject queryObject = new BasicDBObject(ContentItem.SELECTOR, contentItem.selector());
        BasicDBObject updateObject = new BasicDBObject(ContentItem.SELECTOR, contentItem.selector()).append(ContentItem.CONTENT, contentItem.content()).append(ContentItem.PATH
                , contentItem.path());
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
    public Map<String, ContentItem> findForPath(String path) {
        BasicDBObject query = new BasicDBObject(ContentItem.PATH, path);
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
    public ContentItem findByPathAndSelector(String path, String selector) {
        DBObject q = new BasicDBObject(ContentItem.PATH, path).append(ContentItem.SELECTOR, selector);
        DBObject dbObject = items.findOne(q);
        if (dbObject == null) {
            return null;
        }
        return contentItemFrom(dbObject);
    }


    private ContentItem contentItemFrom(DBObject dbObject) {
        String content = dbObject.get(ContentItem.CONTENT).toString();
        String selector = dbObject.get(ContentItem.SELECTOR).toString();
        String itemPath = dbObject.get(ContentItem.PATH).toString();
        return new ContentItem(itemPath, selector, content);
    }

}