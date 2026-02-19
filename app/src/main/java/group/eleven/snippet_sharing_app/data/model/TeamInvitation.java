package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

public class TeamInvitation {
    @SerializedName("id")
    private String id;

    @SerializedName("team_id")
    private String teamId;

    @SerializedName("team_name")
    private String teamName;

    @SerializedName("invited_by_user_id")
    private String invitedByUserId;

    @SerializedName("invited_by_username")
    private String invitedByUsername;

    @SerializedName("invited_email")
    private String invitedEmail;

    @SerializedName("status")
    private String status; // e.g., "pending", "accepted", "rejected"

    @SerializedName("created_at")
    private String createdAt;

    // Constructor
    public TeamInvitation(String id, String teamId, String teamName, String invitedByUserId, String invitedByUsername, String invitedEmail, String status, String createdAt) {
        this.id = id;
        this.teamId = teamId;
        this.teamName = teamName;
        this.invitedByUserId = invitedByUserId;
        this.invitedByUsername = invitedByUsername;
        this.invitedEmail = invitedEmail;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getInvitedByUserId() {
        return invitedByUserId;
    }

    public void setInvitedByUserId(String invitedByUserId) {
        this.invitedByUserId = invitedByUserId;
    }

    public String getInvitedByUsername() {
        return invitedByUsername;
    }

    public void setInvitedByUsername(String invitedByUsername) {
        this.invitedByUsername = invitedByUsername;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
