package statik.auth;

public class AnonymousUser extends User {
    public AnonymousUser() {
        super("anonymous");
    }

    @Override
    public boolean isAnonymous() {
        return true;
    }
}
