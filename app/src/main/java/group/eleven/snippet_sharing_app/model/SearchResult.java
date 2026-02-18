package group.eleven.snippet_sharing_app.model;

public class SearchResult {
    private String id;
    private String title;
    private String subtitle; // e.g., "Security • Middleware"
    private String language; // e.g., "TS", "Py", "Go"
    private String languageColor; // e.g., "#3178C6"
    private String codeSnippet;
    private String username; // e.g., "@dev_jane"
    private int stars;
    private int forks;
    private boolean isPrivate;
    private String timestamp;

    public SearchResult(String id, String title, String subtitle, String language, String languageColor,
            String codeSnippet, String username, int stars, int forks, boolean isPrivate, String timestamp) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.language = language;
        this.languageColor = languageColor;
        this.codeSnippet = codeSnippet;
        this.username = username;
        this.stars = stars;
        this.forks = forks;
        this.isPrivate = isPrivate;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getLanguage() {
        return language;
    }

    public String getLanguageColor() {
        return languageColor;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public String getUsername() {
        return username;
    }

    public int getStars() {
        return stars;
    }

    public int getForks() {
        return forks;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
