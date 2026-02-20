package group.eleven.snippet_sharing_app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.model.SearchResult;
import group.eleven.snippet_sharing_app.ui.profile.ProfileActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rvSearchResults;
    private SearchResultAdapter adapter;
    private EditText etSearch;
    private CircleImageView ivProfile;
    private List<SearchResult> allResults;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        loadProfileImage();
        setupMockData();
        setupListeners();
    }

    private void initViews() {
        sessionManager = new SessionManager(this);

        rvSearchResults = findViewById(R.id.rvSearchResults);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultAdapter();
        rvSearchResults.setAdapter(adapter);

        etSearch = findViewById(R.id.etSearch);
        ivProfile = findViewById(R.id.ivProfile);
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

    private void setupMockData() {
        allResults = new ArrayList<>();
        allResults.add(new SearchResult("1", "JWT Auth Middleware", "Security • Middleware", "TypeScript", "#3178C6",
                "const verifyToken = (req, res, next) => {\n  const token = req.header('auth-token');\n  if (!token) return res.status(401).send('Access Denied');\n  // Verify the token matches secret\n  try { ... }\n}",
                "@dev_jane", 84, 12, false, "3d ago"));

        allResults.add(new SearchResult("2", "OAuth2 Decorator", "Auth • Backend", "Python", "#3776AB",
                "def require_auth(f):\n  @wraps(f)\n  def decorated(*args, **kwargs):\n    auth = request.authorization\n    if not auth or not check_auth(auth):\n      return authenticate()\n    return f(*args, **kwargs)",
                "@alex_code", 42, 5, true, "1w ago"));

        allResults.add(new SearchResult("3", "Session Manager Struct", "Database • Redis", "Go", "#00ADD8",
                "type Session struct {\n  ID     string `json:\"id\"`\n  UserID string `json:\"user_id\"`\n  Expiry int64  `json:\"expiry\"`\n}\n\nfunc (s *Session) IsExpired() bool {\n  return time.Now().Unix() > s.Expiry\n}",
                "@gopher_master", 120, 45, false, "2mo ago"));

        adapter.setItems(allResults);
        updateHeaderCount(allResults.size());
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
        findViewById(R.id.btnClear).setOnClickListener(v -> etSearch.setText(""));

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

        // Search text change listener
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

        // Handle keyboard search action
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterResults(etSearch.getText().toString());
                return true;
            }
            return false;
        });

        // Load More click
        findViewById(R.id.btnLoadMore).setOnClickListener(v -> {
            Toast.makeText(this, "Loading more results...", Toast.LENGTH_SHORT).show();
        });
    }

    private void filterResults(String query) {
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
    }

    private void updateHeaderCount(int count) {
        TextView tvCount = findViewById(R.id.tvResultCount);
        if (tvCount != null) {
            tvCount.setText(getString(R.string.results_found, count));
        }
    }
}
