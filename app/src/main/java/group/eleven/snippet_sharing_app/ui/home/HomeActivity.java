package group.eleven.snippet_sharing_app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.databinding.ActivityHomeBinding;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Home Activity - main dashboard after login
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private ActivityHomeBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting HomeActivity");

        try {
            // Inflate the layout
            binding = ActivityHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            Log.d(TAG, "onCreate: Layout inflated");

            // Handle window insets
            ViewCompat.setOnApplyWindowInsetsListener(binding.coordinatorLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            // Initialize session manager
            sessionManager = new SessionManager(this);

            // Check if logged in
            if (!sessionManager.isLoggedIn()) {
                Log.w(TAG, "User not logged in, redirecting...");
                navigateToLogin();
                return;
            }

            // Setup UI
            setupUserInfo();
            setupClickListeners();

            Log.d(TAG, "onCreate: Setup completed successfully");
            Toast.makeText(this, "Welcome to Home!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupUserInfo() {
        User user = sessionManager.getUser();

        String displayName = "User";
        String email = "";

        if (user != null) {
            // Get display name
            if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                displayName = user.getFullName();
            } else if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                displayName = user.getUsername();
            }

            // Get email
            if (user.getEmail() != null) {
                email = user.getEmail();
            }
        }

        binding.tvWelcome.setText(getString(R.string.home_welcome, displayName));
        binding.tvEmail.setText(email);
    }

    private void setupClickListeners() {
        // Quick action cards
        binding.cardExplore.setOnClickListener(v -> {
            Toast.makeText(this, "Explore Snippets - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        binding.cardMySnippets.setOnClickListener(v -> {
            Toast.makeText(this, "My Snippets - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        binding.cardFavorites.setOnClickListener(v -> {
            Toast.makeText(this, "Favorites - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        binding.cardCollections.setOnClickListener(v -> {
            Toast.makeText(this, "Collections - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Logout button
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.profile_logout)
                .setMessage(R.string.profile_logout_confirm)
                .setPositiveButton(R.string.profile_logout, (dialog, which) -> logout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, getString(R.string.profile_logout_success), Toast.LENGTH_SHORT).show();
        navigateToLogin();
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
