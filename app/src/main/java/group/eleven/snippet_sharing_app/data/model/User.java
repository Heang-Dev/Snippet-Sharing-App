package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * User model representing a user in the system
 */
public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("bio")
    private String bio;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("website_url")
    private String websiteUrl;

    @SerializedName("github_url")
    private String githubUrl;

    @SerializedName("twitter_url")
    private String twitterUrl;

    @SerializedName("is_admin")
    private boolean isAdmin;

    @SerializedName("email_verified_at")
    private String emailVerifiedAt;

    @SerializedName("last_login_at")
    private String lastLoginAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public String getLastLoginAt() {
        return lastLoginAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public boolean isEmailVerified() {
        return emailVerifiedAt != null && !emailVerifiedAt.isEmpty();
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public void setLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
