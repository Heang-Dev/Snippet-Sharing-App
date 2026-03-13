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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
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
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.ui.team.TeamMemberAdapter;
import group.eleven.snippet_sharing_app.ui.team.TeamSettingsActivity;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;
import group.eleven.snippet_sharing_app.utils.SessionManager;

public class TeamSettingsMembersFragment extends Fragment implements TeamMemberAdapter.OnItemClickListener {

    private TextInputLayout tilInviteEmail;
    private TextInputEditText etInviteEmail;
    private MaterialButton btnSendInvite;
    private RecyclerView rvCurrentMembers;
    private TextView tvPendingInvitationsHeader;
    private RecyclerView rvPendingInvitations;

    private TeamMemberAdapter teamMemberAdapter;

    private TeamViewModel teamViewModel;
    private String teamId;
    private String currentUserRole;
    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof TeamSettingsActivity) {
            teamId = ((TeamSettingsActivity) getActivity()).getTeamId();
        }
        sessionManager = new SessionManager(requireContext());
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
            teamViewModel.fetchTeamDetails(teamId);
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
        teamMemberAdapter = new TeamMemberAdapter(this);
        rvCurrentMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCurrentMembers.setAdapter(teamMemberAdapter);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(requireActivity()).get(TeamViewModel.class);

        // Observe team details to get current user role
        teamViewModel.getTeamDetailsResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS && resource.getData() != null) {
                Team team = resource.getData();
                String currentUserId = sessionManager.getCurrentUser() != null ? sessionManager.getCurrentUser().getId() : null;
                if (currentUserId != null && currentUserId.equals(team.getOwnerId())) {
                    currentUserRole = "owner";
                }
            }
        });

        teamViewModel.getTeamMembersResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null) {
                    teamMemberAdapter.setTeamMembers(resource.getData());
                    // Determine current user's role from the members list
                    String currentUserId = sessionManager.getCurrentUser() != null ? sessionManager.getCurrentUser().getId() : null;
                    if (currentUserId != null) {
                        for (TeamMember m : resource.getData()) {
                            if (currentUserId.equals(m.getUserId())) {
                                currentUserRole = m.getRole();
                                break;
                            }
                        }
                    }
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
                etInviteEmail.setText("");
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error sending invitation: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Observe remove member result
        teamViewModel.getRemoveTeamMemberResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Member removed successfully", Toast.LENGTH_SHORT).show();
                if (teamId != null) teamViewModel.fetchTeamMembers(teamId);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error removing member: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Observe role update result
        teamViewModel.getUpdateTeamMemberRoleResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Role updated successfully", Toast.LENGTH_SHORT).show();
                if (teamId != null) teamViewModel.fetchTeamMembers(teamId);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error updating role: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

    private boolean canManageMembers() {
        return "owner".equalsIgnoreCase(currentUserRole) || "admin".equalsIgnoreCase(currentUserRole);
    }

    @Override
    public void onItemClick(TeamMember member) {
        Toast.makeText(getContext(), member.getUsername() + " (" + member.getRole() + ")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreOptionsClick(TeamMember member, View view) {
        if (!canManageMembers()) {
            Toast.makeText(getContext(), "Only owners and admins can manage members", Toast.LENGTH_SHORT).show();
            return;
        }

        // Don't allow actions on the owner
        if ("owner".equalsIgnoreCase(member.getRole())) {
            Toast.makeText(getContext(), "Cannot modify the team owner", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.menu_member_options);

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_change_role) {
                showChangeRoleDialog(member);
                return true;
            } else if (id == R.id.action_remove_member) {
                showRemoveMemberDialog(member);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showChangeRoleDialog(TeamMember member) {
        String[] roles = {"admin", "member", "viewer"};
        String[] roleLabels = {"Admin", "Member", "Viewer"};

        int currentIndex = 0;
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equalsIgnoreCase(member.getRole())) {
                currentIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Role for " + member.getUsername())
                .setSingleChoiceItems(roleLabels, currentIndex, null)
                .setPositiveButton("Update", (dialog, which) -> {
                    int selectedIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    String newRole = roles[selectedIndex];

                    if (newRole.equalsIgnoreCase(member.getRole())) {
                        return; // No change
                    }

                    Map<String, String> roleData = new HashMap<>();
                    roleData.put("role", newRole);
                    teamViewModel.updateTeamMemberRole(teamId, member.getUserId(), roleData);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRemoveMemberDialog(TeamMember member) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Member")
                .setMessage("Are you sure you want to remove " + member.getUsername() + " from this team?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    teamViewModel.removeTeamMember(teamId, member.getUserId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
