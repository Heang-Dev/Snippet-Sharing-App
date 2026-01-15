package group.eleven.snippet_sharing_app.data.model;

/**
 * Model class for activity feed items
 */
public class ActivityFeedItem {
    private String userName;
    private String action;
    private String snippetName;
    private String timestamp;
    private int iconResId;

    public ActivityFeedItem(String userName, String action, String snippetName, String timestamp, int iconResId) {
        this.userName = userName;
        this.action = action;
        this.snippetName = snippetName;
        this.timestamp = timestamp;
        this.iconResId = iconResId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAction() {
        return action;
    }

    public String getSnippetName() {
        return snippetName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getDescription() {
        return userName + " " + action + " '" + snippetName + "'";
    }
}
