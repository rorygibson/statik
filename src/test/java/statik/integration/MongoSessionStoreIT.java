package statik.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import statik.session.MongoSessionStore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

@Ignore
public class MongoSessionStoreIT {

    private MongoSessionStore store;

    @Before
    public void setUp() {
        this.store = new MongoSessionStore();
        this.store.configure("mongo-config.properties");
    }

    @After
    public void tearDown() {
        this.store.deleteAllSessions();
    }

    @Test
    public void createsSession() {
        String sessionId = store.createSession("bob");
        assertNotNull(sessionId);
    }

    @Test
    public void hangsOnToSession() {
        String sessionId = store.createSession("bob");
        assertTrue("Should have a session", store.hasSession(sessionId));
    }


    @Test
    public void deletesSession() {
        String sessionId = store.createSession("bob");
        store.deleteSession(sessionId);
        assertFalse("Should not have a session", store.hasSession("bob"));
    }

}
