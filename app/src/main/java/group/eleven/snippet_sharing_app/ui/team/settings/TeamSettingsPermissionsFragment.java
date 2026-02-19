package group.eleven.snippet_sharing_app.ui.team.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import static group.eleven.snippet_sharing_app.data.repository.AuthRepository.Resource; // Static import for Resource

import group.eleven.snippet_sharing_app.ui.team.TeamSettingsActivity;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;

public class TeamSettingsPermissionsFragment extends Fragment {

    private Spinner spinnerRoles;
    private LinearLayout llPermissionsContainer;
    private RadioGroup rgDefaultSnippetPrivacy;
    private RadioButton rbSnippetPublic;
    private RadioButton rbSnippetTeamOnly;
    private RadioButton rbSnippetPrivate;
    private MaterialButton btnSavePermissions;

    private TeamViewModel teamViewModel;
    private String teamId;
    private Team currentTeam; // To store current team details for owner check

    // Placeholder for permission states - in a real app, this would be fetched from API
    private Map<String, Map<String, Boolean>> rolePermissions = new HashMap<>(); // Role -> Permission -> State
    private List<String> availablePermissions = new ArrayList<>();

    public void onCreate() {
        onCreate(null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof TeamSettingsActivity) {
            teamId = ((TeamSettingsActivity) getActivity()).getTeamId();
        }

        // Initialize placeholder permissions (this would come from API in a real app)
        availablePermissions.add("create_snippets");
        availablePermissions.add("edit_all_snippets");
        availablePermissions.add("delete_snippets");
        availablePermissions.add("invite_members");
        availablePermissions.add("remove_members");
        availablePermissions.add("change_member_roles");
        availablePermissions.add("manage_permissions");

        // Default permissions for roles (example data)
        Map<String, Boolean> ownerPerms = new HashMap<>();
        for (String perm : availablePermissions) { ownerPerms.put(perm, true); }
        rolePermissions.put("owner", ownerPerms);

        Map<String, Boolean> adminPerms = new HashMap<>();
        adminPerms.put("create_snippets", true);
        adminPerms.put("edit_all_snippets", true);
        adminPerms.put("delete_snippets", true);
        adminPerms.put("invite_members", true);
        adminPerms.put("remove_members", true);
        adminPerms.put("change_member_roles", false);
        adminPerms.put("manage_permissions", false);
        rolePermissions.put("admin", adminPerms);

        Map<String, Boolean> memberPerms = new HashMap<>();
        memberPerms.put("create_snippets", true);
        memberPerms.put("edit_all_snippets", false);
        memberPerms.put("delete_snippets", false);
        memberPerms.put("invite_members", false);
        memberPerms.put("remove_members", false);
        memberPerms.put("change_member_roles", false);
        memberPerms.put("manage_permissions", false);
        rolePermissions.put("member", memberPerms);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_settings_permissions, container, false);
        initViews(view);
        setupViewModel();
        setupSpinner();
        setupListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (teamId != null) {
            teamViewModel.fetchTeamDetails(teamId); // To get current team details for default snippet privacy and owner check
        }
    }

    private void initViews(View view) {
        spinnerRoles = view.findViewById(R.id.spinner_roles);
        llPermissionsContainer = view.findViewById(R.id.ll_permissions_container);
        rgDefaultSnippetPrivacy = view.findViewById(R.id.rg_default_snippet_privacy);
        rbSnippetPublic = view.findViewById(R.id.rb_snippet_public);
        rbSnippetTeamOnly = view.findViewById(R.id.rb_snippet_team_only);
        rbSnippetPrivate = view.findViewById(R.id.rb_snippet_private);
        btnSavePermissions = view.findViewById(R.id.btn_save_permissions);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(requireActivity()).get(TeamViewModel.class);

        teamViewModel.getTeamDetailsResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                currentTeam = resource.getData();
                if (currentTeam != null) {
                    // Set default snippet privacy
                    if (currentTeam.getPrivacy().equalsIgnoreCase("public")) {
                        rbSnippetPublic.setChecked(true);
                    } else if (currentTeam.getPrivacy().equalsIgnoreCase("private")) { // Assuming private means team-only for snippet privacy within team context
                        rbSnippetTeamOnly.setChecked(true);
                    }
                    // For now, let's assume `Team.privacy` translates to `default_snippet_privacy` for simplicity.
                    // A real API might have a separate field for default snippet privacy.
                }
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error loading team details for permissions: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        teamViewModel.getUpdateTeamResult().observe(getViewLifecycleOwner(), resource -> { // Reusing updateTeam for default snippet privacy
            if (resource.getStatus() == Resource.Status.LOADING) {
                btnSavePermissions.setEnabled(false);
            } else {
                btnSavePermissions.setEnabled(true);
            }

            if (resource.getStatus() == Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Permissions and privacy updated!", Toast.LENGTH_SHORT).show();
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error updating permissions: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // TODO: Observe updateTeamMemberRoleResult for role changes
    }

    private void setupSpinner() {
        // Assume roles are hardcoded for now or fetched from backend
        String[] roles = {"owner", "admin", "member"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoles.setAdapter(adapter);

        spinnerRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = (String) parent.getItemAtPosition(position);
                displayPermissionsForRole(selectedRole);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set default selection to "member" or "admin" if available
        int defaultSelection = 0; // Owner
        if (rolePermissions.containsKey("admin")) defaultSelection = 1; // Admin
        spinnerRoles.setSelection(defaultSelection);
    }

    private void displayPermissionsForRole(String role) {
        llPermissionsContainer.removeAllViews(); // Clear previous permissions

        Map<String, Boolean> perms = rolePermissions.get(role);
        if (perms != null) {
            for (Map.Entry<String, Boolean> entry : perms.entrySet()) {
                SwitchMaterial switchMaterial = new SwitchMaterial(getContext());
                switchMaterial.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                switchMaterial.setText(formatPermissionName(entry.getKey()));
                switchMaterial.setChecked(entry.getValue());
                switchMaterial.setPadding(0, 8, 0, 8);
                switchMaterial.setTextSize(16f);

                // Owner role cannot have permissions changed
                switchMaterial.setEnabled(!role.equalsIgnoreCase("owner"));

                llPermissionsContainer.addView(switchMaterial);
            }
        }
    }

    private String formatPermissionName(String permissionKey) {
        // Convert "create_snippets" to "Create Snippets"
        String[] parts = permissionKey.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    private void setupListeners() {
        btnSavePermissions.setOnClickListener(v -> savePermissions());
    }

    private void savePermissions() {
        if (teamId == null || currentTeam == null) {
            Toast.makeText(getContext(), "Team ID or details missing, cannot save changes.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected default snippet privacy
        String defaultSnippetPrivacy = "public"; // Default
        int selectedPrivacyId = rgDefaultSnippetPrivacy.getCheckedRadioButtonId();
        if (selectedPrivacyId == R.id.rb_snippet_team_only) {
            defaultSnippetPrivacy = "team_only";
        } else if (selectedPrivacyId == R.id.rb_snippet_private) {
            defaultSnippetPrivacy = "private";
        }

        // For now, let's just update the team's privacy field for simplicity,
        // as there's no explicit 'default_snippet_privacy' in the Team model.
        // A real implementation would send a specific update for permissions and default privacy.
        Map<String, String> updateData = new HashMap<>();
        updateData.put("default_snippet_privacy", defaultSnippetPrivacy); // Assuming API accepts this
        // TODO: Gather permission changes from dynamically created switches and send to API
        // This would require iterating through llPermissionsContainer and getting state of each SwitchMaterial

        teamViewModel.updateTeam(teamId, updateData); // Reusing updateTeam for this for now
        Toast.makeText(getContext(), "Saving permissions (logic to be fully implemented)", Toast.LENGTH_SHORT).show();
    }
}
