package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

public class TeamJoinRequest {

    @SerializedName("id")
    private String id;

    @SerializedName("team_id")
    private String teamId;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status; // pending, approved, rejected

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("user")
    private Requester user;

    public static class Requester {
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

        public String getDisplayName() {
            if (fullName != null && !fullName.isEmpty()) return fullName;
            return username != null ? username : "Unknown";
        }
    }

    public String getId() { return id; }
    public String getTeamId() { return teamId; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public Requester getUser() { return user; }
}
