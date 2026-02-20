package group.eleven.snippet_sharing_app.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.MockDataProvider;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.repository.DashboardRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityFavoritesBinding;
import group.eleven.snippet_sharing_app.ui.explore.ExploreActivity;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.ui.home.SnippetCardAdapter;
import group.eleven.snippet_sharing_app.ui.profile.ProfileActivity;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.ui.team.TeamsListActivity;
import group.eleven.snippet_sharing_app.utils.BottomNavHelper;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * FavoritesActivity - Display user's starred/favorite snippets
 */
public class FavoritesActivity extends AppCompatActivity {

    private static final String TAG = "FavoritesActivity";

    private ActivityFavoritesBinding binding;
    private SessionManager sessionManager;
    private DashboardRepository dashboardRepository;
    private SnippetCardAdapter adapter;
    private List<SnippetCard> allFavorites = new ArrayList<>();
    private List<SnippetCard> filteredFavorites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupStatusBar();

        sessionManager = new SessionManager(this);
        dashboardRepository = new DashboardRepository(this);

        setupToolbar();
        setupSearch();
        setupRecyclerView();
        setupBottomNavigation();
        setupClickListeners();

        loadFavorites();
    }

    private void setupStatusBar() {
        android.view.Window window = getWindow();
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(R.attr.appBackgroundColor, typedValue, true);
        int backgroundColor = typedValue.data;
        window.setStatusBarColor(backgroundColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            boolean isLightBackground = isColorLight(backgroundColor);
            controller.setAppearanceLightStatusBars(isLightBackground);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color)
                + 0.587 * android.graphics.Color.green(color)
                + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFavorites(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SnippetCardAdapter(new ArrayList<>());
        adapter.setOnSnippetClickListener(snippet -> {
            Toast.makeText(this, "Clicked: " + snippet.getTitle(), Toast.LENGTH_SHORT).show();
        });
        binding.rvFavorites.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        // Set Favorites as selected
        binding.bottomNav.setSelectedItemId(R.id.nav_favorites);

        // Setup profile avatar
        BottomNavHelper.setupProfileAvatar(this, binding.bottomNav, sessionManager);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_teams) {
                Intent intent = new Intent(this, TeamsListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_favorites) {
                // Already on Favorites
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

        // FAB click
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateSnippetActivity.class);
            startActivity(intent);
        });
    }

    private void setupClickListeners() {
        binding.btnExplore.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExploreActivity.class);
            startActivity(intent);
        });

        binding.swipeRefresh.setOnRefreshListener(this::loadFavorites);
    }

    private void loadFavorites() {
        binding.swipeRefresh.setRefreshing(true);

        // For now, use mock data with favorites filter
        // In production, this would call an API endpoint for starred snippets
        List<SnippetCard> mockSnippets = MockDataProvider.getMockSnippetCards(10);

        // Simulate favorites by showing a subset of snippets
        allFavorites.clear();
        for (int i = 0; i < mockSnippets.size(); i++) {
            // Show alternating snippets as favorites for demo
            if (i % 2 == 0) {
                allFavorites.add(mockSnippets.get(i));
            }
        }

        binding.swipeRefresh.setRefreshing(false);
        updateUI();
    }

    private void filterFavorites(String query) {
        filteredFavorites.clear();

        if (query.isEmpty()) {
            filteredFavorites.addAll(allFavorites);
        } else {
            String lowerQuery = query.toLowerCase();
            for (SnippetCard snippet : allFavorites) {
                if (snippet.getTitle().toLowerCase().contains(lowerQuery) ||
                    snippet.getLanguageBadge().toLowerCase().contains(lowerQuery) ||
                    (snippet.getDescription() != null && snippet.getDescription().toLowerCase().contains(lowerQuery))) {
                    filteredFavorites.add(snippet);
                }
            }
        }

        adapter.filterList(filteredFavorites);
        updateCount(filteredFavorites.size());
    }

    private void updateUI() {
        if (allFavorites.isEmpty()) {
            binding.rvFavorites.setVisibility(View.GONE);
            binding.layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.rvFavorites.setVisibility(View.VISIBLE);
            binding.layoutEmpty.setVisibility(View.GONE);
            filteredFavorites.clear();
            filteredFavorites.addAll(allFavorites);
            adapter.filterList(filteredFavorites);
        }
        updateCount(allFavorites.size());
    }

    private void updateCount(int count) {
        String text = count == 1 ? "1 favorite" : count + " favorites";
        binding.tvFavoritesCount.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure correct nav item is selected when returning
        binding.bottomNav.setSelectedItemId(R.id.nav_favorites);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
