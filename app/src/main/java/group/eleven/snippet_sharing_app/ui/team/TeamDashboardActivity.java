package group.eleven.snippet_sharing_app.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog; // ADDED THIS IMPORT
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.ui.home.ActivityFeedAdapter; // Assuming this adapter can be reused
import group.eleven.snippet_sharing_app.ui.home.SnippetCardAdapter; // Assuming this adapter can be reused
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;
import group.eleven.snippet_sharing_app.utils.SessionManager; // To check current user's role

public class TeamDashboardActivity extends AppCompatActivity implements TeamMemberAdapter.OnItemClickListener {

    public static final String EXTRA_TEAM_ID = "extra_team_id";

    private MaterialToolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView ivTeamAvatar;
    private ImageView ivSettings;
    private TextView tvTeamName;
    private TextView tvTeamDescription;
    private TextView tvTeamPrivacy;
    private TextView tvMemberCountLabel;
    private TextView tvMembersCount;
    private TextView tvSnippetsCount;
    private LinearLayout llQuickActions;
    private View btnInviteMembers;
    private View btnViewSnippets;
    private FloatingActionButton btnCreateSnippet;
    private View btnLeaveTeam;
    private RecyclerView rvTeamMembers;
    private RecyclerView rvTeamSnippets;
    private RecyclerView rvTeamActivity;
    private ProgressBar progressBar;

    private TeamMemberAdapter teamMemberAdapter;
    private TeamSnippetAdapter teamSnippetAdapter; // Changed to TeamSnippetAdapter
    private ActivityFeedAdapter teamActivityAdapter; // Reusing existing adapter

    private TeamViewModel teamViewModel;
    private String teamId;
    private Team currentTeam;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_dashboard);

        teamId = getIntent().getStringExtra(EXTRA_TEAM_ID);
        if (teamId == null) {
            Toast.makeText(this, "Team ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerViews();
        setupViewModel();
        setupListeners();

        fetchTeamDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        ivTeamAvatar = findViewById(R.id.iv_team_avatar);
        ivSettings = findViewById(R.id.ivSettings);
        tvTeamName = findViewById(R.id.tv_team_name);
        tvTeamDescription = findViewById(R.id.tv_team_description);
        tvTeamPrivacy = findViewById(R.id.tv_team_privacy);
        tvMemberCountLabel = findViewById(R.id.tv_member_count_label);
        tvMembersCount = findViewById(R.id.tvMembersCount);
        tvSnippetsCount = findViewById(R.id.tvSnippetsCount);
        llQuickActions = findViewById(R.id.ll_quick_actions);
        btnInviteMembers = findViewById(R.id.btn_invite_members);
        btnViewSnippets = findViewById(R.id.btn_view_snippets);
        btnCreateSnippet = findViewById(R.id.btn_create_snippet);
        btnLeaveTeam = findViewById(R.id.btn_leave_team);
        rvTeamMembers = findViewById(R.id.rv_team_members);
        rvTeamSnippets = findViewById(R.id.rv_team_snippets);
        rvTeamActivity = findViewById(R.id.rv_team_activity);
        progressBar = findViewById(R.id.progress_bar);

        sessionManager = new SessionManager(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(""); // Will set title dynamically
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupRecyclerViews() {
        teamMemberAdapter = new TeamMemberAdapter(this);
        rvTeamMembers.setLayoutManager(new LinearLayoutManager(this)); // Vertical layout like Telegram
        rvTeamMembers.setAdapter(teamMemberAdapter);

        teamSnippetAdapter = new TeamSnippetAdapter(new TeamSnippetAdapter.OnTeamSnippetClickListener() {
            @Override
            public void onTeamSnippetClick(group.eleven.snippet_sharing_app.data.model.TeamSnippet teamSnippet) {
                // TODO: Navigate to team snippet detail
                Toast.makeText(TeamDashboardActivity.this, "Team Snippet: " + teamSnippet.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
        rvTeamSnippets.setLayoutManager(new LinearLayoutManager(this));
        rvTeamSnippets.setAdapter(teamSnippetAdapter);

        teamActivityAdapter = new ActivityFeedAdapter(new ArrayList<>());
        rvTeamActivity.setLayoutManager(new LinearLayoutManager(this));
        rvTeamActivity.setAdapter(teamActivityAdapter);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        teamViewModel.getTeamDetailsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                // Hide other content
                llQuickActions.setVisibility(View.GONE);
                rvTeamMembers.setVisibility(View.GONE);
                rvTeamSnippets.setVisibility(View.GONE);
                rvTeamActivity.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                llQuickActions.setVisibility(View.VISIBLE);
                rvTeamMembers.setVisibility(View.VISIBLE);
                rvTeamSnippets.setVisibility(View.VISIBLE);
                rvTeamActivity.setVisibility(View.VISIBLE);
            }

            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                currentTeam = resource.getData();
                if (currentTeam != null) {
                    displayTeamDetails(currentTeam);
                    // Also fetch members, snippets, activity
                    fetchTeamMembers();
                    fetchTeamSnippets();
                    fetchTeamActivity(); // Now uncommented
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                finish(); // Go back if team details can't be loaded
            }
        });

        // Observe team activity feed result
        teamViewModel.getTeamActivityFeedResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null) {
                    teamActivityAdapter.setActivities(resource.getData());
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error loading activity feed: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Observe leave team result
        teamViewModel.getLeaveTeamResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                btnLeaveTeam.setClickable(false);
                btnLeaveTeam.setAlpha(0.5f);
            } else {
                btnLeaveTeam.setClickable(true);
                btnLeaveTeam.setAlpha(1.0f);
            }
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(this, "Successfully left team!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Indicate a change happened
                finish(); // Go back to teams list
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error leaving team: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Observe team snippets result
        teamViewModel.getTeamSnippetsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null) {
                    teamSnippetAdapter.setTeamSnippets(resource.getData());
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error loading snippets: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Settings button in toolbar
        ivSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamSettingsActivity.class);
            intent.putExtra(TeamSettingsActivity.EXTRA_TEAM_ID, teamId);
            startActivity(intent);
        });

        // Add members action
        btnInviteMembers.setOnClickListener(v -> {
            // TODO: Navigate to invite members page/dialog
            Toast.makeText(this, "Invite Members Clicked", Toast.LENGTH_SHORT).show();
        });

        // View all snippets
        btnViewSnippets.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamSnippetsActivity.class);
            intent.putExtra(TeamSnippetsActivity.EXTRA_TEAM_ID, teamId);
            startActivity(intent);
        });

        // FAB to create new snippet
        btnCreateSnippet.setOnClickListener(v -> {
            // TODO: Navigate to create snippet page (team context)
            Toast.makeText(this, "Create Snippet Clicked", Toast.LENGTH_SHORT).show();
        });

        // Leave team
        btnLeaveTeam.setOnClickListener(v -> confirmLeaveTeam());
    }

    private void confirmLeaveTeam() {
        new AlertDialog.Builder(this)
                .setTitle("Leave Team")
                .setMessage("Are you sure you want to leave '" + currentTeam.getName() + "'? You will lose access to team snippets and features.")
                .setPositiveButton("Leave", (dialog, which) -> teamViewModel.leaveTeam(teamId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayTeamDetails(Team team) {
        // Set collapsing toolbar title
        collapsingToolbar.setTitle(team.getName());

        tvTeamName.setText(team.getName());
        tvTeamDescription.setText(team.getDescription());
        tvTeamPrivacy.setText(team.getPrivacy());

        // Load team avatar
        if (team.getAvatarUrl() != null && !team.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(team.getAvatarUrl())
                    .placeholder(R.drawable.ic_users)
                    .error(R.drawable.ic_users)
                    .centerCrop()
                    .into(ivTeamAvatar);
            ivTeamAvatar.setPadding(0, 0, 0, 0); // Remove padding when showing actual image
        }

        // Update member count label (Telegram style: "X members")
        int memberCount = team.getMemberCount();
        String memberText = memberCount == 1 ? "1 member" : memberCount + " members";
        tvMemberCountLabel.setText(memberText);

        // Update members header with count
        tvMembersCount.setText(memberCount + " Members");

        // Update snippets count
        int snippetCount = team.getSnippetCount();
        String snippetText = snippetCount == 1 ? "1 snippet" : snippetCount + " snippets";
        tvSnippetsCount.setText(snippetText);

        // Control leave team button visibility
        if (sessionManager.getCurrentUser() != null && team.getOwnerId().equals(sessionManager.getCurrentUser().getId())) {
            btnLeaveTeam.setVisibility(View.GONE); // Owner cannot leave their own team directly
        } else {
            btnLeaveTeam.setVisibility(View.VISIBLE);
        }
    }

    private void fetchTeamDetails() {
        teamViewModel.fetchTeamDetails(teamId);
    }

    private void fetchTeamMembers() {
        teamViewModel.fetchTeamMembers(teamId);
        teamViewModel.getTeamMembersResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null) {
                    teamMemberAdapter.setTeamMembers(resource.getData());
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error loading members: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTeamSnippets() {
        // Default filters for dashboard display
        Map<String, String> filters = new HashMap<>();
        // TODO: Add actual filters if needed, e.g., filters.put("limit", "5");
        teamViewModel.fetchTeamSnippets(teamId, filters);
        teamViewModel.getTeamSnippetsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null) {
                    teamSnippetAdapter.setTeamSnippets(resource.getData());
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error loading snippets: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTeamActivity() {
        teamViewModel.fetchTeamActivity(teamId);
    }

    // region TeamMemberAdapter.OnItemClickListener
    @Override
    public void onItemClick(TeamMember member) {
        // TODO: Navigate to member profile or show member details
        Toast.makeText(this, "Member " + member.getUsername() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreOptionsClick(TeamMember member, View view) {
        // TODO: Show a popup menu with options like "Change Role", "Remove Member"
        Toast.makeText(this, "More options for " + member.getUsername(), Toast.LENGTH_SHORT).show();
    }
    // endregion
}
