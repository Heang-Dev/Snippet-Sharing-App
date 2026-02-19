package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

import group.eleven.snippet_sharing_app.R;

/**
 * Model for activity feed items from /feed API
 */
public class FeedActivity {

    @SerializedName("type")
    private String type;

    @SerializedName("user")
    private FeedUser user;

    @SerializedName("resource_type")
    private String resourceType;

    @SerializedName("resource_id")
    private String resourceId;

    @SerializedName("resource")
    private FeedResource resource;

    @SerializedName("message")
    private String message;

    @SerializedName("created_at")
    private String createdAt;

    // Nested class for user info
    public static class FeedUser {
        @SerializedName("id")
        private String id;

        @SerializedName("username")
        private String username;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("avatar_url")
        private String avatarUrl;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getFullName() {
            return fullName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getDisplayName() {
            return fullName != null && !fullName.isEmpty() ? fullName : username;
        }
    }

    // Nested class for resource info
    public static class FeedResource {
        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("slug")
        private String slug;

        @SerializedName("description")
        private String description;

        @SerializedName("language")
        private Language language;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getSlug() {
            return slug;
        }

        public String getDescription() {
            return description;
        }

        public Language getLanguage() {
            return language;
        }
    }

    // Nested class for language info
    public static class Language {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("slug")
        private String slug;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSlug() {
            return slug;
        }
    }

    // Getters
    public String getType() {
        return type;
    }

    public FeedUser getUser() {
        return user;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public FeedResource getResource() {
        return resource;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Get user display name
     */
    public String getUserName() {
        return user != null ? user.getDisplayName() : "Unknown";
    }

    /**
     * Get resource title
     */
    public String getResourceTitle() {
        return resource != null ? resource.getTitle() : "";
    }

    /**
     * Get action verb based on activity type
     */
    public String getAction() {
        if (type == null) return "interacted with";

        switch (type) {
            case "snippet_created":
                return "created";
            case "snippet_updated":
                return "updated";
            case "snippet_forked":
                return "forked";
            case "snippet_favorited":
                return "starred";
            case "snippet_trending":
                return "is trending";
            case "comment_added":
                return "commented on";
            case "follow":
                return "followed";
            case "collection_created":
                return "created collection";
            case "team_created":
                return "created team";
            case "team_joined":
                return "joined team";
            default:
                return "interacted with";
        }
    }

    /**
     * Get icon resource ID based on activity type
     */
    public int getIconResId() {
        if (type == null) return R.drawable.ic_code;

        switch (type) {
            case "snippet_created":
            case "snippet_updated":
                return R.drawable.ic_code;
            case "snippet_forked":
                return R.drawable.ic_collections;
            case "snippet_favorited":
            case "snippet_trending":
                return R.drawable.ic_favorite;
            case "comment_added":
                return R.drawable.ic_explore;
            case "follow":
                return R.drawable.ic_person;
            case "team_created":
            case "team_joined":
                return R.drawable.ic_person;
            default:
                return R.drawable.ic_code;
        }
    }

    /**
     * Get formatted timestamp (e.g., "20 min ago")
     */
    public String getFormattedTime() {
        if (createdAt == null) return "";

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", java.util.Locale.US);
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date date = sdf.parse(createdAt);

            if (date != null) {
                long diff = System.currentTimeMillis() - date.getTime();
                long minutes = diff / (1000 * 60);
                long hours = diff / (1000 * 60 * 60);
                long days = diff / (1000 * 60 * 60 * 24);

                if (minutes < 1) return "Just now";
                if (minutes < 60) return minutes + " min ago";
                if (hours < 24) return hours + "h ago";
                if (days < 7) return days + "d ago";
                return days / 7 + "w ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return createdAt;
    }

    /**
     * Convert to ActivityFeedItem for adapter compatibility
     */
    public ActivityFeedItem toActivityFeedItem() {
        return new ActivityFeedItem(
                getUserName(),
                getAction(),
                getResourceTitle(),
                getFormattedTime(),
                getIconResId()
        );
    }
}
