package group.eleven.snippet_sharing_app.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.model.SearchResult;
import group.eleven.snippet_sharing_app.ui.profile.AccountSettingsActivity;
import group.eleven.snippet_sharing_app.ui.profile.NotificationSettingsActivity;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rvSearchResults;
    private SearchResultAdapter adapter;
    private EditText etSearch;
    private List<SearchResult> allResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupMockData();
        setupListeners();
    }

    private void initViews() {
        rvSearchResults = findViewById(R.id.rvSearchResults);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultAdapter();
        rvSearchResults.setAdapter(adapter);

        etSearch = findViewById(R.id.etSearch);
    }

    private void setupMockData() {
        allResults = new ArrayList<>();
        allResults.add(new SearchResult("1", "JWT Auth Middleware", "Security • Middleware", "TypeScript", "#3178C6",
                "const verifyToken = (req, res, next) => {\n  const token = req.header('auth-token');\n  if (!token) return res.status(401).send('Access Denied');\n  // Verify the token matches secret\n  try { ... }\n}",
                "@dev_jane", 84, 12, false, "3d ago"));

        allResults.add(new SearchResult("2", "OAuth2 Decorator", "Auth • Backend", "Python", "#3776AB",
                "def require_auth(f):\n  @wraps(f)\n  def decorated(*args, **kwargs):\n    auth = request.authorization\n    if not auth or not check_auth(auth):\n      return authenticate()\n    return f(*args, **kwargs)",
                "@alex_code", 42, 5, true, "1w ago"));

        allResults.add(new SearchResult("3", "Session Manager Struct", "Database • Redis", "Go", "#00E5FF",
                "type Session struct {\n  ID     string `json:\"id\"`\n  UserID string `json:\"user_id\"`\n  Expiry int64  `json:\"expiry\"`\n}\n\nfunc (s *Session) IsExpired() bool {\n  return time.Now().Unix() > s.Expiry\n}",
                "@gopher_master", 120, 45, false, "2mo ago"));

        adapter.setItems(allResults);
        updateHeaderCount(allResults.size());
    }

    private void setupListeners() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        // XML structure changed, btnSaveSearch is LinearLayout for layout, assumes
        // clickable?
        // But in new XML it is just a view group for display in scrollview.
        // If user wants it clickable, set id on it. I set id btnSaveSearch on the
        // LinearLayout.
        findViewById(R.id.btnSaveSearch)
                .setOnClickListener(v -> Toast.makeText(this, "Search saved!", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnClear).setOnClickListener(v -> etSearch.setText(""));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterResults(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Load More click
        findViewById(R.id.btnLoadMore).setOnClickListener(v -> {
            Toast.makeText(this, "Loading more results...", Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(
                R.id.bottomNavigation);
        com.google.android.material.floatingactionbutton.FloatingActionButton fab = findViewById(R.id.fab);

        if (bottomNav != null) {
            // Set Home as selected when coming from search
            bottomNav.setSelectedItemId(R.id.nav_home);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    // Go to Home
                    android.content.Intent intent = new android.content.Intent(this,
                            group.eleven.snippet_sharing_app.ui.home.HomeActivity.class);
                    intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_teams) {
                    android.content.Intent intent = new android.content.Intent(this,
                            group.eleven.snippet_sharing_app.ui.team.TeamsListActivity.class);
                    startActivity(intent);
                    return false;
                } else if (id == R.id.nav_snippets) {
                    android.content.Intent intent = new android.content.Intent(this,
                            group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity.class);
                    startActivity(intent);
                    return false;
                } else if (id == R.id.nav_profile) {
                    android.content.Intent intent = new android.content.Intent(this,
                            group.eleven.snippet_sharing_app.ui.profile.ProfileActivity.class);
                    startActivity(intent);
                    return false;
                }
                return false;
            });
        }

        if (fab != null) {
            fab.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(this,
                        group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity.class);
                startActivity(intent);
            });
        }
    }

    private void filterResults(String query) {
        List<SearchResult> filtered = new ArrayList<>();
        for (SearchResult item : allResults) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    item.getLanguage().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.setItems(filtered);
        updateHeaderCount(filtered.size());
    }

    private void updateHeaderCount(int count) {
        TextView tvCount = findViewById(R.id.tvResultCount); // Ensure ID matches XML
        if (tvCount != null) {
            tvCount.setText(count + " results found");
        }
    }
}
