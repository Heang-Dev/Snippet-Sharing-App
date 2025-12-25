package group.eleven.snippet_sharing_app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityHomeBinding;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Home Activity - main dashboard after login
 */
public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.coordinatorLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        // Check if logged in
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setupUserInfo();
        setupClickListeners();
    }

    private void setupUserInfo() {
        User user = sessionManager.getUser();
        if (user != null) {
            String displayName = user.getFullName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = user.getUsername();
            }
            binding.tvWelcome.setText(getString(R.string.home_welcome, displayName));
            binding.tvEmail.setText(user.getEmail());
        }
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
        authRepository.logout().observe(this, resource -> {
            if (resource.isLoading()) {
                return;
            }

            Toast.makeText(this, getString(R.string.profile_logout_success), Toast.LENGTH_SHORT).show();
            navigateToLogin();
        });
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
