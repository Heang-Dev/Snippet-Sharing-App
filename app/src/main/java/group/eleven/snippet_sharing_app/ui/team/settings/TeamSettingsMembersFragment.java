package group.eleven.snippet_sharing_app.ui.team.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import static group.eleven.snippet_sharing_app.data.repository.AuthRepository.Resource; // Static import for Resource
import group.eleven.snippet_sharing_app.ui.team.TeamMemberAdapter;
import group.eleven.snippet_sharing_app.ui.team.TeamSettingsActivity;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;
import group.eleven.snippet_sharing_app.utils.FormValidator; // Assuming a static method for email validation

public class TeamSettingsMembersFragment extends Fragment implements TeamMemberAdapter.OnItemClickListener {

    private TextInputLayout tilInviteEmail;
    private TextInputEditText etInviteEmail;
    private MaterialButton btnSendInvite;
    private RecyclerView rvCurrentMembers;
    private TextView tvPendingInvitationsHeader; // For visibility control
    private RecyclerView rvPendingInvitations;

    private TeamMemberAdapter teamMemberAdapter;
    // private TeamInvitationAdapter teamInvitationAdapter; // Will create later

    private TeamViewModel teamViewModel;
    private String teamId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof TeamSettingsActivity) {
            teamId = ((TeamSettingsActivity) getActivity()).getTeamId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_settings_members, container, false);
        initViews(view);
        setupRecyclerViews();
        setupViewModel();
        setupListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (teamId != null) {
            teamViewModel.fetchTeamMembers(teamId);
            teamViewModel.fetchMyTeamInvitations(); // Fetch invitations for the current user (as a placeholder for pending for this team)
        }
    }

    private void initViews(View view) {
        tilInviteEmail = view.findViewById(R.id.til_invite_email);
        etInviteEmail = view.findViewById(R.id.et_invite_email);
        btnSendInvite = view.findViewById(R.id.btn_send_invite);
        rvCurrentMembers = view.findViewById(R.id.rv_current_members);
        tvPendingInvitationsHeader = view.findViewById(R.id.tv_pending_invitations_header);
        rvPendingInvitations = view.findViewById(R.id.rv_pending_invitations);
    }

    private void setupRecyclerViews() {
        teamMemberAdapter = new TeamMemberAdapter(this); // 'this' for OnItemClickListener
        rvCurrentMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCurrentMembers.setAdapter(teamMemberAdapter);

        // teamInvitationAdapter = new TeamInvitationAdapter(getContext(), this); // Will create this later
        // rvPendingInvitations.setLayoutManager(new LinearLayoutManager(getContext()));
        // rvPendingInvitations.setAdapter(teamInvitationAdapter);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(requireActivity()).get(TeamViewModel.class);

        teamViewModel.getTeamMembersResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null) {
                    teamMemberAdapter.setTeamMembers(resource.getData());
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error loading members: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        teamViewModel.getInviteTeamMemberResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                btnSendInvite.setEnabled(false);
            } else {
                btnSendInvite.setEnabled(true);
            }

            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Invitation sent!", Toast.LENGTH_SHORT).show();
                etInviteEmail.setText(""); // Clear input
                // Optionally refresh members or pending invitations
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error sending invitation: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // TODO: Observe getMyTeamInvitationsResult once TeamInvitationAdapter is implemented
    }

    private void setupListeners() {
        btnSendInvite.setOnClickListener(v -> inviteMember());
    }

    private void inviteMember() {
        if (teamId == null) {
            Toast.makeText(getContext(), "Team ID is missing, cannot invite member.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = etInviteEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilInviteEmail.setError("Please enter a valid email address.");
            return;
        } else {
            tilInviteEmail.setError(null);
        }

        Map<String, String> inviteData = new HashMap<>();
        inviteData.put("email", email);

        teamViewModel.inviteTeamMember(teamId, inviteData);
    }

    @Override
    public void onItemClick(group.eleven.snippet_sharing_app.data.model.TeamMember member) {
        // TODO: Show member details or options
        Toast.makeText(getContext(), "Member clicked: " + member.getUsername(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreOptionsClick(group.eleven.snippet_sharing_app.data.model.TeamMember member, View view) {
        // TODO: Show popup menu for member actions (e.g., Change Role, Remove Member)
        Toast.makeText(getContext(), "More options for: " + member.getUsername(), Toast.LENGTH_SHORT).show();
    }
}
