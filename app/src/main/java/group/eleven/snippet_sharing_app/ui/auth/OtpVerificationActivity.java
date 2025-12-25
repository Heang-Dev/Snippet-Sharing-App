package group.eleven.snippet_sharing_app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import group.eleven.snippet_sharing_app.databinding.ActivityOtpVerificationBinding;
import group.eleven.snippet_sharing_app.utils.SessionManager;

import java.util.Locale;

/**
 * OTP Verification Activity - verifies OTP code for password reset
 */
public class OtpVerificationActivity extends AppCompatActivity {

    private ActivityOtpVerificationBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    private String email;
    private String token;
    private CountDownTimer countDownTimer;
    private boolean canResend = false;

    private static final long TIMER_DURATION = 600000; // 10 minutes in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
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
            finish();
            return;
        }

        // Display email
        binding.tvEmail.setText(email);

        setupClickListeners();
        startTimer();
    }

    private void setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Verify button
        binding.btnVerify.setOnClickListener(v -> verifyOtp());

        // Resend code link
        binding.tvResendCode.setOnClickListener(v -> {
            if (canResend) {
                resendOtp();
            }
        });
    }

    private void startTimer() {
        canResend = false;
        binding.tvResendCode.setTextColor(getColor(R.color.text_hint));

        countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                String timeText = String.format(Locale.getDefault(),
                        getString(R.string.otp_expires_in),
                        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                binding.tvTimer.setText(timeText);
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("Code expired");
                canResend = true;
                binding.tvResendCode.setTextColor(getColor(R.color.primary));
            }
        }.start();
    }

    private void verifyOtp() {
        binding.tilOtp.setError(null);

        String otp = binding.etOtp.getText().toString().trim();

        if (TextUtils.isEmpty(otp)) {
            binding.tilOtp.setError(getString(R.string.validation_required));
            return;
        }

        if (otp.length() != 6) {
            binding.tilOtp.setError("Please enter a 6-digit code");
            return;
        }

        setLoading(true);

        authRepository.verifyOtp(email, otp, token).observe(this, resource -> {
            if (resource.isLoading()) {
                return;
            }

            setLoading(false);

            if (resource.isSuccess()) {
                Toast.makeText(this, getString(R.string.otp_success), Toast.LENGTH_SHORT).show();

                // Navigate to reset password
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("token", resource.getData().getResetToken());
                startActivity(intent);
                finish();
            } else {
                showError(resource.getMessage());
            }
        });
    }

    private void resendOtp() {
        setLoading(true);

        authRepository.resendOtp(email, token).observe(this, resource -> {
            if (resource.isLoading()) {
                return;
            }

            setLoading(false);

            if (resource.isSuccess()) {
                // Update token
                token = resource.getData().getToken();
                Toast.makeText(this, getString(R.string.otp_resend_success), Toast.LENGTH_SHORT).show();

                // Restart timer
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                startTimer();

                // Clear OTP field
                binding.etOtp.setText("");
            } else {
                showError(resource.getMessage());
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.btnVerify.setEnabled(!isLoading);
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnVerify.setText(isLoading ? "" : getString(R.string.otp_button));
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}
