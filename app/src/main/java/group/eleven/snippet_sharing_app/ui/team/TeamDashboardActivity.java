package group.eleven.snippet_sharing_app.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * TeamDashboardActivity - Team Info page (like Telegram group info)
 * Accessed by clicking the header in TeamChatActivity
 */
public class TeamDashboardActivity extends AppCompatActivity implements TeamMemberAdapter.OnItemClickListener {

    public static final String EXTRA_TEAM_ID = "extra_team_id";

    private MaterialToolbar toolbar;
    private TextView tvEdit;
    private ImageView ivTeamAvatar;
    private TextView tvTeamName;
    private TextView tvTeamDescription;
    private TextView tvTeamPrivacy;
    private TextView tvMemberCountLabel;
    private LinearLayout llQuickActions;
    private View btnMute;
    private View btnSearch;
    private View btnMore;
    private View btnInviteMembers;
    private View btnLeaveTeam;
    private TabLayout tabLayout;
    private RecyclerView rvTeamMembers;
    private ProgressBar progressBar;

    private TeamMemberAdapter teamMemberAdapter;
    private TeamSnippetAdapter teamSnippetAdapter;

    private TeamViewModel teamViewModel;
    private String teamId;
    private Team currentTeam;
    private SessionManager sessionManager;
    private boolean isMuted = false;

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
        setupStatusBar();
        setupToolbar();
        setupRecyclerViews();
        setupViewModel();
        setupListeners();

        fetchTeamDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvEdit = findViewById(R.id.tv_edit);
        ivTeamAvatar = findViewById(R.id.iv_team_avatar);
        tvTeamName = findViewById(R.id.tv_team_name);
        tvTeamDescription = findViewById(R.id.tv_team_description);
        tvTeamPrivacy = findViewById(R.id.tv_team_privacy);
        tvMemberCountLabel = findViewById(R.id.tv_member_count_label);
        llQuickActions = findViewById(R.id.ll_quick_actions);
        btnMute = findViewById(R.id.btn_mute);
        btnSearch = findViewById(R.id.btn_search);
        btnMore = findViewById(R.id.btn_more);
        btnInviteMembers = findViewById(R.id.btn_invite_members);
        btnLeaveTeam = findViewById(R.id.btn_leave_team);
        tabLayout = findViewById(R.id.tab_layout);
        rvTeamMembers = findViewById(R.id.rv_team_members);
        progressBar = findViewById(R.id.progress_bar);

        sessionManager = new SessionManager(this);
    }

    private void setupStatusBar() {
        android.view.Window window = getWindow();
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(R.attr.surfaceColor, typedValue, true);
        int statusBarColor;
        if (typedValue.resourceId != 0) {
            statusBarColor = androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            statusBarColor = typedValue.data;
        }
        window.setStatusBarColor(statusBarColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            boolean isLightBackground = isColorLight(statusBarColor);
            controller.setAppearanceLightStatusBars(isLightBackground);
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

    private void setupRecyclerViews() {
        teamMemberAdapter = new TeamMemberAdapter(this);
        rvTeamMembers.setLayoutManager(new LinearLayoutManager(this));
        rvTeamMembers.setNestedScrollingEnabled(false);
        rvTeamMembers.setAdapter(teamMemberAdapter);

        teamSnippetAdapter = new TeamSnippetAdapter(teamSnippet -> {
            Toast.makeText(this, "Snippet: " + teamSnippet.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        teamViewModel.getTeamDetailsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                llQuickActions.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                llQuickActions.setVisibility(View.VISIBLE);
            }

            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                currentTeam = resource.getData();
                if (currentTeam != null) {
                    displayTeamDetails(currentTeam);
                    fetchTeamMembers();
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });

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
                setResult(RESULT_OK);
                finish();
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error leaving team: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        tvEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamSettingsActivity.class);
            intent.putExtra(TeamSettingsActivity.EXTRA_TEAM_ID, teamId);
            startActivity(intent);
        });

        btnMute.setOnClickListener(v -> {
            isMuted = !isMuted;
            ImageView ivMuteIcon = findViewById(R.id.iv_mute_icon);
            if (isMuted) {
                ivMuteIcon.setImageResource(R.drawable.ic_notification_off);
                Toast.makeText(this, "Notifications muted", Toast.LENGTH_SHORT).show();
            } else {
                ivMuteIcon.setImageResource(R.drawable.ic_notification);
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
        });

        btnSearch.setOnClickListener(v -> Toast.makeText(this, "Search in team", Toast.LENGTH_SHORT).show());
        btnMore.setOnClickListener(v -> showMoreOptionsMenu());
        btnInviteMembers.setOnClickListener(v -> Toast.makeText(this, "Add Members", Toast.LENGTH_SHORT).show());
        btnLeaveTeam.setOnClickListener(v -> confirmLeaveTeam());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        rvTeamMembers.setAdapter(teamMemberAdapter);
                        fetchTeamMembers();
                        break;
                    case 1:
                        rvTeamMembers.setAdapter(teamSnippetAdapter);
                        fetchTeamSnippets();
                        break;
                    case 2:
                        Toast.makeText(TeamDashboardActivity.this, "Files coming soon", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showMoreOptionsMenu() {
        String[] options = {"Share Team", "Report", "Copy Link"};
        new AlertDialog.Builder(this)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: Toast.makeText(this, "Share team", Toast.LENGTH_SHORT).show(); break;
                        case 1: Toast.makeText(this, "Report team", Toast.LENGTH_SHORT).show(); break;
                        case 2: Toast.makeText(this, "Link copied", Toast.LENGTH_SHORT).show(); break;
                    }
                })
                .show();
    }

    private void confirmLeaveTeam() {
        new AlertDialog.Builder(this)
                .setTitle("Leave Team")
                .setMessage("Are you sure you want to leave '" + currentTeam.getName() + "'?")
                .setPositiveButton("Leave", (dialog, which) -> teamViewModel.leaveTeam(teamId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayTeamDetails(Team team) {
        tvTeamName.setText(team.getName());
        tvTeamDescription.setText(team.getDescription());
        tvTeamPrivacy.setText(team.getPrivacy());

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

        int memberCount = team.getMemberCount();
        String memberText = memberCount == 1 ? "1 member" : memberCount + " members";
        tvMemberCountLabel.setText(memberText);

        if (sessionManager.getCurrentUser() != null &&
            team.getOwnerId().equals(sessionManager.getCurrentUser().getId())) {
            btnLeaveTeam.setVisibility(View.GONE);
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
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS && resource.getData() != null) {
                teamMemberAdapter.setTeamMembers(resource.getData());
            }
        });
    }

    private void fetchTeamSnippets() {
        Map<String, String> filters = new HashMap<>();
        teamViewModel.fetchTeamSnippets(teamId, filters);
        teamViewModel.getTeamSnippetsResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS && resource.getData() != null) {
                teamSnippetAdapter.setTeamSnippets(resource.getData());
            }
        });
    }

    @Override
    public void onItemClick(TeamMember member) {
        Toast.makeText(this, "Member: " + member.getUsername(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreOptionsClick(TeamMember member, View view) {
        String[] options = {"View Profile", "Message", "Remove from Team"};
        new AlertDialog.Builder(this)
                .setTitle(member.getUsername())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: Toast.makeText(this, "View profile", Toast.LENGTH_SHORT).show(); break;
                        case 1: Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show(); break;
                        case 2: Toast.makeText(this, "Remove member", Toast.LENGTH_SHORT).show(); break;
                    }
                })
                .show();
    }
}
