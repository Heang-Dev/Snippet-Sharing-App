package group.eleven.snippet_sharing_app.ui.auth;

import android.content.Intent;
import android.os.Build;
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
import group.eleven.snippet_sharing_app.data.model.AuthResponse;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityLoginBinding;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Login Activity - handles user authentication
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

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

        // Load remembered email if any
        String rememberedEmail = sessionManager.getRememberEmail();
        if (!TextUtils.isEmpty(rememberedEmail)) {
            binding.etLogin.setText(rememberedEmail);
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
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
        // Reset errors
        binding.tilLogin.setError(null);
        binding.tilPassword.setError(null);

        String login = binding.etLogin.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        // Validate inputs
        boolean hasError = false;

        if (TextUtils.isEmpty(login)) {
            binding.tilLogin.setError(getString(R.string.validation_required));
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.validation_required));
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
        authRepository.login(login, password, deviceName).observe(this, resource -> {
            if (resource.isLoading()) {
                return;
            }

            setLoading(false);

            if (resource.isSuccess()) {
                AuthResponse response = resource.getData();
                // Save email for remember me
                sessionManager.setRememberEmail(login);

                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                navigateToHome();
            } else {
                // Show error
                showError(resource.getMessage());
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.btnLogin.setEnabled(!isLoading);
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setText(isLoading ? "" : getString(R.string.login_button));
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
