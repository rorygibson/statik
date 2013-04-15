package statik;

import org.junit.Test;
import statik.SessionStore;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SessionStoreTest {

    @Test
    public void createsSession() {
        SessionStore store = new SessionStore();
        String sessionId = store.createSession("bob");
        assertNotNull(sessionId);
    }

    @Test
    public void hangsOnToSession() {
        SessionStore store = new SessionStore();
        String sessionId = store.createSession("bob");
        assertTrue("Should have a session", store.hasSession(sessionId));
    }


    @Test
    public void deletesSession() {
        SessionStore store = new SessionStore();
        String sessionId = store.createSession("bob");
        store.deleteSession(sessionId);
        assertFalse("Should not have a session", store.hasSession("bob"));
    }

}
