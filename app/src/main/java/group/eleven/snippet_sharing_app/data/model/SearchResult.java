package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Search result response model
 */
public class SearchResult {
    @SerializedName("snippets")
    private List<Snippet> snippets;

    @SerializedName("users")
    private List<User> users;

    @SerializedName("total_snippets")
    private int totalSnippets;

    @SerializedName("total_users")
    private int totalUsers;

    // Getters
    public List<Snippet> getSnippets() {
        return snippets;
    }

    public List<User> getUsers() {
        return users;
    }

    public int getTotalSnippets() {
        return totalSnippets;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    // Setters
    public void setSnippets(List<Snippet> snippets) {
        this.snippets = snippets;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setTotalSnippets(int totalSnippets) {
        this.totalSnippets = totalSnippets;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }
}
