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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

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
    private ImageView ivTeamAvatar;
    private TextView tvTeamName;
    private TextView tvTeamDescription;
    private TextView tvTeamPrivacy;
    private LinearLayout llTeamStats;
    private LinearLayout llQuickActions;
    private MaterialButton btnInviteMembers;
    private MaterialButton btnCreateSnippet;
    private MaterialButton btnTeamSettings;
    private MaterialButton btnLeaveTeam;
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
        ivTeamAvatar = findViewById(R.id.iv_team_avatar);
        tvTeamName = findViewById(R.id.tv_team_name);
        tvTeamDescription = findViewById(R.id.tv_team_description);
        tvTeamPrivacy = findViewById(R.id.tv_team_privacy);
        llTeamStats = findViewById(R.id.ll_team_stats);
        llQuickActions = findViewById(R.id.ll_quick_actions);
        btnInviteMembers = findViewById(R.id.btn_invite_members);
        btnCreateSnippet = findViewById(R.id.btn_create_snippet);
        btnTeamSettings = findViewById(R.id.btn_team_settings);
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
        rvTeamMembers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
                llTeamStats.setVisibility(View.GONE);
                rvTeamMembers.setVisibility(View.GONE);
                rvTeamSnippets.setVisibility(View.GONE);
                rvTeamActivity.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                llQuickActions.setVisibility(View.VISIBLE);
                llTeamStats.setVisibility(View.VISIBLE);
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
                btnLeaveTeam.setEnabled(false);
            } else {
                btnLeaveTeam.setEnabled(true);
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
        btnInviteMembers.setOnClickListener(v -> {
            // TODO: Navigate to invite members page/dialog
            Toast.makeText(this, "Invite Members Clicked", Toast.LENGTH_SHORT).show();
        });
        btnCreateSnippet.setOnClickListener(v -> {
            // TODO: Navigate to create snippet page (team context)
            Toast.makeText(this, "Create Snippet Clicked", Toast.LENGTH_SHORT).show();
        });
        btnTeamSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamSettingsActivity.class);
            intent.putExtra(TeamSettingsActivity.EXTRA_TEAM_ID, teamId);
            startActivity(intent);
        });
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
        toolbar.setTitle(team.getName());
        tvTeamName.setText(team.getName());
        tvTeamDescription.setText(team.getDescription());
        tvTeamPrivacy.setText(team.getPrivacy());

        Glide.with(this)
                .load(team.getAvatarUrl())
                .placeholder(R.drawable.ic_collections)
                .error(R.drawable.ic_collections)
                .into(ivTeamAvatar);

        // Update stats (TODO: create helper method to set stat card values)
        // For stat_members
        TextView tvStatMembersCount = llTeamStats.findViewById(R.id.stat_members).findViewById(R.id.tvStatCount);
        TextView tvStatMembersLabel = llTeamStats.findViewById(R.id.stat_members).findViewById(R.id.tvStatLabel);
        ImageView ivStatMembersIcon = llTeamStats.findViewById(R.id.stat_members).findViewById(R.id.ivStatIcon);
        tvStatMembersCount.setText(String.valueOf(team.getMemberCount()));
        tvStatMembersLabel.setText("MEMBERS");
        ivStatMembersIcon.setImageResource(R.drawable.ic_person); // Assuming ic_person exists

        // For stat_snippets
        TextView tvStatSnippetsCount = llTeamStats.findViewById(R.id.stat_snippets).findViewById(R.id.tvStatCount);
        TextView tvStatSnippetsLabel = llTeamStats.findViewById(R.id.stat_snippets).findViewById(R.id.tvStatLabel);
        ImageView ivStatSnippetsIcon = llTeamStats.findViewById(R.id.stat_snippets).findViewById(R.id.ivStatIcon);
        tvStatSnippetsCount.setText(String.valueOf(team.getSnippetCount()));
        tvStatSnippetsLabel.setText("SNIPPETS");
        ivStatSnippetsIcon.setImageResource(R.drawable.ic_code); // Assuming ic_code exists

        // For stat_activity (placeholder, actual value depends on backend)
        TextView tvStatActivityCount = llTeamStats.findViewById(R.id.stat_activity).findViewById(R.id.tvStatCount);
        TextView tvStatActivityLabel = llTeamStats.findViewById(R.id.stat_activity).findViewById(R.id.tvStatLabel);
        ImageView ivStatActivityIcon = llTeamStats.findViewById(R.id.stat_activity).findViewById(R.id.ivStatIcon);
        tvStatActivityCount.setText("N/A"); // Or fetch from team object if available
        tvStatActivityLabel.setText("ACTIVITY");
        ivStatActivityIcon.setImageResource(R.drawable.ic_activity); // Assuming ic_activity exists

        // Hide percentage change views in stat cards for simplicity on dashboard
        llTeamStats.findViewById(R.id.stat_members).findViewById(R.id.ivStatArrow).setVisibility(View.GONE);
        llTeamStats.findViewById(R.id.stat_members).findViewById(R.id.tvStatPercentage).setVisibility(View.GONE);
        llTeamStats.findViewById(R.id.stat_snippets).findViewById(R.id.ivStatArrow).setVisibility(View.GONE);
        llTeamStats.findViewById(R.id.stat_snippets).findViewById(R.id.tvStatPercentage).setVisibility(View.GONE);
        llTeamStats.findViewById(R.id.stat_activity).findViewById(R.id.ivStatArrow).setVisibility(View.GONE);
        llTeamStats.findViewById(R.id.stat_activity).findViewById(R.id.tvStatPercentage).setVisibility(View.GONE);

        // Control leave team button visibility
        if (sessionManager.getCurrentUser() != null && team.getOwnerId().equals(sessionManager.getCurrentUser().getId())) {
            btnLeaveTeam.setVisibility(View.GONE); // Owner cannot leave their own team directly (must transfer ownership)
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
