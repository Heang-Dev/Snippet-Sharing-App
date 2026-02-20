package group.eleven.snippet_sharing_app.data.model;

/**
 * Model class for notification items
 */
public class NotificationItem {

    public static final String TYPE_FOLLOW = "follow";
    public static final String TYPE_COMMENT = "comment";
    public static final String TYPE_FORK = "fork";
    public static final String TYPE_LIKE = "like";
    public static final String TYPE_TEAM_INVITE = "team_invite";
    public static final String TYPE_MENTION = "mention";

    private String id;
    private String type;
    private String title;
    private String message;
    private String timestamp;
    private boolean isRead;
    private String actorName;
    private String actorAvatar;
    private String targetId;
    private String targetName;

    public NotificationItem(String id, String type, String title, String message,
                           String timestamp, boolean isRead, String actorName) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.actorName = actorName;
    }

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public String getActorName() { return actorName; }
    public String getActorAvatar() { return actorAvatar; }
    public String getTargetId() { return targetId; }
    public String getTargetName() { return targetName; }

    // Setters
    public void setRead(boolean read) { isRead = read; }
    public void setActorAvatar(String actorAvatar) { this.actorAvatar = actorAvatar; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    /**
     * Get icon resource based on notification type
     */
    public int getIconResId() {
        switch (type) {
            case TYPE_FOLLOW:
                return group.eleven.snippet_sharing_app.R.drawable.ic_person;
            case TYPE_COMMENT:
                return group.eleven.snippet_sharing_app.R.drawable.ic_more_horiz;
            case TYPE_FORK:
                return group.eleven.snippet_sharing_app.R.drawable.ic_git_branch;
            case TYPE_LIKE:
                return group.eleven.snippet_sharing_app.R.drawable.ic_star_filled;
            case TYPE_TEAM_INVITE:
                return group.eleven.snippet_sharing_app.R.drawable.ic_users;
            case TYPE_MENTION:
                return group.eleven.snippet_sharing_app.R.drawable.ic_at_sign;
            default:
                return group.eleven.snippet_sharing_app.R.drawable.ic_bell;
        }
    }
}
