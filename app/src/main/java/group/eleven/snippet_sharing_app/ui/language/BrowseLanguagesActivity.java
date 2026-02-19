package group.eleven.snippet_sharing_app.ui.language;

import android.os.Bundle;
import android.widget.GridLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.ui.profile.AccountSettingsActivity;
import group.eleven.snippet_sharing_app.ui.profile.NotificationSettingsActivity;

public class BrowseLanguagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_languages);

        // Setup Header
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Setup Search
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.setHint("Find a language...");

        // Setup Popular Grid
        setupPopularGrid();

        // Setup All Languages List
        setupAllLanguagesList();

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void setupPopularGrid() {
        GridLayout grid = findViewById(R.id.gridPopular);
        // Clean implementation: Inflate card views for top 4 languages
        // JS
        addPopularCard(grid, "JS", "JavaScript", "2,403 snippets", "#FFD600");
        // Python
        addPopularCard(grid, "Py", "Python", "1,892 snippets", "#2196F3");
        // TS - TypeScript uses blue-ish text usually, using a nice blue
        addPopularCard(grid, "TS", "TypeScript", "1,540 snippets", "#29B6F6");
        // Rust
        addPopularCard(grid, "Rs", "Rust", "982 snippets", "#FF5722");
    }

    private void addPopularCard(GridLayout grid, String initial, String name, String count, String colorHex) {
        View card = getLayoutInflater().inflate(R.layout.item_language_popular_card, grid, false);

        // Use LayoutParams to distribute evenly
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        // Add margins
        params.setMargins(8, 8, 8, 24);
        card.setLayoutParams(params);

        TextView tvInitial = card.findViewById(R.id.tvPopLangInitial);
        TextView tvName = card.findViewById(R.id.tvPopLangName);
        TextView tvCount = card.findViewById(R.id.tvPopSnippetCount);

        tvInitial.setText(initial);
        tvInitial.setTextColor(android.graphics.Color.parseColor(colorHex));
        tvName.setText(name);
        tvCount.setText(count);

        grid.addView(card);
    }

    private void setupAllLanguagesList() {
        RecyclerView rv = findViewById(R.id.rvLanguages);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<LanguageItem> items = new ArrayList<>();
        // C Section
        items.add(new LanguageItem("C")); // Header
        items.add(new LanguageItem("C", "C / C++", "450", "#29B6F6"));
        items.add(new LanguageItem("C#", "C#", "621", "#9C27B0"));
        items.add(new LanguageItem("Cs", "CSS / SCSS", "3.2k", "#00BCD4"));

        // G Section
        items.add(new LanguageItem("G"));
        items.add(new LanguageItem("Go", "Go", "890", "#00BCD4"));
        items.add(new LanguageItem("Gr", "GraphQL", "230", "#E91E63"));

        // H Section
        items.add(new LanguageItem("H"));
        items.add(new LanguageItem("Ht", "HTML", "1.1k", "#FF5722"));
        items.add(new LanguageItem("Hs", "Haskell", "98", "#9C27B0"));

        // J Section
        items.add(new LanguageItem("J"));
        items.add(new LanguageItem("Jv", "Java", "1.2k", "#F44336"));

        // S Section
        items.add(new LanguageItem("S"));
        items.add(new LanguageItem("Sw", "Swift", "567", "#2196F3"));
        items.add(new LanguageItem("Sh", "Shell / Bash", "1.5k", "#4CAF50"));
        items.add(new LanguageItem("Sq", "SQL", "21k", "#E91E63"));

        rv.setAdapter(new AllLanguageAdapter(items));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        FloatingActionButton fab = findViewById(R.id.fab);

        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Go back to Home
                    android.content.Intent intent = new android.content.Intent(this, HomeActivity.class);
                    intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_teams) {
                    startActivity(new android.content.Intent(this, group.eleven.snippet_sharing_app.ui.team.TeamsListActivity.class));
                    return false;
                } else if (id == R.id.nav_snippets) {
                    startActivity(new android.content.Intent(this, MySnippetsActivity.class));
                    return false;
                } else if (id == R.id.nav_profile) {
                    startActivity(new android.content.Intent(this, group.eleven.snippet_sharing_app.ui.profile.ProfileActivity.class));
                    return false;
                }
                return false;
            });
        }

        if (fab != null) {
            fab.setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, CreateSnippetActivity.class));
            });
        }
    }

    // Inner classes for Adapter
    private static class LanguageItem {
        boolean isHeader;
        String sectionLetter;

        String initial;
        String name;
        String count;
        String color;

        // Constructor for Header
        public LanguageItem(String sectionLetter) {
            this.isHeader = true;
            this.sectionLetter = sectionLetter;
        }

        // Constructor for Item
        public LanguageItem(String initial, String name, String count, String color) {
            this.isHeader = false;
            this.initial = initial;
            this.name = name;
            this.count = count;
            this.color = color;
        }
    }

    private class AllLanguageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
        private List<LanguageItem> items;

        public AllLanguageAdapter(List<LanguageItem> items) {
            this.items = items;
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).isHeader ? TYPE_HEADER : TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View v = getLayoutInflater().inflate(R.layout.item_language_section_header, parent, false);
                return new HeaderViewHolder(v);
            } else {
                View v = getLayoutInflater().inflate(R.layout.item_language_row_browse, parent, false);
                return new ItemViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            LanguageItem item = items.get(position);
            if (holder instanceof HeaderViewHolder) {
                ((HeaderViewHolder) holder).tvHeader.setText(item.sectionLetter);
            } else if (holder instanceof ItemViewHolder) {
                ItemViewHolder h = (ItemViewHolder) holder;
                h.tvInitial.setText(item.initial);
                h.tvInitial.setTextColor(android.graphics.Color.parseColor(item.color));
                h.tvName.setText(item.name);
                h.tvCount.setText(item.count);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView tvHeader;

            HeaderViewHolder(View v) {
                super(v);
                tvHeader = v.findViewById(R.id.tvSectionHeader);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView tvInitial, tvName, tvCount;

            ItemViewHolder(View v) {
                super(v);
                tvInitial = v.findViewById(R.id.tvLangIcon);
                tvName = v.findViewById(R.id.tvLangName);
                tvCount = v.findViewById(R.id.tvSnippetCount);
            }
        }
    }
}
