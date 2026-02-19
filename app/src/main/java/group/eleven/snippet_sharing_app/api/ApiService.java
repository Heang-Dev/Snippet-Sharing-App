package group.eleven.snippet_sharing_app.api;

import group.eleven.snippet_sharing_app.data.model.ApiResponse;
import group.eleven.snippet_sharing_app.data.model.AuthResponse;
import group.eleven.snippet_sharing_app.data.model.DashboardStats;
import group.eleven.snippet_sharing_app.data.model.FeedActivity;
import group.eleven.snippet_sharing_app.data.model.ForgotPasswordResponse;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.OtpVerifyResponse;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.data.model.UserResponse;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.model.TeamInvitation;
import group.eleven.snippet_sharing_app.data.model.TeamSnippet;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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
     * Update user profile
     */
    @PUT("user")
    Call<UserResponse> updateProfile(@Body Map<String, String> profileData);

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
    Call<ApiResponse<List<Team>>> getMyTeams();

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
    @GET("teams/invitations")
    Call<ApiResponse<List<TeamInvitation>>> getMyTeamInvitations();

    /**
     * Respond to a team invitation (accept/reject).
     */
    @POST("teams/invitations/{id}/respond")
    Call<MessageResponse> respondToTeamInvitation(@Path("id") String invitationId, @Body Map<String, String> responseData);

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
}