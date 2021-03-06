package statik.auth;

public class User {

    private String username;
    private String password;
    private boolean isDefaultUser;

    public User(String username, String password, boolean isDefault) {
        this.username = username;
        this.password = password;
        this.isDefaultUser = isDefault;
    }

    public User(String username, String password) {
        this(username, password, false);
    }

    public User(String username) {
        this.username = username;
    }

    public boolean isAnonymous() {
        return false;
    }

    public boolean isDefault() {
        return this.isDefaultUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void updateWith(User other) {
        this.username = other.getUsername();
        this.password = other.getPassword();
        this.isDefaultUser = other.isDefault();
    }
}
