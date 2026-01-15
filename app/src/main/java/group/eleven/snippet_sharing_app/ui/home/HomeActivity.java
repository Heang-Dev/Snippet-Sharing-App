package group.eleven.snippet_sharing_app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import group.eleven.snippet_sharing_app.data.model.ActivityFeedItem;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.databinding.ActivityHomeBinding;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;
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
            setupClickListeners();

            Log.d(TAG, "onCreate: Setup completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                    ? user.getFullName() : user.getUsername();
            tvUserName.setText(displayName);
        }

        // Set mock stats (will be replaced with real data)
        tvSnippetsCount.setText("142");
        tvForksCount.setText("89");
        tvViewsCount.setText("1.2k");
    }

    private void setupUserInfo() {
        // User avatar click opens drawer
        binding.ivUserAvatar.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    private void setupStatsCards() {
        // Setup Snippets stat card
        View snippetsCard = binding.statSnippets.getRoot();
        ImageView ivSnippetsIcon = snippetsCard.findViewById(R.id.ivStatIcon);
        TextView tvSnippetsLabel = snippetsCard.findViewById(R.id.tvStatLabel);
        TextView tvSnippetsCount = snippetsCard.findViewById(R.id.tvStatCount);
        TextView tvSnippetsPercentage = snippetsCard.findViewById(R.id.tvStatPercentage);
        ImageView ivSnippetsArrow = snippetsCard.findViewById(R.id.ivStatArrow);

        ivSnippetsIcon.setImageResource(R.drawable.ic_code);
        tvSnippetsLabel.setText(R.string.stats_snippets);
        tvSnippetsCount.setText("142");
        tvSnippetsPercentage.setText("+5%");
        ivSnippetsArrow.setRotation(-90); // Up arrow

        // Setup Views stat card
        View viewsCard = binding.statViews.getRoot();
        ImageView ivViewsIcon = viewsCard.findViewById(R.id.ivStatIcon);
        TextView tvViewsLabel = viewsCard.findViewById(R.id.tvStatLabel);
        TextView tvViewsCount = viewsCard.findViewById(R.id.tvStatCount);
        TextView tvViewsPercentage = viewsCard.findViewById(R.id.tvStatPercentage);
        ImageView ivViewsArrow = viewsCard.findViewById(R.id.ivStatArrow);

        ivViewsIcon.setImageResource(R.drawable.ic_explore);
        tvViewsLabel.setText(R.string.stats_views);
        tvViewsCount.setText("1.2k");
        tvViewsPercentage.setText("+12%");
        ivViewsArrow.setRotation(-90); // Up arrow

        // Setup Forks stat card
        View forksCard = binding.statForks.getRoot();
        ImageView ivForksIcon = forksCard.findViewById(R.id.ivStatIcon);
        TextView tvForksLabel = forksCard.findViewById(R.id.tvStatLabel);
        TextView tvForksCount = forksCard.findViewById(R.id.tvStatCount);
        TextView tvForksPercentage = forksCard.findViewById(R.id.tvStatPercentage);
        ImageView ivForksArrow = forksCard.findViewById(R.id.ivStatArrow);

        ivForksIcon.setImageResource(R.drawable.ic_collections);
        tvForksLabel.setText(R.string.stats_forks);
        tvForksCount.setText("89");
        tvForksPercentage.setText("0%");
        tvForksPercentage.setTextColor(getColor(R.color.text_secondary));
        ivForksArrow.setVisibility(View.GONE);
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

    private void setupRecentSnippets() {
        // Create mock snippet data
        List<SnippetCard> snippets = new ArrayList<>();
        snippets.add(new SnippetCard(
                "Python CSV Parser",
                "Py",
                "Updated 2h ago",
                "import csv\ndef parse_csv(file_path):\n    with open(file_path, 'r') as f:\n        reader = csv.reader(f)\n        for row in reader:\n            print(row)",
                new String[]{"Data", "Utils"},
                R.color.error
        ));
        snippets.add(new SnippetCard(
                "Auth Hook",
                "React",
                "Updated yesterday",
                "export const useAuth = () => {\n  const {user, setUser} = useState(null);\n  \n  useEffect(() => {\n    // Auth logic\n  }, []);\n}",
                new String[]{"React", "Hooks"},
                R.color.info
        ));

        // Setup RecyclerView
        snippetCardAdapter = new SnippetCardAdapter(snippets);
        snippetCardAdapter.setOnSnippetClickListener(snippet -> {
            Toast.makeText(this, "Clicked: " + snippet.getTitle(), Toast.LENGTH_SHORT).show();
        });
        binding.rvRecentSnippets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRecentSnippets.setAdapter(snippetCardAdapter);
    }

    private void setupClickListeners() {
        // Search card click
        binding.searchCard.setOnClickListener(v -> {
            Toast.makeText(this, "Search - Coming Soon", Toast.LENGTH_SHORT).show();
        });

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

        // FAB click
        binding.fabAddSnippet.setOnClickListener(v -> {
            Toast.makeText(this, "Create Snippet - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_all_snippets) {
            Toast.makeText(this, "All Snippets - Coming Soon", Toast.LENGTH_SHORT).show();
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
