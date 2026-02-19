package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TeamSnippet {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("code")
    private String code;

    @SerializedName("language")
    private String language;

    @SerializedName("tags")
    private List<String> tags;

    @SerializedName("is_public")
    private boolean isPublic;

    @SerializedName("author_id")
    private String authorId;

    @SerializedName("author_username")
    private String authorUsername;

    @SerializedName("team_id")
    private String teamId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Constructor
    public TeamSnippet(String id, String title, String description, String code, String language,
                       List<String> tags, boolean isPublic, String authorId, String authorUsername,
                       String teamId, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.code = code;
        this.language = language;
        this.tags = tags;
        this.isPublic = isPublic;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.teamId = teamId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
