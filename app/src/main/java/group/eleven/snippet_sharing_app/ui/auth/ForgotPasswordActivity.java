package group.eleven.snippet_sharing_app.ui.auth;

import android.content.Intent;
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
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityForgotPasswordBinding;

/**
 * Forgot Password Activity - sends OTP to user's email
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
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

        // Send code button
        binding.btnSendCode.setOnClickListener(v -> sendResetCode());

        // Back to login link
        binding.tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void sendResetCode() {
        // Reset errors
        binding.tilEmail.setError(null);

        String email = binding.etEmail.getText().toString().trim().toLowerCase();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.validation_required));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.validation_email_invalid));
            return;
        }

        // Show loading state
        setLoading(true);

        // Call API
        authRepository.forgotPassword(email).observe(this, resource -> {
            if (resource.isLoading()) {
                return;
            }

            setLoading(false);

            if (resource.isSuccess()) {
                Toast.makeText(this, getString(R.string.forgot_password_success), Toast.LENGTH_SHORT).show();

                // Navigate to OTP verification
                Intent intent = new Intent(this, OtpVerificationActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("token", resource.getData().getToken());
                startActivity(intent);
            } else {
                showError(resource.getMessage());
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.btnSendCode.setEnabled(!isLoading);
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSendCode.setText(isLoading ? "" : getString(R.string.forgot_password_button));
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.error))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
