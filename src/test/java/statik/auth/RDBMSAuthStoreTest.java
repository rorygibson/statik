package statik.auth;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class RDBMSAuthStoreTest {
    private RDBMSAuthStore store;


    @Before
    public void setUp() {
        store = new RDBMSAuthStore();
        store.configure("rdbms-config.properties");
        store.deleteAllUsersExceptDefault();
    }

    @Test
    public void createsAdminUserIfNotFound() {
        Collection<User> users = store.users();
        assertEquals("Should have one user", 1, users.size());
        User u = users.iterator().next();
        assertEquals("Should be the admin user", "admin", u.getUsername());
    }

    @Test
    public void authenticatesUserWithPassword() {
        assertTrue("Should have authenticated", store.auth("admin", "password"));
    }

    @Test
    public void rejectsUserWithWrongUsername() {
        assertFalse("Should not authenticate", store.auth("missing", "password"));
    }

    @Test
    public void rejectsUserWithWrongPassword() {
        assertFalse("Should not authenticate", store.auth("admin", "wrong-password"));
    }

    @Test
    public void canDeleteAllUsersExceptDefault() {
        store.addUser("one", "password", false);
        assertEquals("Should have added a user (+admin)", 2, store.users().size());

        store.deleteAllUsersExceptDefault();

        assertEquals("Should have removed all but admin", 1, store.users().size());
        User remainingUser = store.users().iterator().next();
        assertEquals("Should still have the admin user", "admin", remainingUser.getUsername());
    }

    @Test
    public void retrievesAllUsers() {
        store.addUser("one", "password", false);
        store.addUser("two", "password", false);
        assertEquals("Should retrieve 3 (admin + 2) users", 3, store.users().size());
    }

    @Test
    public void retrievesUserByUsername() {
        assertEquals("Should have retrieved user", "admin", store.user("admin").getUsername());
    }

    @Test
    public void updatesUserToChangePassword() {
        User atFirst = new User("fred", "password");
        store.addUser(atFirst);

        User update = new User("fred", "new-password");
        store.updateUser("fred", update);

        assertEquals("Should have changed the password", "new-password", store.user("fred").getPassword());
    }

    @Test
    public void updatesUserToChangeUsername() {
        User atFirst = new User("fred", "password");
        store.addUser(atFirst);

        User update = new User("frederick", "password");
        store.updateUser("fred", update);

        assertFalse("Should be able to retrieve the user by their new name", store.user("frederick").isAnonymous());
    }

    @Test
    public void removesUserByUsername() {
        User user = new User("fred", "password", false);
        store.addUser(user);
        assertEquals("Should have 2 users", 2, store.users().size());
        store.removeUser("fred");
        assertEquals("Should have 1 users", 1, store.users().size());
    }

    @Test
    public void addsUserWithFullDetails() {
        store.addUser("one", "password", false);
        User one = store.user("one");
        assertEquals("Should be the right user", "one", one.getUsername());
    }

    @Test
    public void addsUserFromEntity() {
        User toInsert = new User("fred", "password");

        store.addUser(toInsert);

        User retrieved = store.user("fred");
        assertEquals("Should be the right user", "fred", retrieved.getUsername());
    }

}
