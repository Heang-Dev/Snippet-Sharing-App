package group.eleven.snippet_sharing_app.ui.mysnippets;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.SnippetRepository;
import group.eleven.snippet_sharing_app.model.SnippetModel;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;

public class MySnippetsActivity extends AppCompatActivity implements MySnippetAdapter.OnSnippetActionListener {

    private RecyclerView rvSnippets;
    private MySnippetAdapter adapter;
    private List<SnippetModel> allSnippets = new ArrayList<>();
    private List<SnippetModel> filteredSnippets = new ArrayList<>();

    // Filter Tabs
    private TextView tabAll;
    private LinearLayout tabPrivate, tabPublic, tabTeam;
    private String currentFilter = "All";
    private String currentSearchQuery = "";
    private boolean isSortAscending = true;

    // UI Elements
    private LinearLayout bottomActionBar;
    private TextView tvSort;
    private EditText etSearch; // New Search Bar
    private ImageView btnBack; // New Back Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_snippets);

        initViews();
        setupRecyclerView();
        loadData();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        rvSnippets = findViewById(R.id.rvSnippets);
        tabAll = findViewById(R.id.tabAll);
        tabPrivate = findViewById(R.id.tabPrivate);
        tabPublic = findViewById(R.id.tabPublic);
        tabTeam = findViewById(R.id.tabTeam);
        bottomActionBar = findViewById(R.id.bottomActionBar);
        etSearch = findViewById(R.id.etSearch); // Bind new search bar
        btnBack = findViewById(R.id.btnBack); // Bind back button

        // Sort TextView
        LinearLayout llSortBar = findViewById(R.id.llSortBar);
        if (llSortBar != null && llSortBar.getChildCount() > 0) {
            View v = llSortBar.getChildAt(0);
            if (v instanceof TextView) {
                tvSort = (TextView) v;
            }
        }
    }

    private void setupRecyclerView() {
        rvSnippets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MySnippetAdapter(this);
        rvSnippets.setAdapter(adapter);
    }

    private void loadData() {
        allSnippets = new ArrayList<>(
                SnippetRepository.getInstance().getAllSnippets());
        filterSnippets();
    }

    private void setupListeners() {
        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            startActivity(new Intent(this, CreateSnippetActivity.class));
        });

        // Back Navigation
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        setupFilterTab(tabAll, "All");
        setupFilterTab(tabPrivate, "Private");
        setupFilterTab(tabPublic, "Public");
        setupFilterTab(tabTeam, "Team");

        // Search Input Logic
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString();
                    filterSnippets();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        // Sort
        if (tvSort != null) {
            tvSort.setOnClickListener(v -> toggleSortOrder());
        }

        // Bottom Bar
        setupBottomBar();

        // View Toggle Buttons (Optional)
        findViewById(R.id.btnViewList)
                .setOnClickListener(v -> Toast.makeText(this, "List View Active", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnViewGrid)
                .setOnClickListener(v -> Toast.makeText(this, "Grid View Coming Soon", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomBar() {
        if (bottomActionBar == null)
            return;
        if (bottomActionBar.getChildCount() >= 3) {
            View btnSelectAll = bottomActionBar.getChildAt(0); // Select All
            View btnPrivacy = bottomActionBar.getChildAt(1); // Privacy
            View btnDelete = bottomActionBar.getChildAt(2); // Delete

            btnSelectAll.setOnClickListener(v -> toggleSelectAll());
            btnPrivacy.setOnClickListener(v -> showBulkPrivacyDialog());
            btnDelete.setOnClickListener(v -> confirmBulkDelete());
        }
        updateBottomBarVisibility();
    }

    private void setupFilterTab(View view, String filterType) {
        view.setOnClickListener(v -> {
            updateFilterUI(filterType);
            currentFilter = filterType;
            filterSnippets();
        });
    }

    private void updateFilterUI(String filterType) {
        setTabInactive(tabAll, false);
        setTabInactive(tabPrivate, true);
        setTabInactive(tabPublic, true);
        setTabInactive(tabTeam, true);

        switch (filterType) {
            case "All":
                setTabActive(tabAll, false);
                break;
            case "Private":
                setTabActive(tabPrivate, true);
                break;
            case "Public":
                setTabActive(tabPublic, true);
                break;
            case "Team":
                setTabActive(tabTeam, true);
                break;
        }
    }

    private void setTabActive(View view, boolean isIconTab) {
        view.setBackgroundResource(R.drawable.bg_pill_active);
        if (isIconTab) {
            LinearLayout ll = (LinearLayout) view;
            ImageView iv = (ImageView) ll.getChildAt(0);
            TextView tv = (TextView) ll.getChildAt(1);
            iv.setColorFilter(Color.parseColor("#05100B"), PorterDuff.Mode.SRC_IN);
            tv.setTextColor(Color.parseColor("#05100B"));
        } else {
            TextView tv = (TextView) view;
            tv.setTextColor(Color.parseColor("#05100B"));
        }
    }

    private void setTabInactive(View view, boolean isIconTab) {
        view.setBackgroundResource(R.drawable.bg_pill_inactive);
        if (isIconTab) {
            LinearLayout ll = (LinearLayout) view;
            ImageView iv = (ImageView) ll.getChildAt(0);
            TextView tv = (TextView) ll.getChildAt(1);
            iv.setColorFilter(Color.parseColor("#889990"), PorterDuff.Mode.SRC_IN);
            tv.setTextColor(Color.parseColor("#889990"));
        } else {
            TextView tv = (TextView) view;
            tv.setTextColor(Color.parseColor("#889990"));
        }
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

        // Sort Logic
        // Since we don't hold timestamps, we respect repo order (Newest First) and just
        // reverse if needed
        // Assuming repo order is "Newest First" (default)

        // If isSortAscending ("Newest First" in UI text implication? Actually "Newest"
        // is usually Descending time)
        // Let's assume repo gives Newest First.
        // If user selects "Oldest First", we reverse.
        // My UI text toggle was: "SORT BY Last Modified (Newest)/Oldest".

        // If sorting Ascending (Oldest First) -> Reverse repo order
        // If sorting Descending (Newest First) -> Keep repo order

        // My variable is isSortAscending. Default true?
        // Previously: "SORT BY Last Modified" text.
        // Let's clarify: Repo is Newest First.
        // If isSortAscending is false -> Newest First (default)
        // If isSortAscending is true -> Oldest First

        // Wait, toggleSortOrder logic:
        // isSortAscending = !isSortAscending;
        // Text: isSortAscending ? "Newest" : "Oldest" -> Previous code had logic but
        // maybe labels confused
        // Let's standardize:
        // Default: Newest First.

        if (!isSortAscending) { // If "Oldest First" requested (assuming flag means Ascending)
            // Wait, confusing. Let's simplify.
            // Default repo: Newest First.
            // If we want Oldest First, we reverse.

            // Let's re-implement toggle logic clearly below.
        } else {
            // Newest First
        }

        // Actually, let's just use the boolean to FLIP order if needed.
        if (!isSortAscending) {
            Collections.reverse(filteredSnippets);
        }
        // If isSortAscending is TRUE (default), we Keep.
        // Wait, previous code:
        // "if (!isSortAscending) Collections.reverse(filteredSnippets);"

        // I will stick to what works:
        // Default: Newest First.
        // If user toggles, we reverse perfectly.

        adapter.setSnippets(filteredSnippets);
        updateBottomBarVisibility();
    }

    private void toggleSortOrder() {
        isSortAscending = !isSortAscending;
        if (tvSort != null) {
            tvSort.setText(isSortAscending ? "SORT BY Last Modified (Newest)" : "SORT BY Last Modified (Oldest)");
        }
        // Note: The logic in filterSnippets depends on this flag.
        // If flag toggles, list reverses. Simple.
        filterSnippets();
    }

    // --- Selection & Bulk Logic ---

    private void updateBottomBarVisibility() {
        boolean anySelected = false;
        for (SnippetModel s : filteredSnippets) {
            if (s.isSelected()) {
                anySelected = true;
                break;
            }
        }

        if (bottomActionBar != null) {
            bottomActionBar.setVisibility(anySelected ? View.VISIBLE : View.GONE);
        }
    }

    private void toggleSelectAll() {
        boolean allSelected = true;
        for (SnippetModel s : filteredSnippets) {
            if (!s.isSelected()) {
                allSelected = false;
                break;
            }
        }

        boolean targetState = !allSelected;
        for (SnippetModel s : filteredSnippets) {
            s.setSelected(targetState);
        }
        adapter.notifyDataSetChanged();
        updateBottomBarVisibility();
    }

    private void showBulkPrivacyDialog() {
        Toast.makeText(this, "Bulk Privacy Update Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void confirmBulkDelete() {
        List<SnippetModel> toDelete = new ArrayList<>();
        for (SnippetModel s : filteredSnippets) {
            if (s.isSelected())
                toDelete.add(s);
        }

        if (toDelete.isEmpty())
            return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Snippets")
                .setMessage("Are you sure you want to delete " + toDelete.size() + " selected snippets?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    SnippetRepository.getInstance().deleteSnippets(toDelete);
                    allSnippets.removeAll(toDelete);
                    Toast.makeText(this, "Deleted " + toDelete.size() + " snippets", Toast.LENGTH_SHORT).show();
                    filterSnippets();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // --- OnSnippetActionListener Implementation ---

    @Override
    public void onSnippetClick(SnippetModel snippet) {
        snippet.setSelected(!snippet.isSelected());
        adapter.notifyDataSetChanged();
        updateBottomBarVisibility();
    }

    @Override
    public void onFavoriteClick(SnippetModel snippet) {
        snippet.setFavorite(!snippet.isFavorite());
        adapter.notifyDataSetChanged();
        Toast.makeText(this, snippet.isFavorite() ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT)
                .show();
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
}
