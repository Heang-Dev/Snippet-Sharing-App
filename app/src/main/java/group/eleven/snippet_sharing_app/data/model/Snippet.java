package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model for snippet data from /snippets API
 */
public class Snippet {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("language_id")
    private String languageId;

    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("team_id")
    private String teamId;

    @SerializedName("title")
    private String title;

    @SerializedName("slug")
    private String slug;

    @SerializedName("description")
    private String description;

    @SerializedName("code")
    private String code;

    @SerializedName("file_name")
    private String fileName;

    @SerializedName("visibility")
    private String visibility;

    @SerializedName("expires_at")
    private String expiresAt;

    @SerializedName("views_count")
    private int viewsCount;

    @SerializedName("favorites_count")
    private int favoritesCount;

    @SerializedName("comments_count")
    private int commentsCount;

    @SerializedName("forks_count")
    private int forksCount;

    @SerializedName("forked_from_id")
    private String forkedFromId;

    @SerializedName("version")
    private int version;

    @SerializedName("is_pinned")
    private boolean isPinned;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("language")
    private SnippetLanguage language;

    @SerializedName("category")
    private SnippetCategory category;

    @SerializedName("tags")
    private List<SnippetTag> tags;

    @SerializedName("user")
    private SnippetUser user;

    @SerializedName("is_favorited")
    private boolean isFavorited;

    @SerializedName("is_owner")
    private boolean isOwner;

    // Nested class for language
    public static class SnippetLanguage {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("slug")
        private String slug;

        @SerializedName("display_name")
        private String displayName;

        @SerializedName("icon")
        private String icon;

        @SerializedName("color")
        private String color;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getSlug() { return slug; }
        public String getDisplayName() { return displayName; }
        public String getIcon() { return icon; }
        public String getColor() { return color != null ? color : "#FFFFFF"; }
    }

    // Nested class for category
    public static class SnippetCategory {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("slug")
        private String slug;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        @SerializedName("color")
        private String color;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getSlug() { return slug; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        public String getColor() { return color; }
    }

    // Nested class for tags
    public static class SnippetTag {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("slug")
        private String slug;

        @SerializedName("color")
        private String color;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getSlug() { return slug; }
        public String getColor() { return color; }
    }

    // Nested class for user
    public static class SnippetUser {
        @SerializedName("id")
        private String id;

        @SerializedName("username")
        private String username;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("avatar_url")
        private String avatarUrl;

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getAvatarUrl() { return avatarUrl; }
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getLanguageId() { return languageId; }
    public String getCategoryId() { return categoryId; }
    public String getTeamId() { return teamId; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public String getCode() { return code; }
    public String getFileName() { return fileName; }
    public String getVisibility() { return visibility; }
    public String getExpiresAt() { return expiresAt; }
    public int getViewsCount() { return viewsCount; }
    public int getFavoritesCount() { return favoritesCount; }
    public int getCommentsCount() { return commentsCount; }
    public int getForksCount() { return forksCount; }
    public String getForkedFromId() { return forkedFromId; }
    public int getVersion() { return version; }
    public boolean isPinned() { return isPinned; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public SnippetLanguage getLanguage() { return language; }
    public SnippetCategory getCategory() { return category; }
    public List<SnippetTag> getTags() { return tags; }
    public SnippetUser getUser() { return user; }
    public boolean isFavorited() { return isFavorited; }
    public boolean isOwner() { return isOwner; }

    /**
     * Get language name or default
     */
    public String getLanguageName() {
        return language != null ? language.getName() : "Unknown";
    }

    /**
     * Get language color for badge
     */
    public int getLanguageColor() {
        if (language != null && language.getColor() != null) {
            try {
                return android.graphics.Color.parseColor(language.getColor());
            } catch (Exception e) {
                return android.graphics.Color.WHITE;
            }
        }
        return android.graphics.Color.WHITE;
    }

    /**
     * Get language badge text (first 2-3 chars)
     */
    public String getLanguageBadge() {
        if (language != null && language.getName() != null) {
            String name = language.getName();
            if (name.length() <= 3) return name;
            return name.substring(0, 2);
        }
        return "??";
    }

    /**
     * Get code preview (first few lines)
     */
    public String getCodePreview() {
        if (code == null || code.isEmpty()) {
            return "// No code preview";
        }
        String[] lines = code.split("\n");
        StringBuilder preview = new StringBuilder();
        for (int i = 0; i < Math.min(8, lines.length); i++) {
            preview.append(lines[i]);
            if (i < Math.min(8, lines.length) - 1) {
                preview.append("\n");
            }
        }
        return preview.toString();
    }

    /**
     * Get tag names as array
     */
    public String[] getTagNames() {
        if (tags == null || tags.isEmpty()) {
            return new String[] { visibility != null ? visibility : "private" };
        }
        String[] names = new String[Math.min(tags.size(), 3)];
        for (int i = 0; i < names.length; i++) {
            names[i] = tags.get(i).getName();
        }
        return names;
    }

    /**
     * Get formatted updated time
     */
    public String getFormattedUpdateTime() {
        if (updatedAt == null) return "";

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", java.util.Locale.US);
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date date = sdf.parse(updatedAt);

            if (date != null) {
                long diff = System.currentTimeMillis() - date.getTime();
                long minutes = diff / (1000 * 60);
                long hours = diff / (1000 * 60 * 60);
                long days = diff / (1000 * 60 * 60 * 24);

                if (minutes < 1) return "Updated just now";
                if (minutes < 60) return "Updated " + minutes + " min ago";
                if (hours < 24) return "Updated " + hours + "h ago";
                if (days < 7) return "Updated " + days + "d ago";
                return "Updated " + days / 7 + "w ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Updated recently";
    }

    /**
     * Convert to SnippetCard for adapter compatibility
     */
    public SnippetCard toSnippetCard() {
        return new SnippetCard(
                title,
                getLanguageBadge(),
                getFormattedUpdateTime(),
                getCodePreview(),
                getTagNames(),
                getLanguageColor()
        );
    }
}
