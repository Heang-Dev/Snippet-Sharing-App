package group.eleven.snippet_sharing_app.api;

import group.eleven.snippet_sharing_app.data.model.ApiResponse;
import group.eleven.snippet_sharing_app.data.model.AuthResponse;
import group.eleven.snippet_sharing_app.data.model.ForgotPasswordResponse;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.OtpVerifyResponse;
import group.eleven.snippet_sharing_app.data.model.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

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
}
