package group.eleven.snippet_sharing_app.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import group.eleven.snippet_sharing_app.MainActivity;
import group.eleven.snippet_sharing_app.R;

import java.io.File;

public class AccountSettingsActivity extends AppCompatActivity {

    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    
    private MaterialSwitch swTwoFactor;
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private ImageView ivCurrentPasswordToggle, ivNewPasswordToggle, ivConfirmPasswordToggle;
    
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
        setupPasswordToggles();
        setupActions();
    }

    private void initViews() {
        swTwoFactor = findViewById(R.id.swTwoFactor);
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        ivCurrentPasswordToggle = findViewById(R.id.ivCurrentPasswordToggle);
        ivNewPasswordToggle = findViewById(R.id.ivNewPasswordToggle);
        ivConfirmPasswordToggle = findViewById(R.id.ivConfirmPasswordToggle);
        
        btnConnectGoogle = findViewById(R.id.btnConnectGoogle);
        btnConnectGithub = findViewById(R.id.btnConnectGithub);
        tvGoogleEmail = findViewById(R.id.tvGoogleEmail);
        tvGithubUsername = findViewById(R.id.tvGithubUsername);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (swTwoFactor != null) {
            swTwoFactor.setChecked(prefs.getBoolean("two_factor_enabled", true));
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

    private void setupPasswordToggles() {
        if (ivCurrentPasswordToggle != null && etCurrentPassword != null) {
            ivCurrentPasswordToggle.setOnClickListener(v -> {
                isCurrentPasswordVisible = togglePassword(etCurrentPassword, ivCurrentPasswordToggle, isCurrentPasswordVisible);
            });
        }

        if (ivNewPasswordToggle != null && etNewPassword != null) {
            ivNewPasswordToggle.setOnClickListener(v -> {
                isNewPasswordVisible = togglePassword(etNewPassword, ivNewPasswordToggle, isNewPasswordVisible);
            });
        }

        if (ivConfirmPasswordToggle != null && etConfirmPassword != null) {
            ivConfirmPasswordToggle.setOnClickListener(v -> {
                isConfirmPasswordVisible = togglePassword(etConfirmPassword, ivConfirmPasswordToggle, isConfirmPasswordVisible);
            });
        }
    }

    private boolean togglePassword(EditText editText, ImageView toggleIcon, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_visibility_off);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_visibility);
        }
        editText.setSelection(editText.getText().length());
        return !isVisible;
    }

    private void setupActions() {
        if (findViewById(R.id.btnSaveChanges) != null) {
            findViewById(R.id.btnSaveChanges).setOnClickListener(v -> {
                if (validatePasswords()) {
                    saveSettings();
                    Toast.makeText(this, "All changes saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

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

        if (findViewById(R.id.tvRevokeAll) != null) {
            findViewById(R.id.tvRevokeAll).setOnClickListener(v -> 
                Toast.makeText(this, "All other sessions have been revoked", Toast.LENGTH_SHORT).show()
            );
        }

        if (findViewById(R.id.btnDeleteAccount) != null) {
            findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> showDeleteConfirmation());
        }
    }

    private void updateSocialUI() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (btnConnectGoogle != null) {
            btnConnectGoogle.setText(isGoogleConnected ? "Disconnect" : "Connect");
            String email = prefs.getString("email", "alex@gmail.com");
            tvGoogleEmail.setText(isGoogleConnected ? email : "Not connected");
        }
        if (btnConnectGithub != null) {
            btnConnectGithub.setText(isGithubConnected ? "Disconnect" : "Connect");
            String username = prefs.getString("username", "@alexcodes");
            tvGithubUsername.setText(isGithubConnected ? username : "Not connected");
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account?")
                .setMessage("This will permanently delete all your profile information, snippets, and photos. This cannot be undone.")
                .setPositiveButton("DELETE", (dialog, which) -> deleteAccount())
                .setNegativeButton("CANCEL", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAccount() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().clear().apply();

        String imagePath = prefs.getString("profile_image_path", null);
        if (imagePath != null) {
            File file = new File(imagePath);
            if (file.exists()) file.delete();
        }

        Toast.makeText(this, "Account and all data deleted successfully", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validatePasswords() {
        String newPass = etNewPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();

        if (!newPass.isEmpty()) {
            if (newPass.length() < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
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
        editor.putBoolean("google_connected", isGoogleConnected);
        editor.putBoolean("github_connected", isGithubConnected);
        editor.apply();
    }
}
