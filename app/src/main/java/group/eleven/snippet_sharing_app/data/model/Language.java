package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Language model for programming languages
 * Matches backend Language model
 */
public class Language {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("file_extensions")
    private String[] fileExtensions;

    @SerializedName("icon")
    private String icon;

    @SerializedName("color")
    private String color;

    @SerializedName("snippet_count")
    private int snippetCount;

    @SerializedName("popularity_rank")
    private Integer popularityRank;

    @SerializedName("is_active")
    private boolean isActive;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name;
    }

    public String[] getFileExtensions() {
        return fileExtensions;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public int getSnippetCount() {
        return snippetCount;
    }

    public Integer getPopularityRank() {
        return popularityRank;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setFileExtensions(String[] fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSnippetCount(int snippetCount) {
        this.snippetCount = snippetCount;
    }

    public void setPopularityRank(Integer popularityRank) {
        this.popularityRank = popularityRank;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
