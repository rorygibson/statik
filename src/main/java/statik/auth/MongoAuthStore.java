package statik.auth;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statik.UsesMongo;

import java.util.ArrayList;
import java.util.List;

public class MongoAuthStore extends UsesMongo implements AuthStore {

    private static final Logger LOG = LoggerFactory.getLogger(MongoAuthStore.class);
    public static final String COLLECTION_NAME = "users";
    public static final String DEFAULT = "default";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TRUE = "true";


    private DBCollection users;


    @Override
    public void configure(String configFilename) {
        super.configure(configFilename);
        this.users = db.getCollection(COLLECTION_NAME);
    }

    @Override
    public void deleteAllUsersExceptDefault() {
        LOG.info("Deleting all users except default (admin)");
        DBCursor cur = this.users.find();
        int ctr = 0;
        while (cur.hasNext()) {
            DBObject obj = cur.next();
            boolean isDefault = obj.get(DEFAULT) != null && obj.get(DEFAULT).toString().equalsIgnoreCase(TRUE);
            if (!isDefault) {
                users.remove(obj);
                ctr++;
            }
        }
        LOG.info("Deleted [" + ctr + "] users");
    }

    @Override
    public void addUser(User user) {
        addUser(user.getUsername(), user.getPassword(), user.isDefault());
    }

    @Override
    public User user(String username) {
        BasicDBObject query = new BasicDBObject(USERNAME, username);
        DBObject one = users.findOne(query);
        return userFrom(one);
    }

    @Override
    public void updateUser(String username, User user) {
        User originalUser = this.user(username);
        originalUser.updateWith(user);

        DBObject q = this.users.findOne(new BasicDBObject(USERNAME, username));
        this.users.update(q, dbObjFrom(originalUser));
    }

    @Override
    public void addUser(String name, String password, boolean isDefault) {
        LOG.info("Adding user [" + name + "]");
        users.insert(dbObjFrom(name, password, isDefault));
    }

    @Override
    public boolean auth(String username, String password) {
        LOG.info("Authenticating user [" + username + "]");
        DBObject query = new BasicDBObject(USERNAME, username).append(PASSWORD, password);
        DBCursor dbObjects = users.find(query);
        if (dbObjects != null && dbObjects.hasNext()) {
            LOG.info("Successfully authenticated user [" + username + "]");
            return true;
        }
        LOG.info("User [" + username + "] not found");
        return false;
    }

    @Override
    public List<User> users() {
        LOG.debug("Fetching all users");
        List<User> list = new ArrayList<>();
        DBCursor dbObjects = users.find();

        while (dbObjects.hasNext()) {
            DBObject o = dbObjects.next();
            String username = o.get(USERNAME).toString();
            String password = o.get(PASSWORD).toString();
            boolean isDefault = o.get(DEFAULT) == null ? false : Boolean.valueOf(o.get(DEFAULT).toString());

            User u = new User(username, password, isDefault);
            list.add(u);
        }
        LOG.debug("Found " + list.size() + " users");
        return list;
    }


    @Override
    public void removeUser(String username) {
        LOG.info("Removing user [" + username + "]");
        DBObject query = new BasicDBObject(USERNAME, username);
        users.remove(query);
    }

    private User userFrom(DBObject dbObj) {
        String username = dbObj.get(USERNAME).toString();
        String password = dbObj.get(PASSWORD).toString();
        boolean isDefault = dbObj.get(DEFAULT) == null ? false : Boolean.valueOf(dbObj.get(DEFAULT).toString());

        return new User(username, password, isDefault);
    }

    private BasicDBObject dbObjFrom(String name, String password, boolean isDefault) {
        return new BasicDBObject(USERNAME, name).append(PASSWORD, password).append(DEFAULT, isDefault);
    }

    private BasicDBObject dbObjFrom(User u) {
        return new BasicDBObject(USERNAME, u.getUsername()).append(PASSWORD, u.getPassword()).append(DEFAULT, u.isDefault());
    }

}
