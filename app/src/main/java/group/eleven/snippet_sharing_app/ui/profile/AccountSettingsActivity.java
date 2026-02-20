package group.eleven.snippet_sharing_app.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.databinding.ActivityAccountSettingsBinding;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;
import group.eleven.snippet_sharing_app.utils.ThemeManager;

public class AccountSettingsActivity extends AppCompatActivity {

    private ActivityAccountSettingsBinding binding;
    private ThemeManager themeManager;
    private SessionManager sessionManager;

    private boolean isGoogleConnected = false;
    private boolean isGithubConnected = false;
    private static final String PREFS_NAME = "UserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setupStatusBar();
        initManagers();
        loadSettings();
        setupToolbar();
        setupActions();
        setupCards();
        loadAppVersion();
        calculateCacheSize();
    }

    private void setupStatusBar() {
        Window window = getWindow();
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.surfaceColor, typedValue, true);
        int surfaceColor;
        if (typedValue.resourceId != 0) {
            surfaceColor = ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            surfaceColor = typedValue.data;
        }
        window.setStatusBarColor(surfaceColor);
        window.setNavigationBarColor(surfaceColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            boolean isLightBackground = isColorLight(surfaceColor);
            controller.setAppearanceLightStatusBars(isLightBackground);
            controller.setAppearanceLightNavigationBars(isLightBackground);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color)
                + 0.587 * android.graphics.Color.green(color)
                + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void initManagers() {
        themeManager = ThemeManager.getInstance(this);
        sessionManager = new SessionManager(this);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load theme setting
        boolean isDarkMode = themeManager.getThemeMode() == ThemeManager.MODE_DARK;
        binding.swDarkMode.setChecked(isDarkMode);

        // Load privacy settings
        binding.swPublicProfile.setChecked(prefs.getBoolean("public_profile", true));
        binding.swShowEmail.setChecked(prefs.getBoolean("show_email", false));

        // Load 2FA setting
        binding.swTwoFactor.setChecked(prefs.getBoolean("two_factor_enabled", false));

        // Load notification settings
        binding.swPushNotifications.setChecked(prefs.getBoolean("push_notifications", true));
        binding.swEmailNotifications.setChecked(prefs.getBoolean("email_notifications", true));
        binding.swCommentNotifications.setChecked(prefs.getBoolean("comment_notifications", true));

        // Load social connection status
        isGoogleConnected = prefs.getBoolean("google_connected", false);
        isGithubConnected = prefs.getBoolean("github_connected", false);

        updateSocialUI();
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupActions() {
        // Dark Mode Toggle
        binding.swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) return;

            if (isChecked) {
                themeManager.setThemeMode(ThemeManager.MODE_DARK);
            } else {
                themeManager.setThemeMode(ThemeManager.MODE_LIGHT);
            }
            recreate();
        });

        // Privacy toggles
        binding.swPublicProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                saveSettings();
                Toast.makeText(this, isChecked ? "Profile is now public" : "Profile is now private", Toast.LENGTH_SHORT).show();
            }
        });

        binding.swShowEmail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                saveSettings();
            }
        });

        // Security toggle
        binding.swTwoFactor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                saveSettings();
                Toast.makeText(this, isChecked ? "Two-factor authentication enabled" : "Two-factor authentication disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Notification toggles
        binding.swPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) saveSettings();
        });

        binding.swEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) saveSettings();
        });

        binding.swCommentNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) saveSettings();
        });

        // Social connect buttons
        binding.btnConnectGoogle.setOnClickListener(v -> {
            isGoogleConnected = !isGoogleConnected;
            updateSocialUI();
            saveSettings();
            Toast.makeText(this, isGoogleConnected ? "Google connected" : "Google disconnected", Toast.LENGTH_SHORT).show();
        });

        binding.btnConnectGithub.setOnClickListener(v -> {
            isGithubConnected = !isGithubConnected;
            updateSocialUI();
            saveSettings();
            Toast.makeText(this, isGithubConnected ? "GitHub connected" : "GitHub disconnected", Toast.LENGTH_SHORT).show();
        });

        // Account action buttons
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        binding.btnDeleteAccount.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupCards() {
        // Change Password
        binding.cardChangePassword.setOnClickListener(v -> {
            // TODO: Navigate to change password screen or show dialog
            Toast.makeText(this, "Change password coming soon", Toast.LENGTH_SHORT).show();
        });

        // Active Sessions
        binding.cardActiveSessions.setOnClickListener(v -> {
            // TODO: Navigate to active sessions screen
            Toast.makeText(this, "Active sessions coming soon", Toast.LENGTH_SHORT).show();
        });

        // Clear Cache
        binding.cardClearCache.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Clear Cache")
                    .setMessage("This will clear all cached data. Continue?")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        clearAppCache();
                        Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
                        calculateCacheSize();
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });

        // Export Data
        binding.cardExportData.setOnClickListener(v -> {
            // TODO: Implement data export
            Toast.makeText(this, "Export data coming soon", Toast.LENGTH_SHORT).show();
        });

        // Privacy Policy
        binding.cardPrivacyPolicy.setOnClickListener(v -> {
            // TODO: Open privacy policy URL or screen
            Toast.makeText(this, "Privacy policy coming soon", Toast.LENGTH_SHORT).show();
        });

        // Terms of Service
        binding.cardTermsOfService.setOnClickListener(v -> {
            // TODO: Open terms of service URL or screen
            Toast.makeText(this, "Terms of service coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadAppVersion() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            binding.tvAppVersion.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            binding.tvAppVersion.setText("1.0.0");
        }
    }

    private void calculateCacheSize() {
        long cacheSize = getDirSize(getCacheDir());
        if (getExternalCacheDir() != null) {
            cacheSize += getDirSize(getExternalCacheDir());
        }
        String formattedSize = formatFileSize(cacheSize);
        binding.tvCacheSize.setText(formattedSize);
    }

    private long getDirSize(File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += getDirSize(file);
                    }
                }
            }
        }
        return size;
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }

    private void clearAppCache() {
        try {
            deleteDir(getCacheDir());
            if (getExternalCacheDir() != null) {
                deleteDir(getExternalCacheDir());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    private void updateSocialUI() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        binding.btnConnectGoogle.setText(isGoogleConnected ? getString(R.string.disconnect) : getString(R.string.connect));
        String email = prefs.getString("email", "");
        binding.tvGoogleEmail.setText(isGoogleConnected && !email.isEmpty() ? email : getString(R.string.not_connected));

        binding.btnConnectGithub.setText(isGithubConnected ? getString(R.string.disconnect) : getString(R.string.connect));
        String username = prefs.getString("username", "");
        binding.tvGithubUsername.setText(isGithubConnected && !username.isEmpty() ? "@" + username : getString(R.string.not_connected));
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
        sessionManager.clearAll();
        Toast.makeText(this, R.string.profile_logout_success, Toast.LENGTH_SHORT).show();

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

        String imagePath = prefs.getString("profile_image_path", null);
        prefs.edit().clear().apply();
        sessionManager.clearAll();

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

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        // Privacy
        editor.putBoolean("public_profile", binding.swPublicProfile.isChecked());
        editor.putBoolean("show_email", binding.swShowEmail.isChecked());

        // Security
        editor.putBoolean("two_factor_enabled", binding.swTwoFactor.isChecked());

        // Notifications
        editor.putBoolean("push_notifications", binding.swPushNotifications.isChecked());
        editor.putBoolean("email_notifications", binding.swEmailNotifications.isChecked());
        editor.putBoolean("comment_notifications", binding.swCommentNotifications.isChecked());

        // Social
        editor.putBoolean("google_connected", isGoogleConnected);
        editor.putBoolean("github_connected", isGithubConnected);

        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
