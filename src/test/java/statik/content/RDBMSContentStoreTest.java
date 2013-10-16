package statik.content;

import org.junit.Before;
import org.junit.Test;
import statik.util.Language;

import java.util.Map;

import static com.mongodb.util.MyAsserts.assertTrue;
import static org.junit.Assert.assertEquals;

public class RDBMSContentStoreTest {

    private RDBMSContentStore store;


    @Before
    public void setUp() {
        store = new RDBMSContentStore();
        store.configure("rdbms-config.properties");
        store.clearContentItems();
    }

    @Test
    public void persistsLanguageAttribute() {
        ContentItem c = new ContentItem("domain", "/path", "selector", "content", false, false, Language.French, null);

        store.insertOrUpdate(c);
        ContentItem contentItem = store.findBy("domain", "/path", "selector", Language.French);
        assertEquals("Should have correct language", Language.French, contentItem.language());
    }

    @Test
    public void insertsNewContentItem() {
        assertEquals("Should have no content items", 0, store.findForDomainAndPath("domain", "/path", Language.Default.code()).size());
        ContentItem c = new ContentItem("domain", "/path", "selector", "content", false ,null);

        store.insertOrUpdate(c);

        assertEquals("Should now have an item", 1, store.findForDomainAndPath("domain", "/path", Language.Default.code()).size());
    }

    @Test
    public void clearsAllContentItems() {
        int expected = insertSomeContent();
        assertEquals("Should have multiple content items", expected, store.findForDomainAndPath("domain", "/path", Language.Default.code()).size());

        store.clearContentItems();

        assertEquals("Should have NO content items", 0, store.findForDomainAndPath("domain", "/path", Language.Default.code()).size());
    }

    @Test
    public void updatesExistingContentItem() {
        ContentItem item = new ContentItem("domain", "/path", "p", "this is the content", false, null);
        store.insertOrUpdate(item);

        ContentItem updated = new ContentItem("domain", "/path", "p", "this is the UPDATED content", false, null);
        store.insertOrUpdate(updated);

        Map<String, ContentItem> items = store.findForDomainAndPath("domain", "/path", Language.Default.code());
        assertEquals("Should have only found one content item for domain and /path", 1, items.size());
        ContentItem found = items.get("p");
        assertEquals("Content should have been updated", "this is the UPDATED content", found.content());
    }

    @Test
    public void findsContentForDomainPathAndSelector() {
        store.insertOrUpdate(new ContentItem("domain", "/path", "html > body > p", "this is the content", false, null));
        store.insertOrUpdate(new ContentItem("domain", "/path", "html > body > div > span", "this is the other bit of content", false, null));

        ContentItem found = store.findBy("domain", "/path", "html > body > p", Language.Default);
        assertEquals("Should have returned the correct bit of content", "this is the content", found.content());
    }

    @Test
    public void makesContentLive() {
        store.insertOrUpdate(new ContentItem("domain", "/path", "html > body > p.1", "this is the content 1", false, null));
        store.insertOrUpdate(new ContentItem("domain", "/path", "html > body > p.2", "this is the content 2", false, null));

        store.makeContentLiveFor("domain", "/path");
        Map<String,ContentItem> content = store.findForDomainAndPath("domain", "/path", Language.Default.code());

        assertTrue(content.get("html > body > p.1").live());
        assertTrue(content.get("html > body > p.2").live());
    }

    private int insertSomeContent() {
        int i=0;
        int MAX = 5;

        for (; i<MAX; i++) {
            ContentItem c1 = new ContentItem("domain", "/path", "selector" + i, "content", false, null);
            store.insertOrUpdate(c1);
        }

        return i;
    }

}
