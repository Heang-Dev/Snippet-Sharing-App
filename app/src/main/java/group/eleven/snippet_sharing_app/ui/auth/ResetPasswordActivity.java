package group.eleven.snippet_sharing_app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityResetPasswordBinding;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Reset Password Activity - allows user to set a new password
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    private String email;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        // Get email and token from intent or session
        email = getIntent().getStringExtra("email");
        token = getIntent().getStringExtra("token");

        if (TextUtils.isEmpty(email)) {
            email = sessionManager.getPasswordResetEmail();
        }
        if (TextUtils.isEmpty(token)) {
            token = sessionManager.getPasswordResetToken();
        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
            Toast.makeText(this, "Invalid session. Please try again.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Reset button
        binding.btnReset.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        // Reset errors
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);

        String password = binding.etPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        // Validate inputs
        boolean hasError = false;

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.validation_required));
            hasError = true;
        } else if (password.length() < 8) {
            binding.tilPassword.setError(getString(R.string.validation_password_short));
            hasError = true;
        }

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

        setLoading(true);

        authRepository.resetPassword(email, token, password, confirmPassword).observe(this, resource -> {
            if (resource.isLoading()) {
                return;
            }

            setLoading(false);

            if (resource.isSuccess()) {
                // Clear password reset data
                sessionManager.clearPasswordResetData();

                Toast.makeText(this, getString(R.string.reset_password_success), Toast.LENGTH_LONG).show();
                navigateToLogin();
            } else {
                showError(resource.getMessage());
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.btnReset.setEnabled(!isLoading);
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnReset.setText(isLoading ? "" : getString(R.string.reset_password_button));
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.error))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
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
