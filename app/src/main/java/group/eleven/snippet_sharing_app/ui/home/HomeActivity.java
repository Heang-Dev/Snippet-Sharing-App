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
import group.eleven.snippet_sharing_app.data.model.DashboardStats;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.repository.DashboardRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityHomeBinding;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
import group.eleven.snippet_sharing_app.ui.team.TeamsListActivity;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.ui.profile.ProfileActivity;
import group.eleven.snippet_sharing_app.ui.profile.AccountSettingsActivity;
import group.eleven.snippet_sharing_app.ui.profile.NotificationSettingsActivity;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Home Activity - main dashboard with navigation drawer
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private ActivityHomeBinding binding;
    private SessionManager sessionManager;
    private DashboardRepository dashboardRepository;
    private ActionBarDrawerToggle drawerToggle;

    private ActivityFeedAdapter activityFeedAdapter;
    private SnippetCardAdapter snippetCardAdapter;
    private List<SnippetCard> masterSnippetList = new ArrayList<>();

    // Stats TextViews
    private TextView tvSnippetsCount, tvViewsCount, tvForksCount;
    private TextView tvDrawerSnippetsCount, tvDrawerForksCount, tvDrawerViewsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting HomeActivity");

        try {
            // Inflate the layout
            binding = ActivityHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // ... (window insets code) ...

            // Initialize session manager and repository
            sessionManager = new SessionManager(this);
            dashboardRepository = new DashboardRepository(this);

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
        tvDrawerSnippetsCount = headerView.findViewById(R.id.tvSnippetsCount);
        tvDrawerForksCount = headerView.findViewById(R.id.tvForksCount);
        tvDrawerViewsCount = headerView.findViewById(R.id.tvViewsCount);

        User user = sessionManager.getUser();
        if (user != null) {
            String displayName = user.getFullName() != null && !user.getFullName().isEmpty()
                    ? user.getFullName()
                    : user.getUsername();
            tvUserName.setText(displayName);
        }

        // Set placeholder stats (will be replaced with real data from API)
        tvDrawerSnippetsCount.setText("--");
        tvDrawerForksCount.setText("--");
        tvDrawerViewsCount.setText("--");
    }

    private void setupUserInfo() {
        // Notification icon click navigates to Notification Settings
        binding.ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationSettingsActivity.class);
            startActivity(intent);
        });
    }

    private void setupStatsCards() {
        // Setup Snippets stat card
        View snippetsCard = binding.statSnippets;
        ImageView ivSnippetsIcon = snippetsCard.findViewById(R.id.ivStatIconSnippets);
        TextView tvSnippetsLabel = snippetsCard.findViewById(R.id.tvStatLabelSnippets);
        tvSnippetsCount = snippetsCard.findViewById(R.id.tvStatCountSnippets);

        ivSnippetsIcon.setImageResource(R.drawable.ic_code);
        tvSnippetsLabel.setText(R.string.stats_snippets);
        tvSnippetsCount.setText("--");

        // Setup Views stat card (using favorites_received as popularity metric)
        View viewsCard = binding.statViews;
        ImageView ivViewsIcon = viewsCard.findViewById(R.id.ivStatIconViews);
        TextView tvViewsLabel = viewsCard.findViewById(R.id.tvStatLabelViews);
        tvViewsCount = viewsCard.findViewById(R.id.tvStatCountViews);

        ivViewsIcon.setImageResource(R.drawable.ic_explore);
        tvViewsLabel.setText(R.string.stats_views);
        tvViewsCount.setText("--");

        // Setup Forks stat card (using followers count)
        View forksCard = binding.statForks;
        ImageView ivForksIcon = forksCard.findViewById(R.id.ivStatIconForks);
        TextView tvForksLabel = forksCard.findViewById(R.id.tvStatLabelForks);
        tvForksCount = forksCard.findViewById(R.id.tvStatCountForks);

        ivForksIcon.setImageResource(R.drawable.ic_collections);
        tvForksLabel.setText(R.string.stats_forks);
        tvForksCount.setText("--");

        // Fetch real stats from API
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        dashboardRepository.getDashboardStats().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                DashboardStats stats = resource.data;

                // Update stats cards
                tvSnippetsCount.setText(String.valueOf(stats.getSnippetCount()));
                tvViewsCount.setText(stats.getFormattedFavorites());
                tvForksCount.setText(stats.getFormattedFollowers());

                // Update drawer header stats
                if (tvDrawerSnippetsCount != null) {
                    tvDrawerSnippetsCount.setText(String.valueOf(stats.getSnippetCount()));
                }
                if (tvDrawerForksCount != null) {
                    tvDrawerForksCount.setText(stats.getFormattedFollowers());
                }
                if (tvDrawerViewsCount != null) {
                    tvDrawerViewsCount.setText(stats.getFormattedFavorites());
                }
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load stats: " + resource.message);
                // Keep showing "--" or set defaults
                tvSnippetsCount.setText("0");
                tvViewsCount.setText("0");
                tvForksCount.setText("0");
            }
            // For LOADING status, keep showing "--"
        });
    }

    private void setupActivityFeed() {
        // Setup RecyclerView with empty list initially
        activityFeedAdapter = new ActivityFeedAdapter(new ArrayList<>());
        binding.rvActivityFeed.setLayoutManager(new LinearLayoutManager(this));
        binding.rvActivityFeed.setAdapter(activityFeedAdapter);

        // Fetch real activity feed from API
        loadActivityFeed();
    }

    private void loadActivityFeed() {
        dashboardRepository.getActivityFeed(5).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                activityFeedAdapter.setActivities(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load activity feed: " + resource.message);
                // Show placeholder message or empty state
                List<ActivityFeedItem> placeholder = new ArrayList<>();
                placeholder.add(new ActivityFeedItem(
                        "Welcome", "to", "Snippet Sharing", "Just now", R.drawable.ic_code));
                activityFeedAdapter.setActivities(placeholder);
            }
            // For LOADING, keep showing previous data or empty
        });
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
        dashboardRepository.getRecentSnippets(10).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                masterSnippetList.clear();
                masterSnippetList.addAll(resource.data);

                if (binding != null && binding.etSearch != null) {
                    filter(binding.etSearch.getText().toString());
                } else {
                    if (snippetCardAdapter != null) {
                        snippetCardAdapter.filterList(resource.data);
                    }
                }
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load snippets: " + resource.message);
                // Fallback to local storage if API fails
                loadLocalSnippets();
            }
            // For LOADING, keep showing previous data
        });
    }

    /**
     * Fallback to local snippets when API fails
     */
    private void loadLocalSnippets() {
        try {
            List<group.eleven.snippet_sharing_app.model.SnippetModel> models =
                    group.eleven.snippet_sharing_app.data.SnippetRepository.getInstance().getRecentSnippets();

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

            if (snippetCardAdapter != null) {
                snippetCardAdapter.filterList(cards);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load local snippets", e);
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
            // Set Home as selected
            bottomNav.setSelectedItemId(R.id.nav_home);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Already on Home
                    return true;
                } else if (id == R.id.nav_teams) {
                    Intent intent = new Intent(this,
                            group.eleven.snippet_sharing_app.ui.team.TeamsListActivity.class);
                    startActivity(intent);
                    return false; // Don't switch tab visually, just launch activity
                } else if (id == R.id.nav_snippets) {
                    Intent intent = new Intent(this,
                            group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity.class);
                    startActivity(intent);
                    return false;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
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
            Intent intent = new Intent(HomeActivity.this, TeamsListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_preferences) {
            Intent intent = new Intent(this, AccountSettingsActivity.class);
            startActivity(intent);
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