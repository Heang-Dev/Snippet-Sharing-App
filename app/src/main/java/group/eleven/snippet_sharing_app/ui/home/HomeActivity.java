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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.repository.DashboardRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityHomeBinding;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
import group.eleven.snippet_sharing_app.ui.team.TeamsListActivity;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.ui.profile.ProfileActivity;
import group.eleven.snippet_sharing_app.ui.profile.AccountSettingsActivity;
import group.eleven.snippet_sharing_app.ui.notification.NotificationsActivity;
import group.eleven.snippet_sharing_app.ui.search.SearchActivity;
import group.eleven.snippet_sharing_app.utils.BottomNavHelper;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Home Activity - Social feed with Facebook-style layout
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private ActivityHomeBinding binding;
    private SessionManager sessionManager;
    private DashboardRepository dashboardRepository;
    private ActionBarDrawerToggle drawerToggle;

    private FeedSnippetAdapter feedAdapter;
    private List<SnippetCard> snippetList = new ArrayList<>();

    // Drawer header stats
    private TextView tvDrawerSnippetsCount, tvDrawerForksCount, tvDrawerViewsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting HomeActivity");

        try {
            // Inflate the layout
            binding = ActivityHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Setup theme-aware status bar
            setupStatusBar();

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
            setupFeed();
            setupSwipeRefresh();
            setupClickListeners();

            // Load initial data
            loadFeed();

            Log.d(TAG, "onCreate: Setup completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Refreshing feed");
        loadFeed();
    }

    /**
     * Setup status bar colors based on current theme (light/dark)
     */
    private void setupStatusBar() {
        android.view.Window window = getWindow();

        // Get the background color from theme
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(R.attr.appBackgroundColor, typedValue, true);
        int backgroundColor = typedValue.data;

        // Set status bar color to match background
        window.setStatusBarColor(backgroundColor);

        // Set status bar icon colors based on theme
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            // Check if we're in light mode (background is light colored)
            boolean isLightBackground = isColorLight(backgroundColor);
            // Light background = dark icons (true), Dark background = light icons (false)
            controller.setAppearanceLightStatusBars(isLightBackground);
        }
    }

    /**
     * Determine if a color is light or dark
     */
    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color)
                + 0.587 * android.graphics.Color.green(color)
                + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness < 0.5;
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
        // Load user avatar in create post card
        User user = sessionManager.getUser();
        if (user != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.ivUserAvatar);
        }
    }

    private void setupFeed() {
        // Setup RecyclerView with feed adapter
        binding.rvRecentSnippets.setLayoutManager(new LinearLayoutManager(this));
        feedAdapter = new FeedSnippetAdapter(new ArrayList<>());
        feedAdapter.setOnFeedItemClickListener(new FeedSnippetAdapter.OnFeedItemClickListener() {
            @Override
            public void onSnippetClick(SnippetCard snippet) {
                Toast.makeText(HomeActivity.this, "View: " + snippet.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to snippet detail
            }

            @Override
            public void onLikeClick(SnippetCard snippet, int position) {
                boolean newLikeState = !snippet.isLiked();
                int newCount = snippet.getLikesCount() + (newLikeState ? 1 : -1);
                feedAdapter.updateLikeState(position, newLikeState, newCount);
                // TODO: Call API to like/unlike
            }

            @Override
            public void onCommentClick(SnippetCard snippet) {
                showCommentsBottomSheet(snippet);
            }

            @Override
            public void onShareClick(SnippetCard snippet) {
                shareSnippet(snippet);
            }

            @Override
            public void onAuthorClick(SnippetCard snippet) {
                Toast.makeText(HomeActivity.this, "View Profile: " + snippet.getAuthorName(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to user profile
            }

            @Override
            public void onMoreOptionsClick(SnippetCard snippet, View anchor) {
                showSnippetOptions(snippet, anchor);
            }
        });
        binding.rvRecentSnippets.setAdapter(feedAdapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(this::loadFeed);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh bottom nav selection
        if (binding.bottomNav != null) {
            binding.bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    /**
     * Load public snippets for social feed
     */
    private void loadFeed() {
        dashboardRepository.getPublicSnippets(20).observe(this, resource -> {
            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                snippetList.clear();
                snippetList.addAll(resource.data);
                feedAdapter.setSnippets(snippetList);
                updateEmptyState(snippetList.isEmpty());
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load feed: " + resource.message);
                loadTrendingFallback();
            } else if (resource.status == Resource.Status.LOADING) {
                // Show loading state
                if (snippetList.isEmpty()) {
                    binding.swipeRefresh.setRefreshing(true);
                }
            }
        });
    }

    private void loadTrendingFallback() {
        dashboardRepository.getTrendingSnippets(15).observe(this, resource -> {
            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                snippetList.clear();
                snippetList.addAll(resource.data);
                feedAdapter.setSnippets(snippetList);
                updateEmptyState(snippetList.isEmpty());
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load trending: " + resource.message);
                loadLocalFallback();
            }
        });
    }

    private void loadLocalFallback() {
        try {
            // Use MockDataProvider for realistic test data
            List<SnippetCard> mockCards = group.eleven.snippet_sharing_app.data.MockDataProvider
                    .getMockSnippetCards(15);

            snippetList.clear();
            snippetList.addAll(mockCards);
            feedAdapter.setSnippets(snippetList);
            updateEmptyState(snippetList.isEmpty());

            Log.d(TAG, "Loaded " + mockCards.size() + " mock snippets for testing");
        } catch (Exception e) {
            Log.e(TAG, "Failed to load mock data", e);
            updateEmptyState(true);
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        if (binding.emptyState != null) {
            binding.emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        binding.rvRecentSnippets.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void shareSnippet(SnippetCard snippet) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, snippet.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                snippet.getTitle() + "\n\n" + snippet.getCodePreview() + "\n\nShared via Snippet G11");
        startActivity(Intent.createChooser(shareIntent, "Share snippet"));
    }

    private void showSnippetOptions(SnippetCard snippet, View anchor) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_snippet_options, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_copy_code) {
                copyToClipboard(snippet.getCodePreview());
                return true;
            } else if (id == R.id.action_save) {
                Toast.makeText(this, "Saved to favorites", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_report) {
                Toast.makeText(this, "Report - Coming Soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void copyToClipboard(String text) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("code", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Code copied!", Toast.LENGTH_SHORT).show();
    }


    private void setupClickListeners() {
        // Search icon
        binding.ivSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        // Notification icon
        binding.ivNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Create post card
        binding.cardCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateSnippetActivity.class);
            startActivity(intent);
        });

        // Filter feed
        binding.tvFilterFeed.setOnClickListener(v -> {
            Toast.makeText(this, "Filter - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Empty state create button
        if (binding.btnCreateFirst != null) {
            binding.btnCreateFirst.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateSnippetActivity.class);
                startActivity(intent);
            });
        }

        // FAB
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateSnippetActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation
        binding.bottomNav.setSelectedItemId(R.id.nav_home);
        BottomNavHelper.setupProfileAvatar(this, binding.bottomNav, sessionManager);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_teams) {
                Intent intent = new Intent(this, TeamsListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_favorites) {
                Intent intent = new Intent(this,
                        group.eleven.snippet_sharing_app.ui.favorites.FavoritesActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
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
            Intent intent = new Intent(this, group.eleven.snippet_sharing_app.ui.favorites.FavoritesActivity.class);
            startActivity(intent);
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

    private void showCommentsBottomSheet(SnippetCard snippet) {
        group.eleven.snippet_sharing_app.ui.comment.CommentsBottomSheet bottomSheet =
                group.eleven.snippet_sharing_app.ui.comment.CommentsBottomSheet.newInstance(
                        snippet.getId() != null ? snippet.getId() : "snippet_1",
                        snippet.getTitle()
                );
        bottomSheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}