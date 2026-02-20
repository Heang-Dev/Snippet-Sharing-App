package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    @SerializedName("parent_comment_id")
    private String parentId;

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

    @SerializedName("is_edited")
    private boolean isEdited;

    // Nested user object from API
    @SerializedName("user")
    private User user;

    // Nested replies
    @SerializedName("replies")
    private List<Comment> replies;

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
    public String getParentId() { return parentId; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public int getLikesCount() { return likesCount; }
    public boolean isLiked() { return isLiked; }
    public boolean isEdited() { return isEdited; }
    public User getUser() { return user; }
    public List<Comment> getReplies() { return replies; }

    public String getAuthorName() {
        // Priority: user.full_name > user.username > legacy authorName > "Anonymous"
        if (user != null) {
            if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                return user.getFullName();
            }
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                return user.getUsername();
            }
        }
        if (authorName != null && !authorName.isEmpty()) {
            return authorName;
        }
        return "Anonymous";
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
    public void setParentId(String parentId) { this.parentId = parentId; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public void setLiked(boolean liked) { isLiked = liked; }
    public void setEdited(boolean edited) { isEdited = edited; }
    public void setUser(User user) { this.user = user; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    // Helper methods
    public boolean isReply() {
        return parentId != null && !parentId.isEmpty();
    }

    public boolean hasReplies() {
        return replies != null && !replies.isEmpty();
    }

    public int getRepliesCount() {
        return replies != null ? replies.size() : 0;
    }

    public String getDisplayName() {
        return getAuthorName();
    }

    public String getFormattedLikes() {
        if (likesCount == 0) return "";
        if (likesCount == 1) return "1 like";
        return likesCount + " likes";
    }

    /**
     * Format the created_at timestamp to a human-readable time ago format
     */
    public String getFormattedTime() {
        if (createdAt == null || createdAt.isEmpty()) {
            return "";
        }

        try {
            // Parse ISO 8601 format: 2026-02-20T07:52:12.000000Z
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Handle both with and without microseconds
            String dateStr = createdAt;
            if (dateStr.contains(".")) {
                dateStr = dateStr.substring(0, dateStr.indexOf('.'));
            }
            if (dateStr.endsWith("Z")) {
                dateStr = dateStr.substring(0, dateStr.length() - 1);
            }

            Date date = isoFormat.parse(dateStr);
            if (date == null) return createdAt;

            long diffMs = System.currentTimeMillis() - date.getTime();
            long diffSeconds = diffMs / 1000;
            long diffMinutes = diffSeconds / 60;
            long diffHours = diffMinutes / 60;
            long diffDays = diffHours / 24;
            long diffWeeks = diffDays / 7;
            long diffMonths = diffDays / 30;
            long diffYears = diffDays / 365;

            if (diffSeconds < 60) {
                return "Just now";
            } else if (diffMinutes < 60) {
                return diffMinutes + (diffMinutes == 1 ? " min ago" : " mins ago");
            } else if (diffHours < 24) {
                return diffHours + (diffHours == 1 ? " hour ago" : " hours ago");
            } else if (diffDays < 7) {
                return diffDays + (diffDays == 1 ? " day ago" : " days ago");
            } else if (diffWeeks < 4) {
                return diffWeeks + (diffWeeks == 1 ? " week ago" : " weeks ago");
            } else if (diffMonths < 12) {
                return diffMonths + (diffMonths == 1 ? " month ago" : " months ago");
            } else {
                return diffYears + (diffYears == 1 ? " year ago" : " years ago");
            }
        } catch (Exception e) {
            // Return simplified date if parsing fails
            if (createdAt.length() >= 10) {
                return createdAt.substring(0, 10);
            }
            return createdAt;
        }
    }
}
