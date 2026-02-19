package group.eleven.snippet_sharing_app.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.TeamSnippet;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import static group.eleven.snippet_sharing_app.data.repository.AuthRepository.Resource; // Static import for Resource
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;

public class TeamSnippetsActivity extends AppCompatActivity implements TeamSnippetAdapter.OnTeamSnippetClickListener {

    public static final String EXTRA_TEAM_ID = "extra_team_id";
    public static final String EXTRA_TEAM_NAME = "extra_team_name";

    private MaterialToolbar toolbar;
    private Spinner spinnerLanguageFilter;
    private Spinner spinnerSortBy;
    private RecyclerView rvTeamSnippetsList;
    private TextView tvNoSnippets;
    private ProgressBar progressBar;
    private FloatingActionButton fabCreateTeamSnippet;

    private TeamSnippetAdapter teamSnippetAdapter;
    private TeamViewModel teamViewModel;
    private String teamId;
    private String teamName; // For toolbar title

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_snippets);

        teamId = getIntent().getStringExtra(EXTRA_TEAM_ID);
        teamName = getIntent().getStringExtra(EXTRA_TEAM_NAME);

        if (teamId == null) {
            Toast.makeText(this, "Team ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupSpinners();
        setupRecyclerView();
        setupViewModel();
        setupListeners();

        fetchTeamSnippets();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        spinnerLanguageFilter = findViewById(R.id.spinner_language_filter);
        spinnerSortBy = findViewById(R.id.spinner_sort_by);
        rvTeamSnippetsList = findViewById(R.id.rv_team_snippets_list);
        tvNoSnippets = findViewById(R.id.tv_no_snippets);
        progressBar = findViewById(R.id.progress_bar);
        fabCreateTeamSnippet = findViewById(R.id.fab_create_team_snippet);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(teamName != null ? teamName + " Snippets" : "Team Snippets");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupSpinners() {
        // Placeholder for languages - in a real app, fetch from API or define
        String[] languages = {"All", "Java", "Kotlin", "Python", "JavaScript", "C++"};
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguageFilter.setAdapter(langAdapter);

        // Placeholder for sort options
        String[] sortOptions = {"Newest", "Oldest", "Most Popular"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortOptions);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(sortAdapter);

        // TODO: Implement OnItemSelectedListener for spinners to trigger snippet fetching
    }

    private void setupRecyclerView() {
        teamSnippetAdapter = new TeamSnippetAdapter(this);
        rvTeamSnippetsList.setLayoutManager(new LinearLayoutManager(this));
        rvTeamSnippetsList.setAdapter(teamSnippetAdapter);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        teamViewModel.getTeamSnippetsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                rvTeamSnippetsList.setVisibility(View.GONE);
                tvNoSnippets.setVisibility(View.GONE);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                if (resource.getData() != null && !resource.getData().isEmpty()) {
                    teamSnippetAdapter.setTeamSnippets(resource.getData());
                    rvTeamSnippetsList.setVisibility(View.VISIBLE);
                    tvNoSnippets.setVisibility(View.GONE);
                } else {
                    rvTeamSnippetsList.setVisibility(View.GONE);
                    tvNoSnippets.setVisibility(View.VISIBLE);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                tvNoSnippets.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Error loading snippets: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        fabCreateTeamSnippet.setOnClickListener(v -> {
            // TODO: Navigate to CreateSnippetActivity with teamId pre-filled
            Toast.makeText(this, "Create Team Snippet Clicked (Navigation Pending)", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchTeamSnippets() {
        Map<String, String> filters = new HashMap<>();
        // TODO: Apply selected language filter and sort option
        // String selectedLanguage = spinnerLanguageFilter.getSelectedItem().toString();
        // if (!selectedLanguage.equals("All")) { filters.put("language", selectedLanguage); }
        // String selectedSort = spinnerSortBy.getSelectedItem().toString();
        // filters.put("sort_by", selectedSort.toLowerCase().replace(" ", "_"));

        teamViewModel.fetchTeamSnippets(teamId, filters);
    }

    @Override
    public void onTeamSnippetClick(TeamSnippet teamSnippet) {
        // TODO: Navigate to TeamSnippetDetailActivity or a general SnippetDetailActivity
        Toast.makeText(this, "Team Snippet: " + teamSnippet.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
    }
}
