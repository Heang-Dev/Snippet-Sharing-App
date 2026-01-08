package group.eleven.snippet_sharing_app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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

    private EditText[] otpBoxes;

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

        setupOtpInputs();
        setupClickListeners();
        startTimer();

        // Initially disable verify button
        binding.btnVerify.setEnabled(false);

        // Focus on first OTP box
        binding.etOtp1.requestFocus();
    }

    private void setupOtpInputs() {
        otpBoxes = new EditText[]{
                binding.etOtp1,
                binding.etOtp2,
                binding.etOtp3,
                binding.etOtp4,
                binding.etOtp5,
                binding.etOtp6
        };

        for (int i = 0; i < otpBoxes.length; i++) {
            final int index = i;
            EditText currentBox = otpBoxes[i];

            // Add TextWatcher for auto-focus to next input
            currentBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpBoxes.length - 1) {
                        // Move to next box
                        otpBoxes[index + 1].requestFocus();
                    }
                    // Update verify button state
                    updateVerifyButtonState();
                }
            });

            // Handle backspace to move to previous input
            currentBox.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (currentBox.getText().toString().isEmpty() && index > 0) {
                        // Move to previous box and clear it
                        otpBoxes[index - 1].requestFocus();
                        otpBoxes[index - 1].setText("");
                        return true;
                    }
                }
                return false;
            });
        }
    }

    private void updateVerifyButtonState() {
        String otp = getOtpFromBoxes();
        binding.btnVerify.setEnabled(otp.length() == 6);
    }

    private String getOtpFromBoxes() {
        StringBuilder otp = new StringBuilder();
        for (EditText box : otpBoxes) {
            otp.append(box.getText().toString());
        }
        return otp.toString();
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
        String otp = getOtpFromBoxes();

        // Validate OTP length
        if (otp.length() != 6) {
            showError(getString(R.string.validation_otp_invalid));
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

                // Clear all OTP boxes
                clearOtpBoxes();
            } else {
                showError(resource.getMessage());
            }
        });
    }

    private void clearOtpBoxes() {
        for (EditText box : otpBoxes) {
            box.setText("");
        }
        // Focus on first box
        binding.etOtp1.requestFocus();
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnVerify.setEnabled(false);
            binding.btnVerify.setText("");
        } else {
            binding.btnVerify.setEnabled(getOtpFromBoxes().length() == 6);
            binding.btnVerify.setText(getString(R.string.otp_button));
        }
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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
