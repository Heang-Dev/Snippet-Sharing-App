package group.eleven.snippet_sharing_app.ui.team;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;
import group.eleven.snippet_sharing_app.utils.FormValidator;

public class CreateTeamActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
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
    private MaterialButton btnCreateTeam;
    private MaterialButton btnCancel;

    private Uri selectedImageUri;
    private TeamViewModel teamViewModel;

    // ActivityResultLauncher for image selection
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        initViews();
        setupToolbar();
        setupViewModel();
        setupListeners();
        setupImagePicker();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivTeamAvatarPreview = findViewById(R.id.iv_team_avatar_preview);
        btnUploadAvatar = findViewById(R.id.btn_upload_avatar);
        tilTeamName = findViewById(R.id.til_team_name);
        etTeamName = findViewById(R.id.et_team_name);
        tilTeamDescription = findViewById(R.id.til_team_description);
        etTeamDescription = findViewById(R.id.et_team_description);
        rgPrivacySettings = findViewById(R.id.rg_privacy_settings);
        rbPublic = findViewById(R.id.rb_public);
        rbPrivate = findViewById(R.id.rb_private);
        rbInviteOnly = findViewById(R.id.rb_invite_only);
        btnCreateTeam = findViewById(R.id.btn_create_team);
        btnCancel = findViewById(R.id.btn_cancel);

        // Set default privacy selection
        rbPublic.setChecked(true);
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

    private void setupViewModel() {
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        teamViewModel.getCreateTeamResult().observe(this, resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                // Show loading indicator, disable buttons
                btnCreateTeam.setEnabled(false);
                btnCancel.setEnabled(false);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(this, "Team created successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Indicate success to calling activity
                finish();
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(this, "Error creating team: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                btnCreateTeam.setEnabled(true);
                btnCancel.setEnabled(true);
            }
        });
    }

    private void setupListeners() {
        btnUploadAvatar.setOnClickListener(v -> openImagePicker());
        btnCreateTeam.setOnClickListener(v -> createTeam());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .placeholder(R.drawable.ic_collections) // Default placeholder
                                    .error(R.drawable.ic_collections) // Error placeholder
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

    private void createTeam() {
        // Validate inputs
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

        if (selectedImageUri != null) {
            // Use multipart request with avatar
            teamViewModel.createTeamWithAvatar(teamName, teamDescription, privacy, selectedImageUri);
        } else {
            // Use regular request without avatar
            Map<String, String> teamData = new HashMap<>();
            teamData.put("name", teamName);
            teamData.put("description", teamDescription);
            teamData.put("privacy", privacy);
            teamViewModel.createTeam(teamData);
        }
    }
}
