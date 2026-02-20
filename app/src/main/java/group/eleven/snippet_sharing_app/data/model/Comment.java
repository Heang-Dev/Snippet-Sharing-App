package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for snippet comments
 */
public class Comment {

    @SerializedName("id")
    private String id;

    @SerializedName("snippet_id")
    private String snippetId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("content")
    private String content;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("likes_count")
    private int likesCount;

    @SerializedName("is_liked")
    private boolean isLiked;

    // Nested user object from API
    @SerializedName("user")
    private User user;

    // Legacy author info (for backwards compatibility)
    private String authorName;
    private String authorUsername;
    private String authorAvatar;

    // Constructors
    public Comment() {}

    public Comment(String id, String snippetId, String content, String createdAt,
                   String authorName, String authorUsername, String authorAvatar,
                   int likesCount, boolean isLiked) {
        this.id = id;
        this.snippetId = snippetId;
        this.content = content;
        this.createdAt = createdAt;
        this.authorName = authorName;
        this.authorUsername = authorUsername;
        this.authorAvatar = authorAvatar;
        this.likesCount = likesCount;
        this.isLiked = isLiked;
    }

    // Getters
    public String getId() { return id; }
    public String getSnippetId() { return snippetId; }
    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public int getLikesCount() { return likesCount; }
    public boolean isLiked() { return isLiked; }
    public User getUser() { return user; }
    public String getAuthorName() {
        if (user != null && user.getFullName() != null) return user.getFullName();
        return authorName;
    }
    public String getAuthorUsername() {
        if (user != null && user.getUsername() != null) return user.getUsername();
        return authorUsername;
    }
    public String getAuthorAvatar() {
        if (user != null && user.getAvatarUrl() != null) return user.getAvatarUrl();
        return authorAvatar;
    }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setSnippetId(String snippetId) { this.snippetId = snippetId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public void setLiked(boolean liked) { isLiked = liked; }
    public void setUser(User user) { this.user = user; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    // Helper methods
    public String getDisplayName() {
        if (authorName != null && !authorName.isEmpty()) {
            return authorName;
        }
        return authorUsername != null ? authorUsername : "Anonymous";
    }

    public String getFormattedLikes() {
        if (likesCount == 0) return "";
        if (likesCount == 1) return "1 like";
        return likesCount + " likes";
    }
}
