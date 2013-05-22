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
            boolean isDefault = obj.get("default") != null && obj.get("default").toString().equalsIgnoreCase("true");
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
    public void addUser(String name, String password, boolean isDefault) {
        LOG.info("Adding user [" + name + "]");
        BasicDBObject dbObj = new BasicDBObject("username", name).append("password", password).append("default", isDefault);
        users.insert(dbObj);
    }

    @Override
    public boolean auth(String username, String password) {
        LOG.info("Authenticating user [" + username + "]");
        DBObject query = new BasicDBObject("username", username).append("password", password);
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
            String username = o.get("username").toString();
            String password = o.get("password").toString();
            boolean isDefault = o.get("default") == null ? false : Boolean.valueOf(o.get("default").toString());

            User u = new User(username, password, isDefault);
            list.add(u);
        }
        LOG.debug("Found " + list.size() + " users");
        return list;
    }

    @Override
    public void removeUser(String username) {
        LOG.info("Removing user [" + username + "]");
        DBObject query = new BasicDBObject("username", username);
        users.remove(query);
    }
}
