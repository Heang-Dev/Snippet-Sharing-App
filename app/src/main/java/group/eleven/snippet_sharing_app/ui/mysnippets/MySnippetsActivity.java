package group.eleven.snippet_sharing_app.ui.mysnippets;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.SnippetRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityMySnippetsBinding;
import group.eleven.snippet_sharing_app.model.SnippetModel;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;

public class MySnippetsActivity extends AppCompatActivity implements MySnippetAdapter.OnSnippetActionListener {

    private ActivityMySnippetsBinding binding;
    private MySnippetAdapter adapter;
    private List<SnippetModel> allSnippets = new ArrayList<>();
    private List<SnippetModel> filteredSnippets = new ArrayList<>();

    private String currentFilter = "All";
    private String currentSearchQuery = "";
    private boolean isSortAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMySnippetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupStatusBar();
        setupToolbar();
        setupRecyclerView();
        setupSearchBar();
        setupFilterChips();
        setupSortLabel();
        setupSwipeRefresh();
        setupEmptyState();

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void setupStatusBar() {
        Window window = getWindow();
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.surfaceColor, typedValue, true);
        int surfaceColor;
        if (typedValue.resourceId != 0) {
            surfaceColor = ContextCompat.getColor(this, typedValue.resourceId);
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

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateSnippetActivity.class));
        });
    }

    private void setupRecyclerView() {
        binding.rvSnippets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MySnippetAdapter(this);
        binding.rvSnippets.setAdapter(adapter);
    }

    private void setupSearchBar() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                filterSnippets();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterChips() {
        binding.chipAll.setChecked(true);

        binding.chipAll.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipAll.setChecked(true);
            currentFilter = "All";
            filterSnippets();
        });

        binding.chipPrivate.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipPrivate.setChecked(true);
            currentFilter = "Private";
            filterSnippets();
        });

        binding.chipPublic.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipPublic.setChecked(true);
            currentFilter = "Public";
            filterSnippets();
        });

        binding.chipTeam.setOnClickListener(v -> {
            clearChipSelections();
            binding.chipTeam.setChecked(true);
            currentFilter = "Team";
            filterSnippets();
        });
    }

    private void clearChipSelections() {
        binding.chipAll.setChecked(false);
        binding.chipPrivate.setChecked(false);
        binding.chipPublic.setChecked(false);
        binding.chipTeam.setChecked(false);
    }

    private void setupSortLabel() {
        binding.tvSortLabel.setOnClickListener(v -> toggleSortOrder());
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadData);
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
    }

    private void setupEmptyState() {
        binding.btnCreateFirst.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateSnippetActivity.class));
        });
    }

    private void loadData() {
        binding.swipeRefresh.setRefreshing(false);
        allSnippets = new ArrayList<>(SnippetRepository.getInstance().getAllSnippets());
        filterSnippets();
    }

    private void filterSnippets() {
        filteredSnippets.clear();
        for (SnippetModel s : allSnippets) {
            boolean matchesType = currentFilter.equals("All") || s.getPrivacy().equalsIgnoreCase(currentFilter);
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    s.getTitle().toLowerCase().contains(currentSearchQuery.toLowerCase()) ||
                    s.getLanguage().toLowerCase().contains(currentSearchQuery.toLowerCase());

            if (matchesType && matchesSearch) {
                filteredSnippets.add(s);
            }
        }

        if (!isSortAscending) {
            Collections.reverse(filteredSnippets);
        }

        adapter.setSnippets(filteredSnippets);
        updateUI();
    }

    private void updateUI() {
        int count = filteredSnippets.size();
        String countText = count == 1 ? "1 snippet" : count + " snippets";
        binding.tvSnippetCount.setText(countText);

        if (count == 0) {
            binding.rvSnippets.setVisibility(View.GONE);
            binding.layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.rvSnippets.setVisibility(View.VISIBLE);
            binding.layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void toggleSortOrder() {
        isSortAscending = !isSortAscending;
        binding.tvSortLabel.setText(isSortAscending ? "Last Modified" : "Oldest First");
        filterSnippets();
    }

    // --- OnSnippetActionListener Implementation ---

    @Override
    public void onSnippetClick(SnippetModel snippet) {
        // Open snippet detail or edit
        Intent intent = new Intent(this, CreateSnippetActivity.class);
        intent.putExtra("SNIPPET_ID", snippet.getId());
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(SnippetModel snippet) {
        snippet.setFavorite(!snippet.isFavorite());
        adapter.notifyDataSetChanged();
        Toast.makeText(this, snippet.isFavorite() ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(SnippetModel snippet) {
        Intent intent = new Intent(this, CreateSnippetActivity.class);
        intent.putExtra("SNIPPET_ID", snippet.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(SnippetModel snippet) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Snippet")
                .setMessage("Are you sure you want to delete '" + snippet.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    SnippetRepository.getInstance().deleteSnippet(snippet);
                    allSnippets.remove(snippet);
                    filterSnippets();
                    Toast.makeText(this, "Snippet deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onShareClick(SnippetModel snippet) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBody = snippet.getTitle() + " (" + snippet.getLanguage() + ")\n\n" + snippet.getCode();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, snippet.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Share Snippet via"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
