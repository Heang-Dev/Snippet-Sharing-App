package group.eleven.snippet_sharing_app.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.ui.snippet.CreateSnippetActivity;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;

/**
 * TeamChatActivity - Main team screen showing shared snippets (like Telegram chat)
 * Clicking the header navigates to TeamDashboardActivity (team info page)
 */
public class TeamChatActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_ID = "extra_team_id";

    private ActivityResultLauncher<Intent> createSnippetLauncher;

    private MaterialToolbar toolbar;
    private LinearLayout llTeamHeader;
    private ImageView ivTeamAvatar;
    private TextView tvTeamName;
    private TextView tvMemberCount;
    private RecyclerView rvTeamMessages;
    private LinearLayout llEmptyState;
    private MaterialButton btnShareSnippet;
    private ProgressBar progressBar;

    private TeamViewModel teamViewModel;
    private TeamSnippetAdapter teamSnippetAdapter;
    private String teamId;
    private Team currentTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_chat);

        // Register launcher before onCreate completes
        createSnippetLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        fetchTeamSnippets();
                    }
                }
        );

        teamId = getIntent().getStringExtra(EXTRA_TEAM_ID);
        if (teamId == null) {
            Toast.makeText(this, "Team ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupStatusBar();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        setupListeners();

        fetchTeamDetails();
        fetchTeamSnippets();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        llTeamHeader = findViewById(R.id.ll_team_header);
        ivTeamAvatar = findViewById(R.id.iv_team_avatar);
        tvTeamName = findViewById(R.id.tv_team_name);
        tvMemberCount = findViewById(R.id.tv_member_count);
        rvTeamMessages = findViewById(R.id.rv_team_messages);
        llEmptyState = findViewById(R.id.ll_empty_state);
        btnShareSnippet = findViewById(R.id.btn_share_snippet);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupStatusBar() {
        android.view.Window window = getWindow();
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(R.attr.surfaceColor, typedValue, true);
        int surfaceColor;
        if (typedValue.resourceId != 0) {
            surfaceColor = androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId);
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
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        teamSnippetAdapter = new TeamSnippetAdapter(teamSnippet -> {
            // TODO: Open snippet detail
            Toast.makeText(this, "Snippet: " + teamSnippet.getTitle(), Toast.LENGTH_SHORT).show();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rvTeamMessages.setLayoutManager(layoutManager);
        rvTeamMessages.setAdapter(teamSnippetAdapter);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        // Observe team details
        teamViewModel.getTeamDetailsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                currentTeam = resource.getData();
                if (currentTeam != null) {
                    displayTeamHeader(currentTeam);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Observe team snippets
        teamViewModel.getTeamSnippetsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null && !resource.getData().isEmpty()) {
                    llEmptyState.setVisibility(View.GONE);
                    rvTeamMessages.setVisibility(View.VISIBLE);
                    teamSnippetAdapter.setTeamSnippets(resource.getData());
                } else {
                    llEmptyState.setVisibility(View.VISIBLE);
                    rvTeamMessages.setVisibility(View.GONE);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error loading snippets: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Click header to open team info page
        llTeamHeader.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamDashboardActivity.class);
            intent.putExtra(TeamDashboardActivity.EXTRA_TEAM_ID, teamId);
            startActivity(intent);
        });

        // Share snippet button - open create snippet for team
        btnShareSnippet.setOnClickListener(v -> openCreateSnippetForTeam());
    }

    private void openCreateSnippetForTeam() {
        Intent intent = new Intent(this, CreateSnippetActivity.class);
        intent.putExtra("extra_team_id", teamId);
        if (currentTeam != null) {
            intent.putExtra("extra_team_name", currentTeam.getName());
        }
        createSnippetLauncher.launch(intent);
    }

    private void displayTeamHeader(Team team) {
        tvTeamName.setText(team.getName());

        int memberCount = team.getMemberCount();
        String memberText = memberCount == 1 ? "1 member" : memberCount + " members";
        tvMemberCount.setText(memberText);

        // Load team avatar
        if (team.getAvatarUrl() != null && !team.getAvatarUrl().isEmpty()) {
            ivTeamAvatar.setPadding(0, 0, 0, 0);
            ivTeamAvatar.setImageTintList(null);
            Glide.with(this)
                    .load(team.getAvatarUrl())
                    .placeholder(R.drawable.ic_users)
                    .error(R.drawable.ic_users)
                    .circleCrop()
                    .into(ivTeamAvatar);
        }
    }

    private void fetchTeamDetails() {
        teamViewModel.fetchTeamDetails(teamId);
    }

    private void fetchTeamSnippets() {
        Map<String, String> filters = new HashMap<>();
        teamViewModel.fetchTeamSnippets(teamId, filters);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from team info page
        fetchTeamDetails();
        fetchTeamSnippets();
    }
}
