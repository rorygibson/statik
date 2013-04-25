package statik.session;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InMemorySessionStoreTest {

    @Test
    public void createsSession() {
        SessionStore store = new InMemorySessionStore();
        String sessionId = store.createSession("bob");
        assertNotNull(sessionId);
    }

    @Test
    public void hangsOnToSession() {
        SessionStore store = new InMemorySessionStore();
        String sessionId = store.createSession("bob");
        assertTrue("Should have a session", store.hasSession(sessionId));
    }


    @Test
    public void deletesSession() {
        SessionStore store = new InMemorySessionStore();
        String sessionId = store.createSession("bob");
        store.deleteSession(sessionId);
        assertFalse("Should not have a session", store.hasSession("bob"));
    }

}
