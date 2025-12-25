package group.eleven.snippet_sharing_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import group.eleven.snippet_sharing_app.data.model.User;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Session manager for storing and retrieving user session data securely
 */
public class SessionManager {
    private static final String PREF_NAME = "SnippetAppSession";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_REMEMBER_EMAIL = "remember_email";
    private static final String KEY_PASSWORD_RESET_TOKEN = "password_reset_token";
    private static final String KEY_PASSWORD_RESET_EMAIL = "password_reset_email";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SessionManager(Context context) {
        SharedPreferences prefs;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback to regular SharedPreferences if encryption fails
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }

        this.sharedPreferences = prefs;
        this.editor = sharedPreferences.edit();
        this.gson = new Gson();
    }

    /**
     * Save login session
     */
    public void createLoginSession(String token, User user) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
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
        editor.remove(KEY_AUTH_TOKEN);
        editor.remove(KEY_USER);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    /**
     * Save remembered email
     */
    public void setRememberEmail(String email) {
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
        editor.remove(KEY_REMEMBER_EMAIL);
        editor.apply();
    }

    /**
     * Save password reset token and email
     */
    public void savePasswordResetData(String token, String email) {
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
        editor.remove(KEY_PASSWORD_RESET_TOKEN);
        editor.remove(KEY_PASSWORD_RESET_EMAIL);
        editor.apply();
    }

    /**
     * Clear all data
     */
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}
