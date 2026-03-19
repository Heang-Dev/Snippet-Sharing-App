package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TeamSnippet {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("team_id")
    private String teamId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("code")
    private String code;

    // Uses the same SnippetLanguage class + deserializer registered in ApiClient
    @SerializedName("language")
    private Snippet.SnippetLanguage language;

    @SerializedName("privacy")
    private String privacy;

    @SerializedName("tags")
    private List<Snippet.SnippetTag> tags;

    @SerializedName("user")
    private Snippet.SnippetUser user;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("view_count")
    private int viewCount;

    @SerializedName("favorite_count")
    private int favoriteCount;

    @SerializedName("comment_count")
    private int commentCount;

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTeamId() { return teamId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCode() { return code; }
    public String getPrivacy() { return privacy; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public int getViewCount() { return viewCount; }
    public int getFavoriteCount() { return favoriteCount; }
    public int getCommentCount() { return commentCount; }
    public Snippet.SnippetUser getUser() { return user; }
    public List<Snippet.SnippetTag> getTags() { return tags; }

    /**
     * Get language slug string (for syntax highlighting, backward compat)
     */
    public String getLanguage() {
        return language != null ? language.getSlug() : null;
    }

    /**
     * Get language display name
     */
    public String getLanguageName() {
        return language != null ? language.getDisplayName() : "Unknown";
    }

    /**
     * Get language color from API or fallback
     */
    public String getLanguageColor() {
        return language != null ? language.getColor() : null;
    }

    /**
     * Get author username from nested user object
     */
    public String getAuthorUsername() {
        return user != null ? user.getUsername() : null;
    }

    /**
     * Get author display name
     */
    public String getAuthorName() {
        if (user != null) {
            String name = user.getFullName();
            return (name != null && !name.isEmpty()) ? name : user.getUsername();
        }
        return null;
    }

    /**
     * Get author avatar URL
     */
    public String getAuthorAvatarUrl() {
        return user != null ? user.getAvatarUrl() : null;
    }

    /**
     * Get formatted time ago string
     */
    public String getTimeAgo() {
        if (updatedAt == null) return "";
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", java.util.Locale.US);
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date date = sdf.parse(updatedAt);
            if (date != null) {
                long diff = System.currentTimeMillis() - date.getTime();
                long minutes = diff / (1000 * 60);
                long hours = diff / (1000 * 60 * 60);
                long days = diff / (1000 * 60 * 60 * 24);
                if (minutes < 1) return "just now";
                if (minutes < 60) return minutes + "m ago";
                if (hours < 24) return hours + "h ago";
                if (days < 7) return days + "d ago";
                return days / 7 + "w ago";
            }
        } catch (Exception ignored) {}
        return "";
    }
}
