package statik.auth;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthStoreTest {

    @Test
    public void authenticates() {
        AuthStore store = new InMemoryAuthStore();
        store.addUser("bob", "password", false);
        assertTrue("Should have authenticated", store.auth("bob", "password"));
    }

    @Test
    public void wrongPassword() {
        AuthStore store = new InMemoryAuthStore();
        store.addUser("bob", "password", false);
        assertFalse("Should not have authenticated", store.auth("bob", "not-the-password"));
    }

    @Test
    public void userDoesntExist() {
        AuthStore store = new InMemoryAuthStore();
        store.addUser("bob", "password", false);
        assertFalse("Should not have authenticated", store.auth("not-a-user", "password"));
    }

}
