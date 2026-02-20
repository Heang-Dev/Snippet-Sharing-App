package group.eleven.snippet_sharing_app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.repository.DashboardRepository;
import group.eleven.snippet_sharing_app.data.repository.SearchRepository;
import group.eleven.snippet_sharing_app.model.SearchResult;
import group.eleven.snippet_sharing_app.ui.profile.ProfileActivity;
import group.eleven.snippet_sharing_app.utils.KeyboardUtils;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.utils.SessionManager;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private static final long SEARCH_DEBOUNCE_MS = 500;

    private RecyclerView rvSearchResults;
    private SearchResultAdapter adapter;
    private EditText etSearch;
    private CircleImageView ivProfile;
    private TextView tvResultCount;
    private List<SearchResult> allResults;
    private SessionManager sessionManager;
    private SearchRepository searchRepository;
    private DashboardRepository dashboardRepository;
    private Handler searchHandler;
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        loadProfileImage();
        loadInitialSnippets();
        setupListeners();
    }

    private void initViews() {
        sessionManager = new SessionManager(this);
        searchRepository = new SearchRepository(this);
        dashboardRepository = new DashboardRepository(this);
        searchHandler = new Handler(Looper.getMainLooper());
        allResults = new ArrayList<>();

        rvSearchResults = findViewById(R.id.rvSearchResults);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultAdapter();
        rvSearchResults.setAdapter(adapter);

        etSearch = findViewById(R.id.etSearch);
        ivProfile = findViewById(R.id.ivProfile);
        tvResultCount = findViewById(R.id.tvResultCount);
    }

    private void loadProfileImage() {
        User user = sessionManager.getUser();
        if (user != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(ivProfile);
        }
    }

    /**
     * Load initial trending/public snippets when search screen opens
     */
    private void loadInitialSnippets() {
        showLoading(true);

        dashboardRepository.getTrendingSnippets(20).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allResults.clear();
                for (group.eleven.snippet_sharing_app.data.model.SnippetCard card : resource.data) {
                    allResults.add(mapSnippetCardToSearchResult(card));
                }
                adapter.setItems(allResults);
                updateHeaderCount(allResults.size());
                showLoading(false);
                showEmptyState(allResults.isEmpty());
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load initial snippets: " + resource.message);
                showLoading(false);
                showEmptyState(true);
            }
        });
    }

    /**
     * Search snippets using API
     */
    private void performSearch(String query) {
        if (query.trim().isEmpty()) {
            loadInitialSnippets();
            return;
        }

        showLoading(true);

        searchRepository.searchSnippets(query, 30).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allResults.clear();
                for (Snippet snippet : resource.data) {
                    allResults.add(mapSnippetToSearchResult(snippet));
                }
                adapter.setItems(allResults);
                updateHeaderCount(allResults.size());
                showLoading(false);
                showEmptyState(allResults.isEmpty());
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Search failed: " + resource.message);
                showLoading(false);
                // Fall back to local filtering if API fails
                filterResultsLocally(query);
            }
        });
    }

    /**
     * Map Snippet model to SearchResult UI model
     */
    private SearchResult mapSnippetToSearchResult(Snippet snippet) {
        String languageName = snippet.getLanguageName();
        String languageColor = getLanguageColor(languageName);
        String subtitle = snippet.getDescription() != null ? snippet.getDescription() : "";
        String authorUsername = snippet.getUser() != null ? snippet.getUser().getUsername() : "user";
        String authorName = "@" + authorUsername;
        String code = snippet.getCode() != null ? snippet.getCode() : "";
        String timeAgo = formatTimeAgo(snippet.getCreatedAt());

        return new SearchResult(
                snippet.getId(),
                snippet.getTitle() != null ? snippet.getTitle() : "Untitled",
                subtitle,
                languageName,
                languageColor,
                code.length() > 300 ? code.substring(0, 300) + "..." : code,
                authorName,
                snippet.getFavoriteCount(),
                snippet.getCommentCount(),
                !"public".equals(snippet.getPrivacy()),
                timeAgo
        );
    }

    /**
     * Map SnippetCard model to SearchResult UI model
     */
    private SearchResult mapSnippetCardToSearchResult(group.eleven.snippet_sharing_app.data.model.SnippetCard card) {
        String languageBadge = card.getLanguageBadge() != null ? card.getLanguageBadge() : "Code";
        String languageColor = getLanguageColor(languageBadge);
        String code = card.getCodePreview() != null ? card.getCodePreview() : "";

        return new SearchResult(
                card.getId(),
                card.getTitle() != null ? card.getTitle() : "Untitled",
                card.getDescription() != null ? card.getDescription() : "",
                languageBadge,
                languageColor,
                code,
                "@" + (card.getAuthorUsername() != null ? card.getAuthorUsername() : "user"),
                card.getLikesCount(),
                card.getCommentsCount(),
                !"public".equals(card.getVisibility()),
                card.getUpdatedTime() != null ? card.getUpdatedTime() : ""
        );
    }

    /**
     * Get color for programming language
     */
    private String getLanguageColor(String language) {
        if (language == null) return "#6B7280";
        switch (language.toLowerCase()) {
            case "javascript": return "#F7DF1E";
            case "typescript": return "#3178C6";
            case "python": return "#3776AB";
            case "java": return "#B07219";
            case "kotlin": return "#A97BFF";
            case "swift": return "#FA7343";
            case "go": return "#00ADD8";
            case "rust": return "#DEA584";
            case "c++": case "cpp": return "#00599C";
            case "c#": case "csharp": return "#239120";
            case "php": return "#777BB4";
            case "ruby": return "#CC342D";
            case "html": return "#E34F26";
            case "css": return "#1572B6";
            case "sql": return "#336791";
            default: return "#6B7280";
        }
    }

    /**
     * Format timestamp to relative time
     */
    private String formatTimeAgo(String timestamp) {
        if (timestamp == null) return "";
        // Simple formatting - in production use a proper library
        try {
            // Just return a simple format for now
            return "recently";
        } catch (Exception e) {
            return "";
        }
    }

    private void setupListeners() {
        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Profile image click - navigate to profile
        ivProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // Save search button
        findViewById(R.id.btnSaveSearch).setOnClickListener(v -> {
            Toast.makeText(this, "Search saved!", Toast.LENGTH_SHORT).show();
        });

        // Clear search button
        findViewById(R.id.btnClear).setOnClickListener(v -> {
            etSearch.setText("");
            loadInitialSnippets();
        });

        // Language filter button
        findViewById(R.id.btnLanguageFilter).setOnClickListener(v -> {
            Toast.makeText(this, "Language filter coming soon", Toast.LENGTH_SHORT).show();
        });

        // Sort filter button
        findViewById(R.id.btnSortFilter).setOnClickListener(v -> {
            Toast.makeText(this, "Sort options coming soon", Toast.LENGTH_SHORT).show();
        });

        // Filter button in search bar
        findViewById(R.id.btnFilter).setOnClickListener(v -> {
            Toast.makeText(this, "Advanced filters coming soon", Toast.LENGTH_SHORT).show();
        });

        // Search text change listener with debounce
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel any pending search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Schedule new search with debounce
                searchRunnable = () -> performSearch(s.toString());
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Handle keyboard search action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Cancel debounce and search immediately
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                performSearch(etSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Load More click
        findViewById(R.id.btnLoadMore).setOnClickListener(v -> {
            Toast.makeText(this, "Loading more results...", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Filter results locally (fallback when API fails)
     */
    private void filterResultsLocally(String query) {
        if (query.isEmpty()) {
            adapter.setItems(allResults);
            updateHeaderCount(allResults.size());
            return;
        }

        List<SearchResult> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (SearchResult item : allResults) {
            if (item.getTitle().toLowerCase().contains(lowerQuery) ||
                    item.getLanguage().toLowerCase().contains(lowerQuery) ||
                    item.getSubtitle().toLowerCase().contains(lowerQuery) ||
                    item.getCodeSnippet().toLowerCase().contains(lowerQuery)) {
                filtered.add(item);
            }
        }
        adapter.setItems(filtered);
        updateHeaderCount(filtered.size());
        showEmptyState(filtered.isEmpty());
    }

    private void updateHeaderCount(int count) {
        if (tvResultCount != null) {
            tvResultCount.setText(getString(R.string.results_found, count));
        }
    }

    private void showLoading(boolean show) {
        // No progress bar in layout - could add one later if needed
    }

    private void showEmptyState(boolean show) {
        // Just update the result count to show "0 results found" when empty
        if (show) {
            updateHeaderCount(0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        KeyboardUtils.handleTouchOutsideEditText(this, event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
