package group.eleven.snippet_sharing_app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import group.eleven.snippet_sharing_app.data.model.User;

import com.google.gson.Gson;

/**
 * Session manager for storing and retrieving user session data securely
 * NOTE: Temporarily using regular SharedPreferences for debugging
 */
public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "SnippetAppSession";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_REMEMBER_EMAIL = "remember_email";
    private static final String KEY_PASSWORD_RESET_TOKEN = "password_reset_token";
    private static final String KEY_PASSWORD_RESET_EMAIL = "password_reset_email";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SessionManager(Context context) {
        Log.d(TAG, "Initializing SessionManager");
        // Using regular SharedPreferences for debugging (temporarily disabled encryption)
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        Log.d(TAG, "SessionManager initialized successfully");
    }

    /**
     * Save login session
     * @return true if session was saved successfully, false if data was invalid
     */
    public boolean createLoginSession(String token, User user) {
        Log.d(TAG, "createLoginSession: Attempting to save session");

        // CRITICAL: Validate inputs before saving
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "createLoginSession: FAILED - Token is null or empty!");
            return false;
        }

        if (user == null) {
            Log.e(TAG, "createLoginSession: FAILED - User is null!");
            return false;
        }

        Log.d(TAG, "createLoginSession: Token length = " + token.length());
        Log.d(TAG, "createLoginSession: User = " + user.getUsername());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        boolean success = editor.commit();

        Log.d(TAG, "createLoginSession: Commit result = " + success);

        // Verify the save worked
        if (success) {
            String savedToken = getAuthToken();
            User savedUser = getUser();
            if (savedToken != null && savedUser != null) {
                Log.d(TAG, "createLoginSession: SUCCESS - Session saved and verified");
                return true;
            } else {
                Log.e(TAG, "createLoginSession: FAILED - Could not verify saved data");
                return false;
            }
        }

        return false;
    }

    /**
     * Get auth token
     */
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Get logged in user
     */
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    /**
     * Update user data
     */
    public void updateUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Clear session (logout)
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USER);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    /**
     * Save remembered email
     */
    public void setRememberEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_REMEMBER_EMAIL, email);
        editor.apply();
    }

    /**
     * Get remembered email
     */
    public String getRememberEmail() {
        return sharedPreferences.getString(KEY_REMEMBER_EMAIL, "");
    }

    /**
     * Clear remembered email
     */
    public void clearRememberEmail() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_REMEMBER_EMAIL);
        editor.apply();
    }

    /**
     * Save password reset token and email
     */
    public void savePasswordResetData(String token, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASSWORD_RESET_TOKEN, token);
        editor.putString(KEY_PASSWORD_RESET_EMAIL, email);
        editor.apply();
    }

    /**
     * Get password reset token
     */
    public String getPasswordResetToken() {
        return sharedPreferences.getString(KEY_PASSWORD_RESET_TOKEN, null);
    }

    /**
     * Get password reset email
     */
    public String getPasswordResetEmail() {
        return sharedPreferences.getString(KEY_PASSWORD_RESET_EMAIL, null);
    }

    /**
     * Clear password reset data
     */
    public void clearPasswordResetData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_PASSWORD_RESET_TOKEN);
        editor.remove(KEY_PASSWORD_RESET_EMAIL);
        editor.apply();
    }

    /**
     * Clear all data
     */
    public void clearAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
