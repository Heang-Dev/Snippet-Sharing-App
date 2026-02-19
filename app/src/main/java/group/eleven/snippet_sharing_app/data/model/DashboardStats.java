package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model for dashboard statistics from /feed/stats API
 */
public class DashboardStats {

    @SerializedName("snippets")
    private SnippetStats snippets;

    @SerializedName("comments")
    private CommentStats comments;

    @SerializedName("favorites_received")
    private int favoritesReceived;

    @SerializedName("followers")
    private int followers;

    @SerializedName("following")
    private int following;

    // Nested class for snippet stats
    public static class SnippetStats {
        @SerializedName("total")
        private int total;

        @SerializedName("this_week")
        private int thisWeek;

        public int getTotal() {
            return total;
        }

        public int getThisWeek() {
            return thisWeek;
        }
    }

    // Nested class for comment stats
    public static class CommentStats {
        @SerializedName("total")
        private int total;

        @SerializedName("this_week")
        private int thisWeek;

        public int getTotal() {
            return total;
        }

        public int getThisWeek() {
            return thisWeek;
        }
    }

    public SnippetStats getSnippets() {
        return snippets;
    }

    public CommentStats getComments() {
        return comments;
    }

    public int getFavoritesReceived() {
        return favoritesReceived;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    /**
     * Get total snippet count
     */
    public int getSnippetCount() {
        return snippets != null ? snippets.getTotal() : 0;
    }

    /**
     * Get formatted views count (favorites received as proxy for popularity)
     */
    public String getFormattedFavorites() {
        return formatCount(favoritesReceived);
    }

    /**
     * Get formatted followers count
     */
    public String getFormattedFollowers() {
        return formatCount(followers);
    }

    /**
     * Format large numbers with K suffix
     */
    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format("%.1fk", count / 1000.0);
        }
        return String.valueOf(count);
    }
}
