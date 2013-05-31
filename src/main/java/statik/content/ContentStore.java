package statik.content;

import statik.util.Language;

import java.util.Map;

public interface ContentStore {

    void insertOrUpdate(ContentItem contentItem);

    void configure(String configFilename);

    Map<String,ContentItem> findForDomainAndPath(String domain, String path, String language);

    void clearContentItems();

    ContentItem findBy(String domain, String path, String selector, Language language);

    void makeContentLiveFor(String domain, String path);
}
