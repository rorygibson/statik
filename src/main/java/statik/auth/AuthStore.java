package statik.auth;

import java.util.Collection;

public interface AuthStore {
    boolean auth(String username, String password);

    Collection<User> users();

    void addUser(String name, String password, boolean isDefault);

    void removeUser(String username);

    void configure(String configFilename);

    void deleteAllUsersExceptDefault();

    void addUser(User user);
}
