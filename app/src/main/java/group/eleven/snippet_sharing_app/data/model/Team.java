package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

public class Team {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("member_count")
    private int memberCount;

    @SerializedName("snippet_count")
    private int snippetCount;

    @SerializedName("privacy")
    private String privacy; // e.g., "public", "private", "invite-only"

    @SerializedName("owner_id")
    private String ownerId;

    @SerializedName("user_role") // Role of the current user in this team
    private String userRole; // e.g., "owner", "admin", "member"

    // Constructors
    public Team(String id, String name, String description, String avatarUrl, int memberCount, int snippetCount, String privacy, String ownerId, String userRole) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.avatarUrl = avatarUrl;
        this.memberCount = memberCount;
        this.snippetCount = snippetCount;
        this.privacy = privacy;
        this.ownerId = ownerId;
        this.userRole = userRole;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getSnippetCount() {
        return snippetCount;
    }

    public void setSnippetCount(int snippetCount) {
        this.snippetCount = snippetCount;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
