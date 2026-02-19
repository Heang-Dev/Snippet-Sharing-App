package group.eleven.snippet_sharing_app.ui.team.settings;

import android.content.DialogInterface;
import android.content.Intent; // Added import for Intent
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import static group.eleven.snippet_sharing_app.data.repository.AuthRepository.Resource; // Static import for Resource

import group.eleven.snippet_sharing_app.ui.team.TeamSettingsActivity;
import group.eleven.snippet_sharing_app.ui.team.TeamsListActivity;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;
import group.eleven.snippet_sharing_app.utils.SessionManager; // To get current user ID

public class TeamSettingsDangerZoneFragment extends Fragment {

    private Spinner spinnerNewOwner;
    private MaterialButton btnTransferOwnership;
    private MaterialButton btnDeleteTeam;
    private TextView tvTransferOwnershipDesc;
    private TextView tvDeleteTeamDesc;
    private TextView tvDeleteTeamHeader;
    private TextView tvTransferOwnershipHeader;

    private TeamViewModel teamViewModel;
    private String teamId;
    private Team currentTeam;
    private SessionManager sessionManager;

    // List of current team members for the new owner spinner
    private List<TeamMember> teamMembers = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_team_settings_danger_zone, container, false);
        initViews(view);
        setupViewModel();
        setupListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (teamId != null) {
            teamViewModel.fetchTeamDetails(teamId);
            teamViewModel.fetchTeamMembers(teamId);
        }
    }

    private void initViews(View view) {
        spinnerNewOwner = view.findViewById(R.id.spinner_new_owner);
        btnTransferOwnership = view.findViewById(R.id.btn_transfer_ownership);
        btnDeleteTeam = view.findViewById(R.id.btn_delete_team);
        tvTransferOwnershipDesc = view.findViewById(R.id.tv_transfer_ownership_desc);
        tvDeleteTeamDesc = view.findViewById(R.id.tv_delete_team_desc);
        tvDeleteTeamHeader = view.findViewById(R.id.tv_delete_team_header);
        tvTransferOwnershipHeader = view.findViewById(R.id.tv_transfer_ownership_header);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(requireActivity()).get(TeamViewModel.class);

        teamViewModel.getTeamDetailsResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                currentTeam = resource.getData();
                checkOwnerStatus();
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error loading team details: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        teamViewModel.getTeamMembersResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                if (resource.getData() != null) {
                    teamMembers.clear();
                    // Filter out the current owner from the list for transfer
                    String currentOwnerId = currentTeam != null ? currentTeam.getOwnerId() : "";
                    for (TeamMember member : resource.getData()) {
                        if (!member.getUserId().equals(currentOwnerId)) {
                            teamMembers.add(member);
                        }
                    }
                    setupNewOwnerSpinner();
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error loading team members: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        teamViewModel.getTransferTeamOwnershipResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                btnTransferOwnership.setEnabled(false);
            } else {
                btnTransferOwnership.setEnabled(true);
            }
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Ownership transferred successfully!", Toast.LENGTH_SHORT).show();
                // Optionally navigate back or refresh team details
                if (getActivity() != null) getActivity().finish();
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error transferring ownership: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        teamViewModel.getDeleteTeamResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                btnDeleteTeam.setEnabled(false);
            } else {
                btnDeleteTeam.setEnabled(true);
            }
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Team deleted successfully!", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    // Navigate back to team list or home
                    Intent intent = new Intent(getActivity(), TeamsListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error deleting team: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        btnTransferOwnership.setOnClickListener(v -> confirmTransferOwnership());
        btnDeleteTeam.setOnClickListener(v -> confirmDeleteTeam());
    }

    private void checkOwnerStatus() {
        String currentUserId = sessionManager.getCurrentUser() != null ? sessionManager.getCurrentUser().getId() : null;
        if (currentTeam != null && currentUserId != null && currentTeam.getOwnerId().equals(currentUserId)) {
            // Current user is the owner, enable danger zone actions
            spinnerNewOwner.setEnabled(true);
            btnTransferOwnership.setEnabled(true);
            btnDeleteTeam.setEnabled(true);
            // Also enable the whole danger zone UI if it was disabled
        } else {
            // Current user is not the owner, disable danger zone actions
            spinnerNewOwner.setEnabled(false);
            btnTransferOwnership.setEnabled(false);
            btnDeleteTeam.setEnabled(false);
            // Hide/show messages indicating only owner can do this
            tvTransferOwnershipDesc.setText("Only the team owner can transfer ownership.");
            tvDeleteTeamDesc.setText("Only the team owner can delete the team.");
            btnTransferOwnership.setVisibility(View.GONE);
            spinnerNewOwner.setVisibility(View.GONE);
            btnDeleteTeam.setVisibility(View.GONE);
            tvTransferOwnershipHeader.setVisibility(View.GONE);
            tvDeleteTeamHeader.setVisibility(View.GONE);
        }
    }

    private void setupNewOwnerSpinner() {
        List<String> memberUsernames = new ArrayList<>();
        for (TeamMember member : teamMembers) {
            memberUsernames.add(member.getUsername() + " (" + member.getEmail() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, memberUsernames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNewOwner.setAdapter(adapter);
    }

    private void confirmTransferOwnership() {
        if (spinnerNewOwner.getSelectedItemPosition() == Spinner.INVALID_POSITION || teamMembers.isEmpty()) {
            Toast.makeText(getContext(), "Please select a new owner.", Toast.LENGTH_SHORT).show();
            return;
        }

        TeamMember selectedNewOwner = teamMembers.get(spinnerNewOwner.getSelectedItemPosition());

        new AlertDialog.Builder(requireContext())
                .setTitle("Transfer Ownership")
                .setMessage("Are you sure you want to transfer ownership of '" + currentTeam.getName() + "' to " + selectedNewOwner.getUsername() + "? This action cannot be easily undone.")
                .setPositiveButton("Transfer", (dialog, which) -> transferOwnership(selectedNewOwner.getUserId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void transferOwnership(String newOwnerId) {
        if (teamId == null || newOwnerId == null) {
            Toast.makeText(getContext(), "Error: Team ID or new owner ID missing.", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> newOwnerData = new HashMap<>();
        newOwnerData.put("new_owner_id", newOwnerId);
        teamViewModel.transferOwnership(teamId, newOwnerData);
    }

    private void confirmDeleteTeam() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Team")
                .setMessage("Are you absolutely sure you want to delete '" + currentTeam.getName() + "'? All data will be lost and this action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteTeam())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTeam() {
        if (teamId == null) {
            Toast.makeText(getContext(), "Error: Team ID missing.", Toast.LENGTH_SHORT).show();
            return;
        }
        teamViewModel.deleteTeam(teamId);
    }
}
