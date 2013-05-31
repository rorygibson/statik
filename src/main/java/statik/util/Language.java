package statik.util;

public enum Language {
    English("en"), Portuguese("pt"), French("fr"), Spanish("sp"), Default("en");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }

    public static Language from(String lang) {
        for (Language l : Language.values()) {
            if (l.code.equalsIgnoreCase(lang)) {
                return l;
            }
        }
        return Language.English;
    }
}
