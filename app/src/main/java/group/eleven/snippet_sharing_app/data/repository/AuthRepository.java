package group.eleven.snippet_sharing_app.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.api.ApiService;
import group.eleven.snippet_sharing_app.data.model.AuthResponse;
import group.eleven.snippet_sharing_app.data.model.ErrorResponse;
import group.eleven.snippet_sharing_app.data.model.ForgotPasswordResponse;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.OtpVerifyResponse;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.model.UserResponse;
import group.eleven.snippet_sharing_app.utils.SessionManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for authentication-related operations
 */
public class AuthRepository {
    private final ApiService apiService;
    private final SessionManager sessionManager;
    private final Gson gson;

    public AuthRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
        this.sessionManager = new SessionManager(context);
        this.gson = new Gson();
    }

    /**
     * Login user
     */
    public LiveData<Resource<AuthResponse>> login(String login, String password, String deviceName) {
        MutableLiveData<Resource<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        Map<String, String> credentials = new HashMap<>();
        credentials.put("login", login);
        credentials.put("password", password);
        credentials.put("device_name", deviceName);

        apiService.login(credentials).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse authResponse = response.body();
                        if (authResponse.isSuccess()) {
                            // CRITICAL: Validate token and user before saving
                            String token = authResponse.getToken();
                            User user = authResponse.getUser();

                            if (token == null || token.isEmpty()) {
                                result.setValue(Resource.error("Login failed: No token received from server"));
                                return;
                            }

                            if (user == null) {
                                result.setValue(Resource.error("Login failed: No user data received from server"));
                                return;
                            }

                            // Save session only if we have valid data
                            boolean saved = sessionManager.createLoginSession(token, user);
                            if (saved) {
                                result.setValue(Resource.success(authResponse));
                            } else {
                                result.setValue(Resource.error("Login failed: Could not save session"));
                            }
                        } else {
                            String message = authResponse.getMessage();
                            result.setValue(Resource.error(message != null ? message : "Login failed"));
                        }
                    } else {
                        result.setValue(Resource.error(parseError(response)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result.setValue(Resource.error("Login error: " + e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });

        return result;
    }

    /**
     * Register new user
     */
    public LiveData<Resource<AuthResponse>> register(String username, String email, String password,
                                                      String passwordConfirmation, String deviceName) {
        MutableLiveData<Resource<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("password_confirmation", passwordConfirmation);
        userData.put("device_name", deviceName);

        apiService.register(userData).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        AuthResponse authResponse = response.body();
                        if (authResponse.isSuccess()) {
                            // CRITICAL: Validate token and user before saving
                            String token = authResponse.getToken();
                            User user = authResponse.getUser();

                            if (token == null || token.isEmpty()) {
                                result.setValue(Resource.error("Registration failed: No token received"));
                                return;
                            }

                            if (user == null) {
                                result.setValue(Resource.error("Registration failed: No user data received"));
                                return;
                            }

                            // Save session only if we have valid data
                            boolean saved = sessionManager.createLoginSession(token, user);
                            if (saved) {
                                result.setValue(Resource.success(authResponse));
                            } else {
                                result.setValue(Resource.error("Registration failed: Could not save session"));
                            }
                        } else {
                            String message = authResponse.getMessage();
                            result.setValue(Resource.error(message != null ? message : "Registration failed"));
                        }
                    } else {
                        result.setValue(Resource.error(parseError(response)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result.setValue(Resource.error("Registration error: " + e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });

        return result;
    }

    /**
     * Logout user
     */
    public LiveData<Resource<MessageResponse>> logout() {
        MutableLiveData<Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        apiService.logout().enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                // Clear session regardless of response
                sessionManager.logout();
                ApiClient.resetClient();

                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    // Still consider it success since we cleared local session
                    MessageResponse msg = new MessageResponse();
                    msg.setSuccess(true);
                    msg.setMessage("Logged out successfully");
                    result.setValue(Resource.success(msg));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                // Clear session anyway
                sessionManager.logout();
                ApiClient.resetClient();

                MessageResponse msg = new MessageResponse();
                msg.setSuccess(true);
                msg.setMessage("Logged out successfully");
                result.setValue(Resource.success(msg));
            }
        });

        return result;
    }

    /**
     * Request password reset OTP
     */
    public LiveData<Resource<ForgotPasswordResponse>> forgotPassword(String email) {
        MutableLiveData<Resource<ForgotPasswordResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        Map<String, String> data = new HashMap<>();
        data.put("email", email);

        apiService.forgotPassword(data).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse fpResponse = response.body();
                    if (fpResponse.isSuccess()) {
                        // Save token and email for later use
                        sessionManager.savePasswordResetData(fpResponse.getToken(), email);
                        result.setValue(Resource.success(fpResponse));
                    } else {
                        result.setValue(Resource.error(fpResponse.getMessage()));
                    }
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });

        return result;
    }

    /**
     * Verify OTP
     */
    public LiveData<Resource<OtpVerifyResponse>> verifyOtp(String email, String otp, String token) {
        MutableLiveData<Resource<OtpVerifyResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("otp", otp);
        data.put("token", token);

        apiService.verifyOtp(data).enqueue(new Callback<OtpVerifyResponse>() {
            @Override
            public void onResponse(Call<OtpVerifyResponse> call, Response<OtpVerifyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OtpVerifyResponse otpResponse = response.body();
                    if (otpResponse.isSuccess()) {
                        // Update token with reset token
                        sessionManager.savePasswordResetData(otpResponse.getResetToken(), email);
                        result.setValue(Resource.success(otpResponse));
                    } else {
                        result.setValue(Resource.error(otpResponse.getMessage()));
                    }
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<OtpVerifyResponse> call, Throwable t) {
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });

        return result;
    }

    /**
     * Resend OTP
     */
    public LiveData<Resource<ForgotPasswordResponse>> resendOtp(String email, String token) {
        MutableLiveData<Resource<ForgotPasswordResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("token", token);

        apiService.resendOtp(data).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForgotPasswordResponse fpResponse = response.body();
                    if (fpResponse.isSuccess()) {
                        // Update token
                        sessionManager.savePasswordResetData(fpResponse.getToken(), email);
                        result.setValue(Resource.success(fpResponse));
                    } else {
                        result.setValue(Resource.error(fpResponse.getMessage()));
                    }
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });

        return result;
    }

    /**
     * Reset password
     */
    public LiveData<Resource<MessageResponse>> resetPassword(String email, String token,
                                                              String password, String passwordConfirmation) {
        MutableLiveData<Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("token", token);
        data.put("password", password);
        data.put("password_confirmation", passwordConfirmation);

        apiService.resetPassword(data).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse msgResponse = response.body();
                    if (msgResponse.isSuccess()) {
                        // Clear password reset data
                        sessionManager.clearPasswordResetData();
                        result.setValue(Resource.success(msgResponse));
                    } else {
                        result.setValue(Resource.error(msgResponse.getMessage()));
                    }
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });

        return result;
    }

    /**
     * Get current user
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
                result.setValue(Resource.error(getNetworkError(t)));
            }
        });

        return result;
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

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    /**
     * Get session manager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Resource wrapper class for LiveData
     */
    public static class Resource<T> {
        public enum Status { SUCCESS, ERROR, LOADING }

        private final Status status;
        private final T data;
        private final String message;

        private Resource(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }

        public static <T> Resource<T> success(T data) {
            return new Resource<>(Status.SUCCESS, data, null);
        }

        public static <T> Resource<T> error(String message) {
            return new Resource<>(Status.ERROR, null, message);
        }

        public static <T> Resource<T> loading() {
            return new Resource<>(Status.LOADING, null, null);
        }

        public Status getStatus() {
            return status;
        }

        public T getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }

        public boolean isLoading() {
            return status == Status.LOADING;
        }

        public boolean isSuccess() {
            return status == Status.SUCCESS;
        }

        public boolean isError() {
            return status == Status.ERROR;
        }
    }
}
