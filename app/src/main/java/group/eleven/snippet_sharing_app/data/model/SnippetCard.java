package group.eleven.snippet_sharing_app.data.model;

/**
 * Model class for snippet cards (temporary until API integration)
 */
public class SnippetCard {
    private String title;
    private String languageBadge;
    private String updatedTime;
    private String codePreview;
    private String[] tags;
    private int languageColor;

    public SnippetCard(String title, String languageBadge, String updatedTime, 
                      String codePreview, String[] tags, int languageColor) {
        this.title = title;
        this.languageBadge = languageBadge;
        this.updatedTime = updatedTime;
        this.codePreview = codePreview;
        this.tags = tags;
        this.languageColor = languageColor;
    }

    public String getTitle() {
        return title;
    }

    public String getLanguageBadge() {
        return languageBadge;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public String getCodePreview() {
        return codePreview;
    }

    public String[] getTags() {
        return tags;
    }

    public int getLanguageColor() {
        return languageColor;
    }
}
