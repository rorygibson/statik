package statik.content;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContentItemTest {

    @Test
    public void sizeReturnsLengthOfContentBlock() {
        ContentItem c = new ContentItem("path", "selector", "0123456789", false, false);
        assertEquals("Should return length of content field", 10, c.size());
    }

    @Test
    public void accessors() {
        ContentItem c = new ContentItem("path", "selector", "content", false, false);
        assertEquals("Path wrong", "path", c.path());
        assertEquals("Selector wrong", "selector", c.selector());
        assertEquals("Content wrong", "content", c.content());
    }
}
