package group.eleven.snippet_sharing_app.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.api.ApiService;
import group.eleven.snippet_sharing_app.data.model.ErrorResponse;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.model.UserResponse;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.utils.SessionManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for profile-related operations
 */
public class ProfileRepository {
    private final ApiService apiService;
    private final SessionManager sessionManager;
    private final Gson gson;

    public ProfileRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
        this.sessionManager = new SessionManager(context);
        this.gson = new Gson();
    }

    /**
     * Update user profile with avatar
     *
     * @param fullName   User's full name
     * @param username   User's username
     * @param bio        User's bio
     * @param websiteUrl User's website URL
     * @param githubUrl  User's GitHub URL
     * @param twitterUrl User's Twitter URL
     * @param location   User's location
     * @param avatarPath Local path to avatar image (null if no new avatar)
     */
    public LiveData<Resource<UserResponse>> updateProfile(
            String fullName,
            String username,
            String bio,
            String websiteUrl,
            String githubUrl,
            String twitterUrl,
            String location,
            String avatarPath
    ) {
        MutableLiveData<Resource<UserResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        // Check if we have an avatar to upload
        if (avatarPath != null && !avatarPath.isEmpty()) {
            // Use multipart request with avatar
            updateProfileWithAvatar(result, fullName, username, bio, websiteUrl, githubUrl, twitterUrl, location, avatarPath);
        } else {
            // Use regular PUT request without avatar
            updateProfileWithoutAvatar(result, fullName, username, bio, websiteUrl, githubUrl, twitterUrl, location);
        }

        return result;
    }

    /**
     * Update profile with avatar using multipart form data
     */
    private void updateProfileWithAvatar(
            MutableLiveData<Resource<UserResponse>> result,
            String fullName,
            String username,
            String bio,
            String websiteUrl,
            String githubUrl,
            String twitterUrl,
            String location,
            String avatarPath
    ) {
        File avatarFile = new File(avatarPath);
        if (!avatarFile.exists()) {
            result.setValue(Resource.error("Avatar file not found"));
            return;
        }

        // Create multipart body for avatar
        RequestBody avatarBody = RequestBody.create(MediaType.parse("image/*"), avatarFile);
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", avatarFile.getName(), avatarBody);

        // Create request body map for profile data
        Map<String, RequestBody> profileData = new HashMap<>();
        profileData.put("full_name", createPartFromString(fullName));
        profileData.put("username", createPartFromString(username));
        profileData.put("bio", createPartFromString(bio != null ? bio : ""));
        profileData.put("website_url", createPartFromString(websiteUrl != null ? websiteUrl : ""));
        profileData.put("github_url", createPartFromString(githubUrl != null ? githubUrl : ""));
        profileData.put("twitter_url", createPartFromString(twitterUrl != null ? twitterUrl : ""));
        profileData.put("location", createPartFromString(location != null ? location : ""));
        // Add _method field to simulate PUT request
        profileData.put("_method", createPartFromString("PUT"));

        apiService.updateProfileWithAvatar(avatarPart, profileData).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                handleProfileUpdateResponse(result, response);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                t.printStackTrace();
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });
    }

    /**
     * Update profile without avatar using PUT request
     */
    private void updateProfileWithoutAvatar(
            MutableLiveData<Resource<UserResponse>> result,
            String fullName,
            String username,
            String bio,
            String websiteUrl,
            String githubUrl,
            String twitterUrl,
            String location
    ) {
        Map<String, String> profileData = new HashMap<>();
        profileData.put("full_name", fullName);
        profileData.put("username", username);
        if (bio != null) profileData.put("bio", bio);
        if (websiteUrl != null) profileData.put("website_url", websiteUrl);
        if (githubUrl != null) profileData.put("github_url", githubUrl);
        if (twitterUrl != null) profileData.put("twitter_url", twitterUrl);
        if (location != null) profileData.put("location", location);

        apiService.updateProfile(profileData).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                handleProfileUpdateResponse(result, response);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                t.printStackTrace();
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });
    }

    /**
     * Handle profile update response
     */
    private void handleProfileUpdateResponse(MutableLiveData<Resource<UserResponse>> result, Response<UserResponse> response) {
        try {
            if (response.isSuccessful() && response.body() != null) {
                UserResponse userResponse = response.body();
                if (userResponse.isSuccess()) {
                    // Update user in session
                    User user = userResponse.getUser();
                    if (user != null) {
                        sessionManager.updateUser(user);
                    }
                    result.setValue(Resource.success(userResponse));
                } else {
                    String message = userResponse.getMessage();
                    result.setValue(Resource.error(message != null ? message : "Profile update failed"));
                }
            } else {
                result.setValue(Resource.error(parseError(response)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setValue(Resource.error("Error updating profile: " + e.getMessage()));
        }
    }

    /**
     * Get current user profile from API
     */
    public LiveData<Resource<UserResponse>> getCurrentUser() {
        MutableLiveData<Resource<UserResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.getCurrentUser().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if (userResponse.isSuccess()) {
                        // Update user in session
                        sessionManager.updateUser(userResponse.getUser());
                        result.setValue(Resource.success(userResponse));
                    } else {
                        result.setValue(Resource.error(userResponse.getMessage()));
                    }
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                t.printStackTrace();
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });

        return result;
    }

    /**
     * Helper to create RequestBody from string
     */
    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    /**
     * Helper method to parse error response
     */
    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                ErrorResponse errorResponse = gson.fromJson(
                        response.errorBody().string(),
                        ErrorResponse.class
                );
                return errorResponse.getFirstError();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "An error occurred. Please try again.";
    }

    /**
     * Helper method to get network error message
     */
    private String getNetworkError(Throwable t) {
        if (t instanceof IOException) {
            return "Network error. Please check your connection.";
        }
        return "An error occurred. Please try again.";
    }
}
