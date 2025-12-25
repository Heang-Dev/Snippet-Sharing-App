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
import group.eleven.snippet_sharing_app.utils.FormValidator;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Reset Password Activity - allows user to set a new password
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private FormValidator formValidator;

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

        setupFormValidation();
        setupClickListeners();
    }

    private void setupFormValidation() {
        formValidator = new FormValidator()
                .addPasswordField(binding.tilPassword, binding.etPassword,
                        getString(R.string.validation_required),
                        getString(R.string.validation_password_short))
                .addConfirmPasswordField(binding.tilConfirmPassword, binding.etConfirmPassword,
                        binding.etPassword,
                        getString(R.string.validation_required),
                        getString(R.string.validation_passwords_not_match))
                .setSubmitButton(binding.btnReset);
    }

    private void setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Reset button
        binding.btnReset.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        // Validate all fields and show errors
        if (!formValidator.validateAll()) {
            return;
        }

        String password = binding.etPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

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
        if (isLoading) {
            binding.btnReset.setEnabled(false);
            binding.btnReset.setText("");
        } else {
            binding.btnReset.setEnabled(formValidator.isFormValid());
            binding.btnReset.setText(getString(R.string.reset_password_button));
        }
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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
