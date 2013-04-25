package statik.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InMemorySessionStore implements SessionStore {

    private final Map<String,String> map = new HashMap<>();

    @Override
    public String createSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        map.put(sessionId, username);
        return sessionId;
    }

    @Override
    public boolean hasSession(String sessionId) {
        return map.containsKey(sessionId);
    }

    @Override
    public void deleteSession(String sessionId) {
        map.remove(sessionId);
    }

    @Override
    public String usernameFor(String sessionId) {
        return map.get(sessionId);
    }

    @Override
    public void configure(String configFilename) {
        // no-op
    }

    @Override
    public void deleteAllSessions() {
        this.map.clear();
    }
}
