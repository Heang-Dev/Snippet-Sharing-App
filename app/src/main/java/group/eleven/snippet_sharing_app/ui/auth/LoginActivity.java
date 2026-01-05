package group.eleven.snippet_sharing_app.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityLoginBinding;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.utils.FormValidator;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Login Activity - handles user authentication
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private FormValidator formValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize repository and session manager
        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        // Auto-fill for development (remove in production)
        binding.etLogin.setText("chhunmengheang5@gmail.com");
        binding.etPassword.setText("12345678");

        setupFormValidation();
        setupClickListeners();
    }

    private void setupFormValidation() {
        formValidator = new FormValidator()
                .addLoginField(binding.tilLogin, binding.etLogin,
                        getString(R.string.validation_required))
                .addRequiredField(binding.tilPassword, binding.etPassword,
                        getString(R.string.validation_required))
                .setSubmitButton(binding.btnLogin);
    }

    private void setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Login button
        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        // Forgot password link
        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Sign up link
        binding.tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        Log.d(TAG, "attemptLogin: Starting login process");

        try {
            // Validate fields
            if (formValidator == null || !formValidator.validateAll()) {
                return;
            }

            String login = binding.etLogin.getText() != null
                    ? binding.etLogin.getText().toString().trim() : "";
            String password = binding.etPassword.getText() != null
                    ? binding.etPassword.getText().toString() : "";

            if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
                showError("Please fill in all fields");
                return;
            }

            // Show loading
            setLoading(true);

            String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
            Log.d(TAG, "attemptLogin: Calling API with login: " + login);

            // Call API
            authRepository.login(login, password, deviceName).observe(this, resource -> {
                try {
                    if (resource == null) {
                        setLoading(false);
                        showError("An unexpected error occurred");
                        return;
                    }

                    if (resource.isLoading()) {
                        return;
                    }

                    setLoading(false);

                    if (resource.isSuccess()) {
                        Log.d(TAG, "attemptLogin: API returned success");

                        // CRITICAL: Verify session was saved correctly with valid data
                        String savedToken = sessionManager.getAuthToken();
                        User savedUser = sessionManager.getUser();

                        Log.d(TAG, "attemptLogin: Verifying - Token: " + (savedToken != null ? "present" : "NULL"));
                        Log.d(TAG, "attemptLogin: Verifying - User: " + (savedUser != null ? savedUser.getUsername() : "NULL"));

                        if (savedToken == null || savedToken.isEmpty()) {
                            Log.e(TAG, "attemptLogin: Session verification FAILED - no token");
                            showError("Login error: Session not saved properly");
                            return;
                        }

                        if (savedUser == null) {
                            Log.e(TAG, "attemptLogin: Session verification FAILED - no user");
                            showError("Login error: User data not saved properly");
                            return;
                        }

                        if (!sessionManager.isLoggedIn()) {
                            Log.e(TAG, "attemptLogin: Session verification FAILED - not logged in");
                            showError("Login error: Session state invalid");
                            return;
                        }

                        // All checks passed - navigate to home
                        Log.d(TAG, "attemptLogin: All verifications passed - navigating to home");
                        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        navigateToHome();

                    } else {
                        String errorMessage = resource.getMessage();
                        Log.e(TAG, "attemptLogin: Login failed - " + errorMessage);
                        showError(errorMessage != null ? errorMessage : "Login failed");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "attemptLogin: CRASH in callback", e);
                    setLoading(false);
                    showError("Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "attemptLogin: CRASH", e);
            setLoading(false);
            showError("Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private void setLoading(boolean isLoading) {
        if (binding == null) return;

        if (isLoading) {
            binding.btnLogin.setEnabled(false);
            binding.btnLogin.setText("");
        } else {
            // Re-enable based on form validity
            boolean formValid = formValidator != null && formValidator.isFormValid();
            binding.btnLogin.setEnabled(formValid);
            binding.btnLogin.setText(getString(R.string.login_button));
        }
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        if (binding == null) return;

        Snackbar.make(binding.getRoot(), message != null ? message : "An error occurred", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.error))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    private void navigateToHome() {
        Log.d(TAG, "navigateToHome: Creating intent for HomeActivity");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.d(TAG, "navigateToHome: Starting HomeActivity");
        startActivity(intent);
        Log.d(TAG, "navigateToHome: Finishing LoginActivity");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
