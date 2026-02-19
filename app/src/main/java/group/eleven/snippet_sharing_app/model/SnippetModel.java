package group.eleven.snippet_sharing_app.model;

public class SnippetModel {
    private String id;
    private String title;
    private String language; // e.g., "JavaScript"
    private String languageColor; // e.g., "#FFD600"
    private String privacy; // "Public", "Private", "Team"
    private String version; // "v1.2.0"
    private String lastModifiedTime; // "Modified 2h ago"
    private boolean isFavorite;
    private boolean isSelected;
    private String code; // Added code content

    public SnippetModel(String id, String title, String language, String languageColor, String privacy, String version,
            String lastModifiedTime, boolean isFavorite, String code) {
        this.id = id;
        this.title = title;
        this.language = language;
        this.languageColor = languageColor;
        this.privacy = privacy;
        this.version = version;
        this.lastModifiedTime = lastModifiedTime;
        this.isFavorite = isFavorite;
        this.code = code; // Initialize code
        this.isSelected = false;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    public String getLanguageColor() {
        return languageColor;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getVersion() {
        return version;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getCode() {
        return code;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
