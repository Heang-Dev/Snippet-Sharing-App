package group.eleven.snippet_sharing_app.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamInvitation; // ADDED THIS IMPORT
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.ui.team.TeamListAdapter;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;

public class TeamsListActivity extends AppCompatActivity implements TeamListAdapter.OnItemClickListener, TeamInvitationAdapter.OnInvitationActionListener {

    private RecyclerView rvTeams;
    private TeamListAdapter teamListAdapter;
    private TextView tvNoTeams;
    private FloatingActionButton fabCreateTeam;
    private MaterialToolbar toolbar;

    // For Team Invitations
    private RecyclerView rvTeamInvitations;
    private TeamInvitationAdapter teamInvitationAdapter;
    private TextView tvInvitationsHeader;
    private TextView tvNoInvitations; // Added for when there are no invitations


    // ViewModel for team data
    private TeamViewModel teamViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_list);

        initViews();
        setupToolbar();
        setupRecyclerViews(); // Renamed to handle both
        setupViewModel();
        setupListeners();

        // Fetch teams when the activity is created
        fetchTeams();
        fetchTeamInvitations();
    }

    private void initViews() {
        rvTeams = findViewById(R.id.rv_teams);
        tvNoTeams = findViewById(R.id.tv_no_teams);
        fabCreateTeam = findViewById(R.id.fab_create_team);
        toolbar = findViewById(R.id.toolbar);

        // Initialize invitation views
        rvTeamInvitations = findViewById(R.id.rv_team_invitations);
        tvInvitationsHeader = findViewById(R.id.tv_invitations_header);
        tvNoInvitations = findViewById(R.id.tv_no_invitations); // Assuming you'll add this TextView to XML for "no invitations" state
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupRecyclerViews() { // Renamed method
        // Setup for My Teams
        teamListAdapter = new TeamListAdapter(this); // 'this' refers to OnItemClickListener
        rvTeams.setLayoutManager(new LinearLayoutManager(this));
        rvTeams.setAdapter(teamListAdapter);

        // Setup for Team Invitations
        teamInvitationAdapter = new TeamInvitationAdapter(this); // 'this' refers to OnInvitationActionListener
        rvTeamInvitations.setLayoutManager(new LinearLayoutManager(this));
        rvTeamInvitations.setAdapter(teamInvitationAdapter);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        teamViewModel.getMyTeamsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                // Show loading indicator
                tvNoTeams.setVisibility(View.GONE);
                rvTeams.setVisibility(View.GONE);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null && !resource.getData().isEmpty()) {
                    teamListAdapter.setTeams(resource.getData());
                    rvTeams.setVisibility(View.VISIBLE);
                    tvNoTeams.setVisibility(View.GONE);
                } else {
                    rvTeams.setVisibility(View.GONE);
                    tvNoTeams.setVisibility(View.VISIBLE);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                rvTeams.setVisibility(View.GONE);
                tvNoTeams.setVisibility(View.VISIBLE); // Optionally show error message here
            }
        });

        // Observe the result of responding to team invitations
        teamViewModel.getRespondToTeamInvitationResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                // Optionally show a progress indicator
                Toast.makeText(this, "Processing invitation...", Toast.LENGTH_SHORT).show();
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(this, resource.getData().getMessage(), Toast.LENGTH_SHORT).show();
                // Refresh invitations after successful response
                fetchTeamInvitations();
                fetchTeams(); // Also refresh teams in case an invitation to a new team was accepted
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        fabCreateTeam.setOnClickListener(v -> {
            // Navigate to CreateTeamActivity
            Intent intent = new Intent(TeamsListActivity.this, CreateTeamActivity.class);
            startActivity(intent);
        });
    }

    private void fetchTeams() {
        teamViewModel.fetchMyTeams();
    }

    private void fetchTeamInvitations() {
        teamViewModel.fetchMyTeamInvitations();
        teamViewModel.getMyTeamInvitationsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                tvInvitationsHeader.setVisibility(View.GONE);
                rvTeamInvitations.setVisibility(View.GONE);
                tvNoInvitations.setVisibility(View.GONE);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null && !resource.getData().isEmpty()) {
                    teamInvitationAdapter.setInvitations(resource.getData());
                    tvInvitationsHeader.setVisibility(View.VISIBLE);
                    rvTeamInvitations.setVisibility(View.VISIBLE);
                    tvNoInvitations.setVisibility(View.GONE);
                } else {
                    // No invitations
                    tvInvitationsHeader.setVisibility(View.GONE);
                    rvTeamInvitations.setVisibility(View.GONE);
                    tvNoInvitations.setVisibility(View.VISIBLE); // Show "No invitations" message
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Invitations Error: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                tvInvitationsHeader.setVisibility(View.GONE);
                rvTeamInvitations.setVisibility(View.GONE);
                tvNoInvitations.setVisibility(View.VISIBLE); // Show "No invitations" or error message
            }
        });
    }

    @Override
    public void onItemClick(Team team) {
        // Navigate to TeamDashboardActivity
        Intent intent = new Intent(TeamsListActivity.this, TeamDashboardActivity.class);
        intent.putExtra("teamId", team.getId());
        startActivity(intent);
    }

    //region TeamInvitationAdapter.OnInvitationActionListener implementation
    @Override
    public void onAcceptInvitation(TeamInvitation invitation) {
        teamViewModel.respondToTeamInvitation(invitation.getId(), true); // true for accept
    }

    @Override
    public void onRejectInvitation(TeamInvitation invitation) {
        teamViewModel.respondToTeamInvitation(invitation.getId(), false); // false for reject
    }
    //endregion
}
