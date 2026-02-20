package group.eleven.snippet_sharing_app.ui.explore;

import android.content.Intent;
import android.os.Bundle;
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
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.repository.DashboardRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityExploreBinding;
import group.eleven.snippet_sharing_app.ui.favorites.FavoritesActivity;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.ui.home.SnippetCardAdapter;
import group.eleven.snippet_sharing_app.ui.profile.ProfileActivity;
import group.eleven.snippet_sharing_app.ui.search.SearchActivity;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.ui.team.TeamsListActivity;
import group.eleven.snippet_sharing_app.utils.BottomNavHelper;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * ExploreActivity - Browse and discover public snippets
 */
public class ExploreActivity extends AppCompatActivity {

    private static final String TAG = "ExploreActivity";

    private ActivityExploreBinding binding;
    private SessionManager sessionManager;
    private DashboardRepository dashboardRepository;
    private SnippetCardAdapter snippetAdapter;
    private List<SnippetCard> allSnippets = new ArrayList<>();
    private String currentLanguageFilter = null; // null means "All"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupStatusBar();

        sessionManager = new SessionManager(this);
        dashboardRepository = new DashboardRepository(this);

        setupSearchBar();
        setupLanguageChips();
        setupSnippetsRecyclerView();
        setupBottomNavigation();
        setupSwipeRefresh();
        setupEmptyState();

        loadTrendingSnippets();
    }

    private void setupStatusBar() {
        android.view.Window window = getWindow();
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(R.attr.surfaceColor, typedValue, true);
        int surfaceColor;
        if (typedValue.resourceId != 0) {
            surfaceColor = androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            surfaceColor = typedValue.data;
        }
        window.setStatusBarColor(surfaceColor);
        window.setNavigationBarColor(surfaceColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            boolean isLightBackground = isColorLight(surfaceColor);
            controller.setAppearanceLightStatusBars(isLightBackground);
            controller.setAppearanceLightNavigationBars(isLightBackground);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color)
                + 0.587 * android.graphics.Color.green(color)
                + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void setupSearchBar() {
        // Make search bar clickable to launch full SearchActivity
        binding.etSearch.setFocusable(false);
        binding.etSearch.setClickable(true);
        binding.etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        binding.searchCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadTrendingSnippets);
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
    }

    private void setupEmptyState() {
        binding.btnCreateFirst.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateSnippetActivity.class);
            startActivity(intent);
        });
    }

    private void setupLanguageChips() {
        // Set "All" as initially selected
        binding.chipAll.setChecked(true);

        // Language chip click listeners
        binding.chipAll.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipAll.setChecked(true);
            currentLanguageFilter = null;
            filterAndShowSnippets();
            updateSectionTitle("Trending");
        });

        binding.chipJavascript.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipJavascript.setChecked(true);
            currentLanguageFilter = "javascript";
            filterAndShowSnippets();
            updateSectionTitle("JavaScript");
        });

        binding.chipPython.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipPython.setChecked(true);
            currentLanguageFilter = "python";
            filterAndShowSnippets();
            updateSectionTitle("Python");
        });

        binding.chipJava.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipJava.setChecked(true);
            currentLanguageFilter = "java";
            filterAndShowSnippets();
            updateSectionTitle("Java");
        });

        binding.chipKotlin.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipKotlin.setChecked(true);
            currentLanguageFilter = "kotlin";
            filterAndShowSnippets();
            updateSectionTitle("Kotlin");
        });

        binding.chipTypescript.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipTypescript.setChecked(true);
            currentLanguageFilter = "typescript";
            filterAndShowSnippets();
            updateSectionTitle("TypeScript");
        });

        binding.chipSwift.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipSwift.setChecked(true);
            currentLanguageFilter = "swift";
            filterAndShowSnippets();
            updateSectionTitle("Swift");
        });

        binding.chipGo.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipGo.setChecked(true);
            currentLanguageFilter = "go";
            filterAndShowSnippets();
            updateSectionTitle("Go");
        });
    }

    private void clearChipSelections() {
        binding.chipAll.setChecked(false);
        binding.chipJavascript.setChecked(false);
        binding.chipPython.setChecked(false);
        binding.chipJava.setChecked(false);
        binding.chipKotlin.setChecked(false);
        binding.chipTypescript.setChecked(false);
        binding.chipSwift.setChecked(false);
        binding.chipGo.setChecked(false);
    }

    private void filterAndShowSnippets() {
        if (currentLanguageFilter == null) {
            // Show all snippets
            snippetAdapter.filterList(allSnippets);
            updateUI(allSnippets.size());
        } else {
            // Filter by language
            List<SnippetCard> filtered = new ArrayList<>();
            for (SnippetCard snippet : allSnippets) {
                String lang = snippet.getLanguageBadge();
                if (lang != null && lang.toLowerCase().contains(currentLanguageFilter.toLowerCase())) {
                    filtered.add(snippet);
                }
            }
            snippetAdapter.filterList(filtered);
            updateUI(filtered.size());
        }
    }

    private void updateSectionTitle(String category) {
        if (category.equals("Trending")) {
            binding.tvSectionTitle.setText(R.string.trending_now);
        } else {
            binding.tvSectionTitle.setText(category + " Snippets");
        }
    }

    private void updateUI(int count) {
        if (count == 0) {
            binding.rvSnippets.setVisibility(View.GONE);
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            if (currentLanguageFilter != null) {
                binding.tvEmptyTitle.setText("No " + currentLanguageFilter + " snippets");
                binding.tvEmptyMessage.setText("Be the first to share a " + currentLanguageFilter + " snippet!");
            } else {
                binding.tvEmptyTitle.setText("No snippets found");
                binding.tvEmptyMessage.setText("Be the first to share a snippet\nin this category!");
            }
        } else {
            binding.rvSnippets.setVisibility(View.VISIBLE);
            binding.layoutEmpty.setVisibility(View.GONE);
        }

        String countText = count == 1 ? "1 snippet" : count + " snippets";
        binding.tvSnippetCount.setText(countText);
    }

    private void setupSnippetsRecyclerView() {
        binding.rvSnippets.setLayoutManager(new LinearLayoutManager(this));
        snippetAdapter = new SnippetCardAdapter(new ArrayList<>());
        snippetAdapter.setOnSnippetClickListener(snippet -> {
            Toast.makeText(this, "Clicked: " + snippet.getTitle(), Toast.LENGTH_SHORT).show();
        });
        binding.rvSnippets.setAdapter(snippetAdapter);
    }

    private void loadTrendingSnippets() {
        binding.swipeRefresh.setRefreshing(true);

        dashboardRepository.getTrendingSnippets(30).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.swipeRefresh.setRefreshing(false);
                allSnippets.clear();
                allSnippets.addAll(resource.data);
                filterAndShowSnippets();
                Log.d(TAG, "Loaded " + resource.data.size() + " trending snippets");
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load trending snippets: " + resource.message);
                // Try public snippets as fallback
                loadPublicSnippetsFallback();
            } else if (resource.status == Resource.Status.LOADING) {
                // Keep showing refresh indicator
            }
        });
    }

    private void loadPublicSnippetsFallback() {
        dashboardRepository.getPublicSnippets(30).observe(this, resource -> {
            binding.swipeRefresh.setRefreshing(false);

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allSnippets.clear();
                allSnippets.addAll(resource.data);
                filterAndShowSnippets();
                Log.d(TAG, "Loaded " + resource.data.size() + " public snippets as fallback");
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load public snippets: " + resource.message);
                updateUI(0);
                Toast.makeText(this, "Unable to load snippets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        // No item selected since Explore is not in bottom nav
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
                Intent intent = new Intent(this, FavoritesActivity.class);
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

        // FAB click
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateSnippetActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this screen
        if (allSnippets.isEmpty()) {
            loadTrendingSnippets();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
