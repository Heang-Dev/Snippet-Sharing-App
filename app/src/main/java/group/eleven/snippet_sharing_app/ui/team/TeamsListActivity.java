package group.eleven.snippet_sharing_app.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamInvitation;
import group.eleven.snippet_sharing_app.data.model.TeamsResponse;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;

import java.util.List;
import group.eleven.snippet_sharing_app.ui.home.HomeActivity;
import group.eleven.snippet_sharing_app.ui.mysnippets.MySnippetsActivity;
import group.eleven.snippet_sharing_app.ui.profile.ProfileActivity;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;
import group.eleven.snippet_sharing_app.utils.BottomNavHelper;
import group.eleven.snippet_sharing_app.utils.SessionManager;

public class TeamsListActivity extends AppCompatActivity implements TeamListAdapter.OnItemClickListener, TeamInvitationAdapter.OnInvitationActionListener {

    private RecyclerView rvTeams;
    private TeamListAdapter teamListAdapter;
    private TextView tvNoTeams;
    private TextView tvTeamsCount;
    private FloatingActionButton fabCreateTeam;
    private MaterialButton btnCreateTeam;
    private MaterialToolbar toolbar;
    private LinearLayout layoutEmptyTeams;
    private LinearLayout layoutInvitations;
    private BottomNavigationView bottomNav;
    private MaterialCardView cardDiscoverTeams;

    // For Team Invitations
    private RecyclerView rvTeamInvitations;
    private TeamInvitationAdapter teamInvitationAdapter;
    private TextView tvInvitationsHeader;
    private TextView tvNoInvitations;

    // ViewModel for team data
    private TeamViewModel teamViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_list);

        setupStatusBar();
        initViews();
        setupToolbar();
        setupBottomNavigation();
        setupRecyclerViews();
        setupViewModel();
        setupListeners();

        // Teams fetched in onResume()
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

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvTeams = findViewById(R.id.rv_teams);
        tvNoTeams = findViewById(R.id.tv_no_teams);
        tvTeamsCount = findViewById(R.id.tvTeamsCount);
        fabCreateTeam = findViewById(R.id.fab_create_team);
        btnCreateTeam = findViewById(R.id.btnCreateTeam);
        layoutEmptyTeams = findViewById(R.id.layoutEmptyTeams);
        layoutInvitations = findViewById(R.id.layoutInvitations);
        bottomNav = findViewById(R.id.bottomNav);
        cardDiscoverTeams = findViewById(R.id.cardDiscoverTeams);

        // Initialize invitation views
        rvTeamInvitations = findViewById(R.id.rv_team_invitations);
        tvInvitationsHeader = findViewById(R.id.tv_invitations_header);
        tvNoInvitations = findViewById(R.id.tv_no_invitations);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupBottomNavigation() {
        // Apply navigation bar insets
        View bottomNavContainer = findViewById(R.id.bottomNavContainer);
        BottomNavHelper.applyNavigationBarInsets(bottomNavContainer);
        BottomNavHelper.applyFabNavigationBarInsets(findViewById(R.id.fab_create_team));

        // Set Teams as selected
        bottomNav.setSelectedItemId(R.id.nav_teams);

        // Setup profile avatar in bottom nav
        BottomNavHelper.setupProfileAvatar(this, bottomNav, new SessionManager(this));

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_teams) {
                // Already on teams
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, group.eleven.snippet_sharing_app.ui.favorites.FavoritesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
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
                layoutEmptyTeams.setVisibility(View.GONE);
                rvTeams.setVisibility(View.GONE);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                TeamsResponse teamsResponse = resource.getData();
                List<Team> allTeams = teamsResponse != null ? teamsResponse.getAllTeams() : new ArrayList<>();

                if (!allTeams.isEmpty()) {
                    teamListAdapter.setTeams(allTeams);
                    rvTeams.setVisibility(View.VISIBLE);
                    layoutEmptyTeams.setVisibility(View.GONE);
                    tvTeamsCount.setText(allTeams.size() + " teams");
                    fabCreateTeam.setVisibility(View.VISIBLE);
                } else {
                    rvTeams.setVisibility(View.GONE);
                    layoutEmptyTeams.setVisibility(View.VISIBLE);
                    tvTeamsCount.setText("0 teams");
                    fabCreateTeam.setVisibility(View.GONE); // Hide FAB when showing empty state with button
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_LONG).show();
                rvTeams.setVisibility(View.GONE);
                layoutEmptyTeams.setVisibility(View.VISIBLE);
                tvTeamsCount.setText("0 teams");
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
        View.OnClickListener createTeamListener = v -> {
            Intent intent = new Intent(TeamsListActivity.this, CreateTeamActivity.class);
            startActivity(intent);
        };

        fabCreateTeam.setOnClickListener(createTeamListener);
        if (btnCreateTeam != null) {
            btnCreateTeam.setOnClickListener(createTeamListener);
        }

        // Hide discover teams card - no backend endpoint for public team search yet
        if (cardDiscoverTeams != null) {
            cardDiscoverTeams.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTeams();
        fetchTeamInvitations();
    }

    private void fetchTeams() {
        teamViewModel.fetchMyTeams();
    }

    private void fetchTeamInvitations() {
        teamViewModel.fetchMyTeamInvitations();
        teamViewModel.getMyTeamInvitationsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                layoutInvitations.setVisibility(View.GONE);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null && !resource.getData().isEmpty()) {
                    teamInvitationAdapter.setInvitations(resource.getData());
                    layoutInvitations.setVisibility(View.VISIBLE);
                    rvTeamInvitations.setVisibility(View.VISIBLE);
                    tvNoInvitations.setVisibility(View.GONE);
                } else {
                    // No invitations - hide the entire section
                    layoutInvitations.setVisibility(View.GONE);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                // On error, hide invitations section
                layoutInvitations.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(Team team) {
        // Navigate to TeamChatActivity (main team screen with shared snippets)
        Intent intent = new Intent(TeamsListActivity.this, TeamChatActivity.class);
        intent.putExtra(TeamChatActivity.EXTRA_TEAM_ID, team.getId());
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
