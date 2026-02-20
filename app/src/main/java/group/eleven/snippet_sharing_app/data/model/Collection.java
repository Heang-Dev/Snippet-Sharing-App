package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Collection model for snippet collections
 * Matches backend Collection model
 */
public class Collection {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("description")
    private String description;

    @SerializedName("is_public")
    private boolean isPublic;

    @SerializedName("cover_image")
    private String coverImage;

    @SerializedName("snippets_count")
    private int snippetsCount;

    @SerializedName("views_count")
    private int viewsCount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Nested user object
    @SerializedName("user")
    private User user;

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public int getSnippetsCount() {
        return snippetsCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void setSnippetsCount(int snippetsCount) {
        this.snippetsCount = snippetsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
