package statik.content;

public class ContentItem {

    private final String path;
    private final String selector;
    private final String content;
    public static final String IS_COPY = "isCopy";
    public static final String PATH = "path";
    public static final String SELECTOR = "selector";
    public static final String CONTENT = "content";
    private final boolean isCopy;

    public ContentItem(String path, String selector, String content, boolean isCopy) {
        this.path = path;
        this.selector = selector;
        this.content = content;
        this.isCopy = isCopy;
    }

    public ContentItem(String path, String selector, String content) {
        this(path, selector, content, false);
    }

    public boolean isCopy() {
        return this.isCopy;
    }

    public String content() {
        return this.content;
    }

    public String selector() {
        return this.selector;
    }

    public String path() {
        return this.path;
    }

    public long size() {
        return this.content.length();
    }

}
