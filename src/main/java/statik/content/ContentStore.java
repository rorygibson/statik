package statik.content;

import java.util.Map;

public interface ContentStore {
    boolean isEmpty();

    void insertOrUpdate(ContentItem contentItem);

    void configure(String configFilename);

    Map<String,ContentItem> findForPath(String path);

    void clearContentItems();

    ContentItem findByPathAndSelector(String path, String selector);
}
