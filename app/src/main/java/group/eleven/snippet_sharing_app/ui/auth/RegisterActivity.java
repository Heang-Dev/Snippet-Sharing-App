package group.eleven.snippet_sharing_app.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.AuthResponse;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityRegisterBinding;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;

import java.util.regex.Pattern;

/**
 * Register Activity - handles user registration
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthRepository authRepository;

    // Username regex pattern
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authRepository = new AuthRepository(this);

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Register button
        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        // Sign in link
        binding.tvSignIn.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        // Reset errors
        binding.tilUsername.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim().toLowerCase();
        String password = binding.etPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        // Validate inputs
        boolean hasError = false;

        // Username validation
        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError(getString(R.string.validation_required));
            hasError = true;
        } else if (username.length() < 3 || username.length() > 30) {
            binding.tilUsername.setError(getString(R.string.register_error_username));
            hasError = true;
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            binding.tilUsername.setError(getString(R.string.validation_username_invalid));
            hasError = true;
        }

        // Email validation
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.validation_required));
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.validation_email_invalid));
            hasError = true;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.validation_required));
            hasError = true;
        } else if (password.length() < 8) {
            binding.tilPassword.setError(getString(R.string.validation_password_short));
            hasError = true;
        }

        // Confirm password validation
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.validation_required));
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.validation_passwords_not_match));
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Show loading state
        setLoading(true);

        // Get device name for token
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;

        // Call API
        authRepository.register(username, email, password, confirmPassword, deviceName)
                .observe(this, resource -> {
                    if (resource.isLoading()) {
                        return;
                    }

                    setLoading(false);

                    if (resource.isSuccess()) {
                        AuthResponse response = resource.getData();
                        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    } else {
                        showError(resource.getMessage());
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        binding.btnRegister.setEnabled(!isLoading);
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setText(isLoading ? "" : getString(R.string.register_button));
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.error))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
