package group.eleven.snippet_sharing_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.ui.onboarding.OnboardingActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Main Activity - Splash screen that redirects to Login or Home
 */
public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500; // 1.5 seconds
    private static final String PREF_NAME = "onboarding_pref";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Delay and then navigate
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DELAY);
    }

    private void navigateToNextScreen() {
        // Check if onboarding is completed
        boolean onboardingCompleted = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getBoolean(KEY_ONBOARDING_COMPLETED, false);

        Intent intent;
        if (!onboardingCompleted) {
            // First time user, show onboarding
            intent = new Intent(this, OnboardingActivity.class);
        } else {
            SessionManager sessionManager = new SessionManager(this);
            if (sessionManager.isLoggedIn()) {
                // User is logged in, go to Home
                intent = new Intent(this, HomeActivity.class);
            } else {
                // User is not logged in, go to Login
                intent = new Intent(this, LoginActivity.class);
            }
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
