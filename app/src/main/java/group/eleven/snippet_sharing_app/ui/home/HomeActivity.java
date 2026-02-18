package group.eleven.snippet_sharing_app.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.SnippetManager;
import group.eleven.snippet_sharing_app.data.model.ActivityFeedItem;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.databinding.ActivityHomeBinding;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Home Activity - main dashboard with navigation drawer
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private ActivityHomeBinding binding;
    private SessionManager sessionManager;
    private ActionBarDrawerToggle drawerToggle;

    private ActivityFeedAdapter activityFeedAdapter;
    private SnippetCardAdapter snippetCardAdapter;
    private List<SnippetCard> masterSnippetList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting HomeActivity");

        try {
            // Inflate the layout
            binding = ActivityHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // ... (window insets code) ...

            // Initialize session manager
            sessionManager = new SessionManager(this);

            if (!sessionManager.isLoggedIn()) {
                navigateToLogin();
                return;
            }

            // Setup toolbar
            setSupportActionBar(binding.toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            // Setup navigation drawer
            setupNavigationDrawer();

            // Setup UI components
            setupUserInfo();
            setupStatsCards();
            setupActivityFeed();
            setupRecentSnippets();
            setupSearch(); // Initialize search listener
            setupClickListeners();

            Log.d(TAG, "onCreate: Setup completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Refreshing snippets list");
        if (snippetCardAdapter != null) {
            snippetCardAdapter.notifyDataSetChanged();
        }
    }

    private void setupNavigationDrawer() {
        // Setup drawer toggle
        drawerToggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Setup navigation view
        binding.navigationView.setNavigationItemSelectedListener(this);

        // Setup drawer header
        View headerView = binding.navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tvUserName);
        TextView tvSnippetsCount = headerView.findViewById(R.id.tvSnippetsCount);
        TextView tvForksCount = headerView.findViewById(R.id.tvForksCount);
        TextView tvViewsCount = headerView.findViewById(R.id.tvViewsCount);

        User user = sessionManager.getUser();
        if (user != null) {
            String displayName = user.getFullName() != null && !user.getFullName().isEmpty()
                    ? user.getFullName()
                    : user.getUsername();
            tvUserName.setText(displayName);
        }

        // Set mock stats (will be replaced with real data)
        tvSnippetsCount.setText("142");
        tvForksCount.setText("89");
        tvViewsCount.setText("1.2k");
    }

    private void setupUserInfo() {
        // User avatar click navigates to My Snippets
        binding.ivUserAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(this, group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity.class);
            startActivity(intent);
        });
    }

    private void setupStatsCards() {
        // Setup Snippets stat card
        View snippetsCard = binding.statSnippets;
        ImageView ivSnippetsIcon = snippetsCard.findViewById(R.id.ivStatIconSnippets);
        TextView tvSnippetsLabel = snippetsCard.findViewById(R.id.tvStatLabelSnippets);
        TextView tvSnippetsCount = snippetsCard.findViewById(R.id.tvStatCountSnippets);
        // Arrow and percentage removed in design update

        ivSnippetsIcon.setImageResource(R.drawable.ic_code);
        tvSnippetsLabel.setText(R.string.stats_snippets);
        tvSnippetsCount.setText("142");

        // Setup Views stat card
        View viewsCard = binding.statViews;
        ImageView ivViewsIcon = viewsCard.findViewById(R.id.ivStatIconViews);
        TextView tvViewsLabel = viewsCard.findViewById(R.id.tvStatLabelViews);
        TextView tvViewsCount = viewsCard.findViewById(R.id.tvStatCountViews);

        ivViewsIcon.setImageResource(R.drawable.ic_explore);
        tvViewsLabel.setText(R.string.stats_views);
        tvViewsCount.setText("12.5k");

        // Setup Forks stat card
        View forksCard = binding.statForks;
        ImageView ivForksIcon = forksCard.findViewById(R.id.ivStatIconForks);
        TextView tvForksLabel = forksCard.findViewById(R.id.tvStatLabelForks);
        TextView tvForksCount = forksCard.findViewById(R.id.tvStatCountForks);

        ivForksIcon.setImageResource(R.drawable.ic_collections);
        tvForksLabel.setText(R.string.stats_forks);
        tvForksCount.setText("89");
    }

    private void setupActivityFeed() {
        // Create mock activity feed data
        List<ActivityFeedItem> activities = new ArrayList<>();
        activities.add(new ActivityFeedItem(
                "Alice", "updated", "Auth Logic", "20 min ago", R.drawable.ic_code));
        activities.add(new ActivityFeedItem(
                "John", "starred", "Docker Config", "1h ago", R.drawable.ic_favorite));

        // Setup RecyclerView
        activityFeedAdapter = new ActivityFeedAdapter(activities);
        binding.rvActivityFeed.setLayoutManager(new LinearLayoutManager(this));
        binding.rvActivityFeed.setAdapter(activityFeedAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentSnippets();
    }

    private void setupRecentSnippets() {
        // Setup RecyclerView with empty list initially
        binding.rvRecentSnippets.setLayoutManager(new LinearLayoutManager(this));
        snippetCardAdapter = new SnippetCardAdapter(new ArrayList<>());
        snippetCardAdapter.setOnSnippetClickListener(snippet -> {
            Toast.makeText(this, "Clicked: " + snippet.getTitle(), Toast.LENGTH_SHORT).show();
        });
        binding.rvRecentSnippets.setAdapter(snippetCardAdapter);
    }

    private void loadRecentSnippets() {
        List<group.eleven.snippet_sharing_app.model.SnippetModel> models = group.eleven.snippet_sharing_app.data.SnippetRepository
                .getInstance().getRecentSnippets();

        List<SnippetCard> cards = new ArrayList<>();
        for (group.eleven.snippet_sharing_app.model.SnippetModel model : models) {
            int color = android.graphics.Color.WHITE;
            try {
                color = android.graphics.Color.parseColor(model.getLanguageColor());
            } catch (Exception e) {
                e.printStackTrace();
            }

            cards.add(new SnippetCard(
                    model.getTitle(),
                    model.getLanguage(),
                    model.getLastModifiedTime(),
                    model.getCode() != null ? model.getCode() : "// No code preview",
                    new String[] { model.getPrivacy() },
                    color));
        }

        masterSnippetList.clear();
        masterSnippetList.addAll(cards);

        if (binding != null && binding.etSearch != null) {
            filter(binding.etSearch.getText().toString());
        } else {
            if (snippetCardAdapter != null)
                snippetCardAdapter.filterList(cards);
        }
    }

    private void setupSearch() {
        binding.etSearch.setFocusable(false);
        binding.etSearch.setClickable(true);
        binding.etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this,
                    group.eleven.snippet_sharing_app.ui.search.SearchActivity.class);
            startActivity(intent);
        });
    }

    private void filter(String text) {
        List<SnippetCard> filteredList = new ArrayList<>();
        if (text == null)
            text = "";

        for (SnippetCard snippet : masterSnippetList) {
            if (snippet.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    snippet.getLanguageBadge().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(snippet);
            }
        }

        if (snippetCardAdapter != null) {
            snippetCardAdapter.filterList(filteredList);
        }
    }

    // ...

    private void setupClickListeners() {
        // View All Activity
        binding.tvViewAllActivity.setOnClickListener(v -> {
            Toast.makeText(this, "View All Activity - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // View toggle icons
        binding.ivGridView.setOnClickListener(v -> {
            Toast.makeText(this, "Grid View - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        binding.ivListView.setOnClickListener(v -> {
            Toast.makeText(this, "List View Active", Toast.LENGTH_SHORT).show();
        });

        // Bottom Navigation
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        com.google.android.material.floatingactionbutton.FloatingActionButton fab = findViewById(R.id.fab);

        if (bottomNav != null) {
            // Set Search (Home) as selected
            bottomNav.setSelectedItemId(R.id.nav_search);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_search) {
                    // Already on Home
                    return true;
                } else if (id == R.id.nav_library) {
                    Intent intent = new Intent(this,
                            group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity.class);
                    startActivity(intent);
                    return false; // Don't switch tab visually, just launch activity
                } else if (id == R.id.nav_activity) {
                    Toast.makeText(this, "Activity - Coming Soon", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (id == R.id.nav_settings) {
                    Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return false;
            });
        }

        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateSnippetActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_all_snippets) {
            Intent intent = new Intent(this, group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorites) {
            Toast.makeText(this, "Favorites - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_shared) {
            Toast.makeText(this, "Shared with me - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_teams) {
            Toast.makeText(this, "Teams - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_profile) {
            Toast.makeText(this, "Profile - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_preferences) {
            Toast.makeText(this, "Preferences - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_help) {
            Toast.makeText(this, "Help & Support - Coming Soon", Toast.LENGTH_SHORT).show();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint({ "MissingSuperCall", "GestureBackNavigation" })
    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Show logout confirmation
            showLogoutConfirmation();
        }
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