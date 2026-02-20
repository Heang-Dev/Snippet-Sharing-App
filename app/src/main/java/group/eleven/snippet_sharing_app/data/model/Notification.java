package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Notification model
 * Matches backend Notification model
 */
public class Notification {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("type")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("link")
    private String link;

    @SerializedName("icon")
    private String icon;

    @SerializedName("actor_id")
    private String actorId;

    @SerializedName("actor")
    private NotificationActor actor;

    @SerializedName("related_resource_type")
    private String relatedResourceType;

    @SerializedName("related_resource_id")
    private String relatedResourceId;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("read_at")
    private String readAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    /**
     * Nested actor model for notification
     */
    public static class NotificationActor {
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
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    public String getIcon() {
        return icon;
    }

    public String getActorId() {
        return actorId;
    }

    public NotificationActor getActor() {
        return actor;
    }

    public String getRelatedResourceType() {
        return relatedResourceType;
    }

    public String getRelatedResourceId() {
        return relatedResourceId;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getReadAt() {
        return readAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public void setActor(NotificationActor actor) {
        this.actor = actor;
    }

    public void setRelatedResourceType(String relatedResourceType) {
        this.relatedResourceType = relatedResourceType;
    }

    public void setRelatedResourceId(String relatedResourceId) {
        this.relatedResourceId = relatedResourceId;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setReadAt(String readAt) {
        this.readAt = readAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
