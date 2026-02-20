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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Language;
import group.eleven.snippet_sharing_app.data.repository.LanguageRepository;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.ui.profile.AccountSettingsActivity;
import group.eleven.snippet_sharing_app.ui.profile.NotificationSettingsActivity;

public class BrowseLanguagesActivity extends AppCompatActivity {

    private LanguageRepository languageRepository;
    private GridLayout gridPopular;
    private RecyclerView rvLanguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_languages);

        languageRepository = new LanguageRepository(this);

        // Setup Header
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Setup Search
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.setHint("Find a language...");

        gridPopular = findViewById(R.id.gridPopular);
        rvLanguages = findViewById(R.id.rvLanguages);

        // Load languages from API
        loadPopularLanguages();
        loadAllLanguages();

        // Setup Bottom Navigation
        setupBottomNavigation();
    }

    private void loadPopularLanguages() {
        languageRepository.getPopularLanguages().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                gridPopular.removeAllViews();
                int count = 0;
                for (Language lang : resource.data) {
                    if (count >= 4) break;
                    String initial = getLanguageInitial(lang.getName());
                    String color = lang.getColor() != null ? lang.getColor() : "#6B7280";
                    String snippetCount = formatCount(lang.getSnippetCount()) + " snippets";
                    addPopularCard(gridPopular, initial, lang.getDisplayName(), snippetCount, color);
                    count++;
                }
            } else if (resource.status == Resource.Status.ERROR) {
                // Fall back to hardcoded popular languages
                setupPopularGridFallback();
            }
        });
    }

    private void loadAllLanguages() {
        languageRepository.getLanguages().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                List<LanguageItem> items = convertToLanguageItems(resource.data);
                rvLanguages.setLayoutManager(new LinearLayoutManager(this));
                rvLanguages.setAdapter(new AllLanguageAdapter(items));
            } else if (resource.status == Resource.Status.ERROR) {
                // Fall back to hardcoded language list
                setupAllLanguagesListFallback();
            }
        });
    }

    private List<LanguageItem> convertToLanguageItems(List<Language> languages) {
        // Sort languages alphabetically
        Collections.sort(languages, (a, b) ->
            a.getName().compareToIgnoreCase(b.getName()));

        List<LanguageItem> items = new ArrayList<>();
        String currentLetter = "";

        for (Language lang : languages) {
            String firstLetter = lang.getName().substring(0, 1).toUpperCase();
            if (!firstLetter.equals(currentLetter)) {
                items.add(new LanguageItem(firstLetter)); // Header
                currentLetter = firstLetter;
            }
            String initial = getLanguageInitial(lang.getName());
            String color = lang.getColor() != null ? lang.getColor() : "#6B7280";
            String count = formatCount(lang.getSnippetCount());
            items.add(new LanguageItem(initial, lang.getDisplayName(), count, color));
        }

        return items;
    }

    private String getLanguageInitial(String name) {
        if (name == null || name.isEmpty()) return "?";
        if (name.length() <= 2) return name;
        // Handle special cases
        switch (name.toLowerCase()) {
            case "javascript": return "JS";
            case "typescript": return "TS";
            case "python": return "Py";
            case "c++": case "cpp": return "C++";
            case "c#": case "csharp": return "C#";
            case "golang": case "go": return "Go";
            case "rust": return "Rs";
            case "swift": return "Sw";
            case "kotlin": return "Kt";
            case "shell": case "bash": return "Sh";
            case "html": return "Ht";
            case "css": case "scss": return "Cs";
            default: return name.substring(0, 2);
        }
    }

    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format("%.1fk", count / 1000.0);
        }
        return String.valueOf(count);
    }

    private void setupPopularGridFallback() {
        gridPopular.removeAllViews();
        addPopularCard(gridPopular, "JS", "JavaScript", "2,403 snippets", "#FFD600");
        addPopularCard(gridPopular, "Py", "Python", "1,892 snippets", "#2196F3");
        addPopularCard(gridPopular, "TS", "TypeScript", "1,540 snippets", "#29B6F6");
        addPopularCard(gridPopular, "Rs", "Rust", "982 snippets", "#FF5722");
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

    private void setupAllLanguagesListFallback() {
        rvLanguages.setLayoutManager(new LinearLayoutManager(this));

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

        rvLanguages.setAdapter(new AllLanguageAdapter(items));
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
                } else if (id == R.id.nav_favorites) {
                    startActivity(new android.content.Intent(this, group.eleven.snippet_sharing_app.ui.favorites.FavoritesActivity.class));
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
