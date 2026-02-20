package group.eleven.snippet_sharing_app.ui.explore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.repository.DashboardRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityExploreBinding;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.ui.home.SnippetCardAdapter;
import group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity;
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

        loadTrendingSnippets();
    }

    private void setupStatusBar() {
        android.view.Window window = getWindow();
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(R.attr.surfaceColor, typedValue, true);
        int statusBarColor;
        if (typedValue.resourceId != 0) {
            statusBarColor = androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            statusBarColor = typedValue.data;
        }
        window.setStatusBarColor(statusBarColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            boolean isLightBackground = isColorLight(statusBarColor);
            controller.setAppearanceLightStatusBars(isLightBackground);
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

    private void setupLanguageChips() {
        // Set "All" as initially selected
        binding.chipAll.setChecked(true);

        // Language chip click listeners
        binding.chipAll.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipAll.setChecked(true);
            loadTrendingSnippets();
        });

        binding.chipJavascript.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipJavascript.setChecked(true);
            filterByLanguage("javascript");
        });

        binding.chipPython.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipPython.setChecked(true);
            filterByLanguage("python");
        });

        binding.chipJava.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipJava.setChecked(true);
            filterByLanguage("java");
        });

        binding.chipKotlin.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipKotlin.setChecked(true);
            filterByLanguage("kotlin");
        });

        binding.chipTypescript.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipTypescript.setChecked(true);
            filterByLanguage("typescript");
        });
    }

    private void clearChipSelections() {
        binding.chipAll.setChecked(false);
        binding.chipJavascript.setChecked(false);
        binding.chipPython.setChecked(false);
        binding.chipJava.setChecked(false);
        binding.chipKotlin.setChecked(false);
        binding.chipTypescript.setChecked(false);
    }

    private void filterByLanguage(String language) {
        // Filter the loaded snippets by language
        List<SnippetCard> filtered = new ArrayList<>();
        for (SnippetCard snippet : allSnippets) {
            if (snippet.getLanguageBadge().toLowerCase().contains(language.substring(0, 2).toLowerCase())) {
                filtered.add(snippet);
            }
        }
        snippetAdapter.filterList(filtered);

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No snippets found for " + language, Toast.LENGTH_SHORT).show();
        }
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
        dashboardRepository.getTrendingSnippets(20).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allSnippets.clear();
                allSnippets.addAll(resource.data);
                snippetAdapter.filterList(resource.data);
                Log.d(TAG, "Loaded " + resource.data.size() + " trending snippets");
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load trending snippets: " + resource.message);
                // Try public snippets as fallback
                loadPublicSnippetsFallback();
            }
        });
    }

    private void loadPublicSnippetsFallback() {
        dashboardRepository.getPublicSnippets(20).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allSnippets.clear();
                allSnippets.addAll(resource.data);
                snippetAdapter.filterList(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load public snippets: " + resource.message);
                Toast.makeText(this, "Failed to load snippets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        // No item selected since Explore is not in bottom nav anymore
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
                Intent intent = new Intent(this, group.eleven.snippet_sharing_app.ui.favorites.FavoritesActivity.class);
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
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
