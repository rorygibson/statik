package statik.content;

public class ContentItem {

    public static final String LIVE = "live";
    private final String path;
    private final String selector;
    private final String content;
    public static final String IS_COPY = "isCopy";
    public static final String PATH = "path";
    public static final String SELECTOR = "selector";
    public static final String CONTENT = "content";
    private final boolean isCopy;
    private boolean live;

    public ContentItem(String path, String selector, String content, boolean isCopy, boolean live) {
        this.path = path;
        this.selector = selector;
        this.content = content;
        this.isCopy = isCopy;
        this.live = live;
    }

    public ContentItem(String path, String selector, String content, boolean committed) {
        this(path, selector, content, false, committed);
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

    public boolean live() {
        return this.live;
    }
}
