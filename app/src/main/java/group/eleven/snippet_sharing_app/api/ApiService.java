package group.eleven.snippet_sharing_app.api;

import group.eleven.snippet_sharing_app.data.model.ApiResponse;
import group.eleven.snippet_sharing_app.data.model.AuthResponse;
import group.eleven.snippet_sharing_app.data.model.Category;
import group.eleven.snippet_sharing_app.data.model.Comment;
import group.eleven.snippet_sharing_app.data.model.DashboardStats;
import group.eleven.snippet_sharing_app.data.model.FeedActivity;
import group.eleven.snippet_sharing_app.data.model.ForgotPasswordResponse;
import group.eleven.snippet_sharing_app.data.model.Language;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.Notification;
import group.eleven.snippet_sharing_app.data.model.OtpVerifyResponse;
import group.eleven.snippet_sharing_app.data.model.SearchResult;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.model.UserResponse;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.model.TeamInvitation;
import group.eleven.snippet_sharing_app.data.model.TeamSnippet;
import group.eleven.snippet_sharing_app.data.model.TeamsResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.List;
import java.util.Map;

/**
 * Retrofit API Service interface for all API endpoints
 */
public interface ApiService {

    // ==================== Authentication ====================

    /**
     * Login with email/username and password
     */
    @POST("auth/login")
    Call<AuthResponse> login(@Body Map<String, String> credentials);

    /**
     * Register new user
     */
    @POST("auth/register")
    Call<AuthResponse> register(@Body Map<String, String> userData);

    /**
     * Logout current device
     */
    @POST("auth/logout")
    Call<MessageResponse> logout();

    /**
     * Logout all devices
     */
    @POST("auth/logout-all")
    Call<MessageResponse> logoutAll();

    // ==================== Password Reset ====================

    /**
     * Request password reset OTP
     */
    @POST("auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body Map<String, String> email);

    /**
     * Verify OTP code
     */
    @POST("auth/verify-otp")
    Call<OtpVerifyResponse> verifyOtp(@Body Map<String, String> otpData);

    /**
     * Resend OTP code
     */
    @POST("auth/resend-otp")
    Call<ForgotPasswordResponse> resendOtp(@Body Map<String, String> data);

    /**
     * Reset password with verified token
     */
    @POST("auth/reset-password")
    Call<MessageResponse> resetPassword(@Body Map<String, String> passwordData);

    // ==================== User Profile ====================

    /**
     * Get current user profile
     */
    @GET("user")
    Call<UserResponse> getCurrentUser();

    /**
     * Update user profile (text fields only)
     */
    @PUT("user")
    Call<UserResponse> updateProfile(@Body Map<String, String> profileData);

    /**
     * Update user profile with avatar (multipart)
     */
    @Multipart
    @POST("user")
    Call<UserResponse> updateProfileWithAvatar(
            @Part MultipartBody.Part avatar,
            @PartMap Map<String, RequestBody> profileData
    );

    /**
     * Update user password
     */
    @PUT("user/password")
    Call<MessageResponse> updatePassword(@Body Map<String, String> passwordData);

    /**
     * Delete user account
     */
    @DELETE("user")
    Call<MessageResponse> deleteAccount(@Body Map<String, String> password);


    // ==================== Team Management ====================

    /**
     * Get a list of teams the current user is a member of.
     */
    @GET("teams")
    Call<ApiResponse<TeamsResponse>> getMyTeams();

    /**
     * Create a new team.
     */
    @POST("teams")
    Call<ApiResponse<Team>> createTeam(@Body Map<String, String> teamData);

    /**
     * Get details for a specific team.
     */
    @GET("teams/{id}")
    Call<ApiResponse<Team>> getTeamDetails(@Path("id") String teamId);

    /**
     * Update details for a specific team.
     */
    @PUT("teams/{id}")
    Call<ApiResponse<Team>> updateTeam(@Path("id") String teamId, @Body Map<String, String> teamData);

    /**
     * Delete a specific team.
     */
    @DELETE("teams/{id}")
    Call<MessageResponse> deleteTeam(@Path("id") String teamId);

    /**
     * Invite a member to a specific team.
     */
    @POST("teams/{id}/members")
    Call<MessageResponse> inviteTeamMember(@Path("id") String teamId, @Body Map<String, String> inviteData);

    /**
     * Remove a member from a specific team.
     */
    @DELETE("teams/{id}/members/{memberId}")
    Call<MessageResponse> removeTeamMember(@Path("id") String teamId, @Path("memberId") String memberId);

    /**
     * Update a member's role in a specific team.
     */
    @PUT("teams/{id}/members/{memberId}")
    Call<MessageResponse> updateTeamMemberRole(@Path("id") String teamId, @Path("memberId") String memberId, @Body Map<String, String> roleData);

    /**
     * Get a list of members for a specific team.
     */
    @GET("teams/{id}/members")
    Call<ApiResponse<List<TeamMember>>> getTeamMembers(@Path("id") String teamId);

    /**
     * Get a list of pending invitations for the current user.
     */
    @GET("invitations")
    Call<ApiResponse<List<TeamInvitation>>> getMyTeamInvitations();

    /**
     * Accept a team invitation.
     */
    @POST("invitations/{id}/accept")
    Call<MessageResponse> acceptTeamInvitation(@Path("id") String invitationId);

    /**
     * Decline a team invitation.
     */
    @POST("invitations/{id}/decline")
    Call<MessageResponse> declineTeamInvitation(@Path("id") String invitationId);

    /**
     * Get a list of snippets for a specific team.
     */
    @GET("teams/{id}/snippets")
    Call<ApiResponse<List<TeamSnippet>>> getTeamSnippets(@Path("id") String teamId, @QueryMap Map<String, String> filters);

    /**
     * Create a new snippet for a specific team.
     */
    @POST("teams/{id}/snippets")
    Call<ApiResponse<TeamSnippet>> createTeamSnippet(@Path("id") String teamId, @Body Map<String, String> snippetData);

    /**
     * Transfer ownership of a team.
     */
    @POST("teams/{id}/transfer-ownership")
    Call<MessageResponse> transferTeamOwnership(@Path("id") String teamId, @Body Map<String, String> newOwnerData);

    /**
     * Get a list of activity feed items for a specific team.
     */
    @GET("teams/{id}/activity")
    Call<ApiResponse<List<group.eleven.snippet_sharing_app.data.model.ActivityFeedItem>>> getTeamActivity(@Path("id") String teamId);

    // ==================== Dashboard / Feed ====================

    /**
     * Get activity statistics for the authenticated user (snippets count, views, etc.)
     */
    @GET("feed/stats")
    Call<ApiResponse<DashboardStats>> getDashboardStats();

    /**
     * Get personalized activity feed (from followed users)
     */
    @GET("feed")
    Call<ApiResponse<List<FeedActivity>>> getActivityFeed(@QueryMap Map<String, String> params);

    /**
     * Get public activity feed (trending/recent)
     */
    @GET("feed/public")
    Call<ApiResponse<List<FeedActivity>>> getPublicFeed(@QueryMap Map<String, String> params);

    // ==================== Snippets ====================

    /**
     * Get current user's snippets
     */
    @GET("snippets")
    Call<ApiResponse<List<Snippet>>> getMySnippets(@QueryMap Map<String, String> params);

    /**
     * Get public/trending snippets
     */
    @GET("snippets/public")
    Call<ApiResponse<List<Snippet>>> getPublicSnippets(@QueryMap Map<String, String> params);

    /**
     * Get trending snippets
     */
    @GET("snippets/trending")
    Call<ApiResponse<List<Snippet>>> getTrendingSnippets(@QueryMap Map<String, String> params);

    /**
     * Create a new snippet
     */
    @POST("snippets")
    Call<ApiResponse<Snippet>> createSnippet(@Body Map<String, Object> snippetData);

    /**
     * Get single snippet by slug
     */
    @GET("snippets/{slug}")
    Call<ApiResponse<Snippet>> getSnippetBySlug(@Path("slug") String slug);

    /**
     * Update a snippet
     */
    @PUT("snippets/{id}")
    Call<ApiResponse<Snippet>> updateSnippet(@Path("id") String id, @Body Map<String, Object> snippetData);

    /**
     * Delete a snippet
     */
    @DELETE("snippets/{id}")
    Call<MessageResponse> deleteSnippet(@Path("id") String id);

    // ==================== Languages ====================

    /**
     * Get all programming languages
     */
    @GET("languages")
    Call<ApiResponse<List<Language>>> getLanguages();

    /**
     * Get popular programming languages
     */
    @GET("languages/popular")
    Call<ApiResponse<List<Language>>> getPopularLanguages();

    /**
     * Get language by slug
     */
    @GET("languages/{slug}")
    Call<ApiResponse<Language>> getLanguageBySlug(@Path("slug") String slug);

    /**
     * Get snippets by language
     */
    @GET("languages/{slug}/snippets")
    Call<ApiResponse<List<Snippet>>> getSnippetsByLanguage(@Path("slug") String slug, @QueryMap Map<String, String> params);

    // ==================== Categories ====================

    /**
     * Get all categories
     */
    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();

    /**
     * Get categories as tree structure
     */
    @GET("categories/tree")
    Call<ApiResponse<List<Category>>> getCategoriesTree();

    /**
     * Get category by slug
     */
    @GET("categories/{slug}")
    Call<ApiResponse<Category>> getCategoryBySlug(@Path("slug") String slug);

    /**
     * Get snippets by category
     */
    @GET("categories/{slug}/snippets")
    Call<ApiResponse<List<Snippet>>> getSnippetsByCategory(@Path("slug") String slug, @QueryMap Map<String, String> params);

    // ==================== Search ====================

    /**
     * Global search (snippets and users)
     */
    @GET("search")
    Call<ApiResponse<SearchResult>> search(@Query("q") String query, @QueryMap Map<String, String> params);

    /**
     * Search snippets only
     */
    @GET("search/snippets")
    Call<ApiResponse<List<Snippet>>> searchSnippets(@Query("q") String query, @QueryMap Map<String, String> params);

    /**
     * Search users only
     */
    @GET("search/users")
    Call<ApiResponse<List<User>>> searchUsers(@Query("q") String query, @QueryMap Map<String, String> params);

    /**
     * Autocomplete search suggestions
     */
    @GET("search/autocomplete")
    Call<ApiResponse<List<String>>> searchAutocomplete(@Query("q") String query);

    // ==================== Notifications ====================

    /**
     * Get user notifications
     */
    @GET("notifications")
    Call<ApiResponse<List<Notification>>> getNotifications(@QueryMap Map<String, String> params);

    /**
     * Get unread notification count
     */
    @GET("notifications/unread-count")
    Call<ApiResponse<Integer>> getUnreadNotificationCount();

    /**
     * Mark notification as read
     */
    @POST("notifications/{id}/read")
    Call<MessageResponse> markNotificationAsRead(@Path("id") String notificationId);

    /**
     * Mark all notifications as read
     */
    @POST("notifications/read-all")
    Call<MessageResponse> markAllNotificationsAsRead();

    /**
     * Delete a notification
     */
    @DELETE("notifications/{id}")
    Call<MessageResponse> deleteNotification(@Path("id") String notificationId);

    // ==================== Comments ====================

    /**
     * Get comments for a snippet
     */
    @GET("snippets/{snippetId}/comments")
    Call<ApiResponse<List<Comment>>> getSnippetComments(@Path("snippetId") String snippetId, @QueryMap Map<String, String> params);

    /**
     * Add a comment to a snippet
     */
    @POST("snippets/{snippetId}/comments")
    Call<ApiResponse<Comment>> addComment(@Path("snippetId") String snippetId, @Body Map<String, String> commentData);

    /**
     * Update a comment
     */
    @PUT("comments/{id}")
    Call<ApiResponse<Comment>> updateComment(@Path("id") String commentId, @Body Map<String, String> commentData);

    /**
     * Delete a comment
     */
    @DELETE("comments/{id}")
    Call<MessageResponse> deleteComment(@Path("id") String commentId);

    /**
     * Like a comment
     */
    @POST("comments/{id}/like")
    Call<MessageResponse> likeComment(@Path("id") String commentId);

    /**
     * Unlike a comment
     */
    @DELETE("comments/{id}/like")
    Call<MessageResponse> unlikeComment(@Path("id") String commentId);

    // ==================== Favorites ====================

    /**
     * Get user's favorite snippets
     */
    @GET("snippets/favorites")
    Call<ApiResponse<List<Snippet>>> getFavoriteSnippets(@QueryMap Map<String, String> params);

    /**
     * Add snippet to favorites
     */
    @POST("snippets/{id}/favorite")
    Call<MessageResponse> addToFavorites(@Path("id") String snippetId);

    /**
     * Remove snippet from favorites
     */
    @DELETE("snippets/{id}/favorite")
    Call<MessageResponse> removeFromFavorites(@Path("id") String snippetId);

    // ==================== Likes ====================

    /**
     * Like a snippet
     */
    @POST("snippets/{id}/like")
    Call<MessageResponse> likeSnippet(@Path("id") String snippetId);

    /**
     * Unlike a snippet
     */
    @DELETE("snippets/{id}/like")
    Call<MessageResponse> unlikeSnippet(@Path("id") String snippetId);

    // ==================== User Public Profiles ====================

    /**
     * Get public user profile by username
     */
    @GET("users/{username}")
    Call<ApiResponse<User>> getUserProfile(@Path("username") String username);

    /**
     * Get public snippets of a user
     */
    @GET("users/{username}/snippets")
    Call<ApiResponse<List<Snippet>>> getUserSnippets(@Path("username") String username, @QueryMap Map<String, String> params);

    /**
     * Get followers of a user
     */
    @GET("users/{username}/followers")
    Call<ApiResponse<List<User>>> getUserFollowers(@Path("username") String username, @QueryMap Map<String, String> params);

    /**
     * Get users that a user is following
     */
    @GET("users/{username}/following")
    Call<ApiResponse<List<User>>> getUserFollowing(@Path("username") String username, @QueryMap Map<String, String> params);

    // ==================== Follow System ====================

    /**
     * Follow a user
     */
    @POST("users/{username}/follow")
    Call<MessageResponse> followUser(@Path("username") String username);

    /**
     * Unfollow a user
     */
    @DELETE("users/{username}/follow")
    Call<MessageResponse> unfollowUser(@Path("username") String username);

    /**
     * Check if following a user
     */
    @GET("users/{username}/is-following")
    Call<ApiResponse<Boolean>> isFollowingUser(@Path("username") String username);

    // ==================== Sharing ====================

    /**
     * Get snippets shared with me
     */
    @GET("shares/with-me")
    Call<ApiResponse<List<Snippet>>> getSharedWithMe(@QueryMap Map<String, String> params);

    /**
     * Get snippets I shared with others
     */
    @GET("shares/by-me")
    Call<ApiResponse<List<Snippet>>> getSharedByMe(@QueryMap Map<String, String> params);

    /**
     * Share a snippet with users
     */
    @POST("snippets/{id}/share")
    Call<MessageResponse> shareSnippet(@Path("id") String snippetId, @Body Map<String, Object> shareData);

    // ==================== Collections ====================

    /**
     * Get user's collections
     */
    @GET("collections")
    Call<ApiResponse<List<group.eleven.snippet_sharing_app.data.model.Collection>>> getCollections();

    /**
     * Create a new collection
     */
    @POST("collections")
    Call<ApiResponse<group.eleven.snippet_sharing_app.data.model.Collection>> createCollection(@Body Map<String, String> collectionData);

    /**
     * Get collection by ID
     */
    @GET("collections/{id}")
    Call<ApiResponse<group.eleven.snippet_sharing_app.data.model.Collection>> getCollection(@Path("id") String collectionId);

    /**
     * Update a collection
     */
    @PUT("collections/{id}")
    Call<ApiResponse<group.eleven.snippet_sharing_app.data.model.Collection>> updateCollection(@Path("id") String collectionId, @Body Map<String, String> collectionData);

    /**
     * Delete a collection
     */
    @DELETE("collections/{id}")
    Call<MessageResponse> deleteCollection(@Path("id") String collectionId);

    /**
     * Get snippets in a collection
     */
    @GET("collections/{id}/snippets")
    Call<ApiResponse<List<Snippet>>> getCollectionSnippets(@Path("id") String collectionId, @QueryMap Map<String, String> params);

    /**
     * Add snippet to collection
     */
    @POST("collections/{id}/snippets")
    Call<MessageResponse> addSnippetToCollection(@Path("id") String collectionId, @Body Map<String, String> snippetData);

    /**
     * Remove snippet from collection
     */
    @DELETE("collections/{collectionId}/snippets/{snippetId}")
    Call<MessageResponse> removeSnippetFromCollection(@Path("collectionId") String collectionId, @Path("snippetId") String snippetId);
}