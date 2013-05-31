package statik.content;

import statik.util.Language;

public class ContentItem {

    public static final String LIVE = "is_live";
    public static final String DOMAIN = "domain";
    public static final String IS_COPY = "is_copy";
    public static final String PATH = "path";
    public static final String SELECTOR = "selector";
    public static final String CONTENT = "content";
    public static final String LANGUAGE = "language";
    private final boolean isCopy;
    private final String domain;
    private final String path;
    private final String selector;
    private final String content;
    private final Language language;
    private boolean live;

    public ContentItem(String domain, String path, String selector, String content, boolean isCopy, boolean live) {
        this(domain, path, selector, content, isCopy, live, Language.Default);
    }

    public ContentItem(String domain, String path, String selector, String content, boolean live) {
        this(domain, path, selector, content, false, live, Language.Default);
    }

    public ContentItem(String domain, String path, String selector, String content, boolean isCopy, boolean isLive, Language language) {
        this.domain = domain;
        this.path = path;
        this.selector = selector;
        this.content = content;
        this.isCopy = isCopy;
        this.live = isLive;
        this.language = language;
    }

    public Language language() {
        return this.language;
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

    public String domain() {
        return this.domain;
    }
}
