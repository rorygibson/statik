package statik.content;

import java.util.Map;

public interface ContentStore {

    void insertOrUpdate(ContentItem contentItem);

    void configure(String configFilename);

    Map<String,ContentItem> findForPath(String path);

    void clearContentItems();

    ContentItem findByPathAndSelector(String path, String selector);

    void makeContentLiveFor(String path);
}
