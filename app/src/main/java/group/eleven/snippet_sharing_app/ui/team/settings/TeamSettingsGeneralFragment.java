package group.eleven.snippet_sharing_app.ui.team.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import static group.eleven.snippet_sharing_app.data.repository.AuthRepository.Resource; // Static import for Resource

import group.eleven.snippet_sharing_app.ui.team.TeamSettingsActivity;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;
import group.eleven.snippet_sharing_app.utils.FormValidator;

public class TeamSettingsGeneralFragment extends Fragment {

    private ImageView ivTeamAvatarPreview;
    private MaterialButton btnUploadAvatar;
    private TextInputLayout tilTeamName;
    private TextInputEditText etTeamName;
    private TextInputLayout tilTeamDescription;
    private TextInputEditText etTeamDescription;
    private RadioGroup rgPrivacySettings;
    private RadioButton rbPublic;
    private RadioButton rbPrivate;
    private RadioButton rbInviteOnly;
    private MaterialButton btnSaveChanges;

    private Uri selectedImageUri;
    private TeamViewModel teamViewModel;
    private String teamId;

    // ActivityResultLauncher for image selection
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get teamId from parent activity
        if (getActivity() instanceof TeamSettingsActivity) {
            teamId = ((TeamSettingsActivity) getActivity()).getTeamId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_settings_general, container, false);
        initViews(view);
        setupViewModel();
        setupListeners();
        setupImagePicker();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch current team details to pre-populate fields
        if (teamId != null) {
            teamViewModel.fetchTeamDetails(teamId);
        }
    }

    private void initViews(View view) {
        ivTeamAvatarPreview = view.findViewById(R.id.iv_team_avatar_preview);
        btnUploadAvatar = view.findViewById(R.id.btn_upload_avatar);
        tilTeamName = view.findViewById(R.id.til_team_name);
        etTeamName = view.findViewById(R.id.et_team_name);
        tilTeamDescription = view.findViewById(R.id.til_team_description);
        etTeamDescription = view.findViewById(R.id.et_team_description);
        rgPrivacySettings = view.findViewById(R.id.rg_privacy_settings);
        rbPublic = view.findViewById(R.id.rb_public);
        rbPrivate = view.findViewById(R.id.rb_private);
        rbInviteOnly = view.findViewById(R.id.rb_invite_only);
        btnSaveChanges = view.findViewById(R.id.btn_save_changes);
    }

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(requireActivity()).get(TeamViewModel.class);

        teamViewModel.getTeamDetailsResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Team team = resource.getData();
                if (team != null) {
                    displayTeamDetails(team);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error loading team details: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        teamViewModel.getUpdateTeamResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                btnSaveChanges.setEnabled(false);
            } else {
                btnSaveChanges.setEnabled(true);
            }

            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Team details updated successfully!", Toast.LENGTH_SHORT).show();
                // Optionally update UI with new data if needed, or refetch details
                // teamViewModel.fetchTeamDetails(teamId);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Error updating team: " + resource.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        btnUploadAvatar.setOnClickListener(v -> openImagePicker());
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .placeholder(R.drawable.ic_collections)
                                    .error(R.drawable.ic_collections)
                                    .into(ivTeamAvatarPreview);
                        }
                    }
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void displayTeamDetails(Team team) {
        etTeamName.setText(team.getName());
        etTeamDescription.setText(team.getDescription());

        // Set privacy radio button
        if (team.getPrivacy().equalsIgnoreCase("public")) {
            rbPublic.setChecked(true);
        } else if (team.getPrivacy().equalsIgnoreCase("private")) {
            rbPrivate.setChecked(true);
        } else if (team.getPrivacy().equalsIgnoreCase("invite-only")) {
            rbInviteOnly.setChecked(true);
        }

        Glide.with(this)
                .load(team.getAvatarUrl())
                .placeholder(R.drawable.ic_collections)
                .error(R.drawable.ic_collections)
                .into(ivTeamAvatarPreview);
    }

    private void saveChanges() {
        if (teamId == null) {
            Toast.makeText(getContext(), "Team ID is missing, cannot save changes.", Toast.LENGTH_SHORT).show();
            return;
        }

        String teamName = etTeamName.getText().toString().trim();
        String teamDescription = etTeamDescription.getText().toString().trim();

        if (!FormValidator.isTeamNameValid(teamName)) {
            tilTeamName.setError("Team name is required and must be 3-50 characters.");
            return;
        } else {
            tilTeamName.setError(null);
        }

        String privacy = "public"; // Default
        int selectedPrivacyId = rgPrivacySettings.getCheckedRadioButtonId();
        if (selectedPrivacyId == R.id.rb_private) {
            privacy = "private";
        } else if (selectedPrivacyId == R.id.rb_invite_only) {
            privacy = "invite-only";
        }

        Map<String, String> updateData = new HashMap<>();
        updateData.put("name", teamName);
        updateData.put("description", teamDescription);
        updateData.put("privacy", privacy);
        // TODO: Handle avatar upload if selectedImageUri is not null.

        teamViewModel.updateTeam(teamId, updateData);
    }
}
