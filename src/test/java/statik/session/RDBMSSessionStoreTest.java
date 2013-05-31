package statik.session;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RDBMSSessionStoreTest {

    private SessionStore store;

    @Before
    public void setUp() {
        this.store = new RDBMSSessionStore();
        store.configure("rdbms-config.properties");
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
        assertFalse("Should not have a session", store.hasSession(sessionId));
    }


    @Test
    public void getUsernameForSession() {
        String sessionId = store.createSession("bob");
        assertEquals("Should have the correct username", "bob", store.usernameFor(sessionId));
    }


    @Test
    public void deletesAllSessions() {
        String bobSessionId = store.createSession("bob");
        String fredSessionId = store.createSession("fred");
        assertTrue(store.hasSession(bobSessionId));
        assertTrue(store.hasSession(fredSessionId));

        store.deleteAllSessions();

        assertFalse(store.hasSession(bobSessionId));
        assertFalse(store.hasSession(fredSessionId));
    }

}
