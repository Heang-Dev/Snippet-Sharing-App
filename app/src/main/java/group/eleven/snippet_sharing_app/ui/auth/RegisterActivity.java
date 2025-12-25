package group.eleven.snippet_sharing_app.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import group.eleven.snippet_sharing_app.utils.FormValidator;

/**
 * Register Activity - handles user registration
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthRepository authRepository;
    private FormValidator formValidator;

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

        setupFormValidation();
        setupClickListeners();
    }

    private void setupFormValidation() {
        formValidator = new FormValidator()
                .addUsernameField(binding.tilUsername, binding.etUsername,
                        getString(R.string.validation_required),
                        getString(R.string.register_error_username))
                .addEmailField(binding.tilEmail, binding.etEmail,
                        getString(R.string.validation_required),
                        getString(R.string.validation_email_invalid))
                .addPasswordField(binding.tilPassword, binding.etPassword,
                        getString(R.string.validation_required),
                        getString(R.string.validation_password_short))
                .addConfirmPasswordField(binding.tilConfirmPassword, binding.etConfirmPassword,
                        binding.etPassword,
                        getString(R.string.validation_required),
                        getString(R.string.validation_passwords_not_match))
                .setSubmitButton(binding.btnRegister);
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
        // Validate all fields and show errors
        if (!formValidator.validateAll()) {
            return;
        }

        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim().toLowerCase();
        String password = binding.etPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

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
        if (isLoading) {
            binding.btnRegister.setEnabled(false);
            binding.btnRegister.setText("");
        } else {
            binding.btnRegister.setEnabled(formValidator.isFormValid());
            binding.btnRegister.setText(getString(R.string.register_button));
        }
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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
