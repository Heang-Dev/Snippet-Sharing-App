package group.eleven.snippet_sharing_app.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;
import group.eleven.snippet_sharing_app.utils.ThemeManager;

public class AccountSettingsActivity extends AppCompatActivity {

    private MaterialSwitch swTwoFactor, swDarkMode, swPushNotifications, swEmailNotifications;
    private ThemeManager themeManager;
    private SessionManager sessionManager;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;

    private Button btnConnectGoogle, btnConnectGithub;
    private TextView tvGoogleEmail, tvGithubUsername;
    private boolean isGoogleConnected = false;
    private boolean isGithubConnected = false;
    private static final String PREFS_NAME = "UserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        loadSettings();
        setupNavigation();
        setupActions();
    }

    private void initViews() {
        // Managers
        themeManager = ThemeManager.getInstance(this);
        sessionManager = new SessionManager(this);

        // Switches
        swTwoFactor = findViewById(R.id.swTwoFactor);
        swDarkMode = findViewById(R.id.swDarkMode);
        swPushNotifications = findViewById(R.id.swPushNotifications);
        swEmailNotifications = findViewById(R.id.swEmailNotifications);

        // Password fields (using TextInputEditText now)
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        // Social login buttons
        btnConnectGoogle = findViewById(R.id.btnConnectGoogle);
        btnConnectGithub = findViewById(R.id.btnConnectGithub);
        tvGoogleEmail = findViewById(R.id.tvGoogleEmail);
        tvGithubUsername = findViewById(R.id.tvGithubUsername);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load theme setting
        if (swDarkMode != null) {
            boolean isDarkMode = themeManager.getThemeMode() == ThemeManager.MODE_DARK;
            swDarkMode.setChecked(isDarkMode);
        }

        // Load 2FA setting
        if (swTwoFactor != null) {
            swTwoFactor.setChecked(prefs.getBoolean("two_factor_enabled", false));
        }

        // Load notification settings
        if (swPushNotifications != null) {
            swPushNotifications.setChecked(prefs.getBoolean("push_notifications", true));
        }
        if (swEmailNotifications != null) {
            swEmailNotifications.setChecked(prefs.getBoolean("email_notifications", true));
        }

        isGoogleConnected = prefs.getBoolean("google_connected", false);
        isGithubConnected = prefs.getBoolean("github_connected", false);

        updateSocialUI();
    }

    private void setupNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void setupActions() {
        // Dark Mode Toggle
        if (swDarkMode != null) {
            swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Only process if user manually changed it (not from loadSettings)
                if (!buttonView.isPressed()) return;

                if (isChecked) {
                    themeManager.setThemeMode(ThemeManager.MODE_DARK);
                } else {
                    themeManager.setThemeMode(ThemeManager.MODE_LIGHT);
                }
                // Recreate activity to apply theme change immediately
                recreate();
            });
        }

        // Save Changes Button
        if (findViewById(R.id.btnSaveChanges) != null) {
            findViewById(R.id.btnSaveChanges).setOnClickListener(v -> {
                if (validatePasswords()) {
                    saveSettings();
                    Toast.makeText(this, "All changes saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        // Logout Button
        if (findViewById(R.id.btnLogout) != null) {
            findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutConfirmation());
        }

        // Social connect buttons
        if (btnConnectGoogle != null) {
            btnConnectGoogle.setOnClickListener(v -> {
                isGoogleConnected = !isGoogleConnected;
                updateSocialUI();
                Toast.makeText(this, isGoogleConnected ? "Google connected" : "Google disconnected", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnConnectGithub != null) {
            btnConnectGithub.setOnClickListener(v -> {
                isGithubConnected = !isGithubConnected;
                updateSocialUI();
                Toast.makeText(this, isGithubConnected ? "GitHub connected" : "GitHub disconnected", Toast.LENGTH_SHORT).show();
            });
        }

        // Delete Account Button
        if (findViewById(R.id.btnDeleteAccount) != null) {
            findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> showDeleteConfirmation());
        }
    }

    private void updateSocialUI() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (btnConnectGoogle != null && tvGoogleEmail != null) {
            btnConnectGoogle.setText(isGoogleConnected ? getString(R.string.disconnect) : getString(R.string.connect));
            String email = prefs.getString("email", "");
            tvGoogleEmail.setText(isGoogleConnected && !email.isEmpty() ? email : getString(R.string.not_connected));
        }
        if (btnConnectGithub != null && tvGithubUsername != null) {
            btnConnectGithub.setText(isGithubConnected ? getString(R.string.disconnect) : getString(R.string.connect));
            String username = prefs.getString("username", "");
            tvGithubUsername.setText(isGithubConnected && !username.isEmpty() ? "@" + username : getString(R.string.not_connected));
        }
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.logout, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void performLogout() {
        // Clear session
        sessionManager.clearAll();

        Toast.makeText(this, R.string.profile_logout_success, Toast.LENGTH_SHORT).show();

        // Navigate to login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDeleteConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_account)
                .setMessage(R.string.delete_account_confirm)
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteAccount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Read image path BEFORE clearing prefs
        String imagePath = prefs.getString("profile_image_path", null);

        // Clear profile data
        prefs.edit().clear().apply();

        // Clear session data
        sessionManager.clearAll();

        // Delete profile image file
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) file.delete();
        }

        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validatePasswords() {
        if (etNewPassword == null || etConfirmPassword == null) return true;

        String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";
        String confirmPass = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        if (!newPass.isEmpty()) {
            if (newPass.length() < 6) {
                etNewPassword.setError("Password must be at least 6 characters");
                return false;
            }
            if (!newPass.equals(confirmPass)) {
                etConfirmPassword.setError("Passwords do not match");
                return false;
            }
        }
        return true;
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        if (swTwoFactor != null) {
            editor.putBoolean("two_factor_enabled", swTwoFactor.isChecked());
        }
        if (swPushNotifications != null) {
            editor.putBoolean("push_notifications", swPushNotifications.isChecked());
        }
        if (swEmailNotifications != null) {
            editor.putBoolean("email_notifications", swEmailNotifications.isChecked());
        }
        editor.putBoolean("google_connected", isGoogleConnected);
        editor.putBoolean("github_connected", isGithubConnected);
        editor.apply();
    }
}
