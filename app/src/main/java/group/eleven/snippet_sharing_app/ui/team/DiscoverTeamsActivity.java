package group.eleven.snippet_sharing_app.ui.team;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;

public class DiscoverTeamsActivity extends AppCompatActivity implements DiscoverTeamAdapter.OnJoinClickListener {

    private MaterialToolbar toolbar;
    private EditText etSearch;
    private RecyclerView rvTeams;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage;

    private DiscoverTeamAdapter adapter;
    private TeamViewModel teamViewModel;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_teams);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupSearch();

        // Load initial results
        searchTeams("");
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearch);
        rvTeams = findViewById(R.id.rvTeams);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new DiscoverTeamAdapter(this);
        rvTeams.setLayoutManager(new LinearLayoutManager(this));
        rvTeams.setAdapter(adapter);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        teamViewModel.getDiscoverTeamsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                rvTeams.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.GONE);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                if (resource.getData() != null && !resource.getData().isEmpty()) {
                    adapter.setTeams(resource.getData());
                    rvTeams.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                } else {
                    rvTeams.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    tvEmptyMessage.setText("No public teams found");
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyMessage.setText("Error: " + resource.getMessage());
            }
        });

        teamViewModel.getRequestJoinTeamResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(this, "Join request sent!", Toast.LENGTH_SHORT).show();
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> searchTeams(s.toString().trim());
                searchHandler.postDelayed(searchRunnable, 400);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchTeams(String query) {
        Map<String, String> filters = new HashMap<>();
        if (!query.isEmpty()) {
            filters.put("search", query);
        }
        teamViewModel.discoverTeams(filters);
    }

    @Override
    public void onJoinClick(Team team, int position) {
        Map<String, String> body = new HashMap<>();
        teamViewModel.requestJoinTeam(team.getId(), body);
        adapter.updateTeamRequestStatus(position);
    }
}
