package statik.content;

import java.util.Map;

public interface ContentStore {

    void insertOrUpdate(ContentItem contentItem);

    void configure(String configFilename);

    Map<String,ContentItem> findForDomainAndPath(String domain, String path);

    void clearContentItems();

    ContentItem findBy(String domain, String path, String selector);

    void makeContentLiveFor(String domain, String path);
}
