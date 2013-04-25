package statik.session;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import statik.UsesMongo;

import java.util.UUID;

public class MongoSessionStore extends UsesMongo implements SessionStore {

    private static final String COLLECTION_NAME = "sessions";
    public static final String SESSION_ID = "sessionId";
    public static final String USERNAME = "username";
    public static final String UNKNOWN = "<UNKNOWN>";
    private DBCollection sessions;

    @Override
    public String createSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        BasicDBObject dbObj = new BasicDBObject(SESSION_ID, sessionId).append(USERNAME, username);
        this.sessions.insert(dbObj);
        return sessionId;
    }

    @Override
    public boolean hasSession(String sessionId) {
        BasicDBObject q = new BasicDBObject(SESSION_ID, sessionId);
        DBCursor dbObjects = this.sessions.find(q);
        return dbObjects.hasNext();
    }

    @Override
    public void deleteSession(String sessionId) {
        BasicDBObject q = new BasicDBObject(SESSION_ID, sessionId);
        this.sessions.remove(q);
    }

    @Override
    public String usernameFor(String sessionId) {
        BasicDBObject q = new BasicDBObject(SESSION_ID, sessionId);
        DBObject one = this.sessions.findOne(q);
        if (one != null) {
            return one.get(USERNAME).toString();
        }
        return UNKNOWN;
    }

    @Override
    public void configure(String configFilename) {
        super.configure(configFilename);
        this.sessions = db.getCollection(COLLECTION_NAME);
    }

    @Override
    public void deleteAllSessions() {
        this.sessions.drop();
    }
}
