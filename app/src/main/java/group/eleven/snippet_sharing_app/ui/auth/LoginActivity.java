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
        Log.d(TAG, "attemptLogin: BYPASSING AUTHENTICATION FOR TESTING");

        try {
            // Create mock user for testing purposes
            User mockUser = new User();
            mockUser.setId("dev_user_123");
            mockUser.setUsername("test_developer");
            mockUser.setEmail(binding.etLogin.getText() != null ? binding.etLogin.getText().toString() : "dev@example.com");
            mockUser.setFullName("Developer Mode");

            // Save a mock session so HomeActivity doesn't redirect back to Login
            sessionManager.createLoginSession("mock_bypass_token", mockUser);

            Toast.makeText(this, "Login Bypassed (Testing Mode)", Toast.LENGTH_SHORT).show();
            
            // Navigate to Home
            navigateToHome();

        } catch (Exception e) {
            Log.e(TAG, "attemptLogin: Error during bypass", e);
            // Fallback to basic navigation
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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