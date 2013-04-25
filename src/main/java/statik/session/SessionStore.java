package statik.session;

public interface SessionStore {
    String createSession(String username);

    boolean hasSession(String sessionId);

    void deleteSession(String sessionId);

    String usernameFor(String sessionId);

    void configure(String configFilename);

    void deleteAllSessions();
}
