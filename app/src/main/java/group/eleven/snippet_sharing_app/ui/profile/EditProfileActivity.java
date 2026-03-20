package group.eleven.snippet_sharing_app.ui.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.repository.ProfileRepository;
import group.eleven.snippet_sharing_app.utils.KeyboardUtils;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Edit Profile Activity - Allows users to edit their profile information
 */
public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private static final int MAX_BIO_LENGTH = 160;

    // Views
    private Toolbar toolbar;
    private CircleImageView ivProfile;
    private FrameLayout flChangePhoto;
    private TextInputEditText etFullName, etUsername, etEmail, etBio;
    private TextInputEditText etWebsite, etLocation, etGithub, etTwitter;
    private TextView tvBioCharCount, tvEmailStatus;
    private MaterialButton btnSaveChanges, btnCancel;
    private FrameLayout layoutLoading;

    // Data
    private SessionManager sessionManager;
    private ProfileRepository profileRepository;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private String selectedImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        profileRepository = new ProfileRepository(this);

        initViews();

        // Setup keyboard dismiss on outside touch
        View rootView = findViewById(android.R.id.content);
        KeyboardUtils.setupKeyboardDismissOnOutsideTouch(this, rootView);

        setupToolbar();
        setupImagePicker();
        setupClickListeners();
        setupBioCharCounter();
        loadUserData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivProfile = findViewById(R.id.ivProfile);
        flChangePhoto = findViewById(R.id.flChangePhoto);
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etBio = findViewById(R.id.etBio);
        etWebsite = findViewById(R.id.etWebsite);
        etLocation = findViewById(R.id.etLocation);
        etGithub = findViewById(R.id.etGithub);
        etTwitter = findViewById(R.id.etTwitter);
        tvBioCharCount = findViewById(R.id.tvBioCharCount);
        tvEmailStatus = findViewById(R.id.tvEmailStatus);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
        layoutLoading = findViewById(R.id.layoutLoading);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String savedPath = saveImageToInternalStorage(uri);
                        if (savedPath != null) {
                            selectedImagePath = savedPath;
                            ivProfile.setImageURI(Uri.fromFile(new File(savedPath)));
                            Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        // Change photo
        flChangePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        findViewById(R.id.tvChangePhoto).setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Save changes
        btnSaveChanges.setOnClickListener(v -> saveProfile());

        // Cancel
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupBioCharCounter() {
        etBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                tvBioCharCount.setText(length + "/" + MAX_BIO_LENGTH + " characters");

                // Change color if approaching limit
                if (length > MAX_BIO_LENGTH) {
                    tvBioCharCount.setTextColor(getResources().getColor(R.color.error, getTheme()));
                } else {
                    tvBioCharCount.setTextColor(getResources().getColor(R.color.text_muted, getTheme()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUserData() {
        User user = sessionManager.getUser();

        if (user != null) {
            // Full name
            String fullName = user.getFullName();
            if (fullName != null && !fullName.isEmpty()) {
                etFullName.setText(fullName);
            }

            // Username
            String username = user.getUsername();
            if (username != null && !username.isEmpty()) {
                etUsername.setText("@" + username);
            }

            // Email
            String email = user.getEmail();
            if (email != null && !email.isEmpty()) {
                etEmail.setText(email);

                // Show verified badge if email is verified
                if (user.isEmailVerified()) {
                    tvEmailStatus.setVisibility(View.VISIBLE);
                } else {
                    tvEmailStatus.setVisibility(View.GONE);
                }
            }

            // Bio
            String bio = user.getBio();
            if (bio != null && !bio.isEmpty()) {
                etBio.setText(bio);
                tvBioCharCount.setText(bio.length() + "/" + MAX_BIO_LENGTH + " characters");
            }

            // Website
            String website = user.getWebsiteUrl();
            if (website != null && !website.isEmpty()) {
                etWebsite.setText(website);
            }

            // GitHub
            String github = user.getGithubUrl();
            if (github != null && !github.isEmpty()) {
                etGithub.setText(github);
            }

            // Twitter
            String twitter = user.getTwitterUrl();
            if (twitter != null && !twitter.isEmpty()) {
                etTwitter.setText(twitter);
            }

            // Location
            String location = user.getLocation();
            if (location != null && !location.isEmpty()) {
                etLocation.setText(location);
            }

            // Avatar
            String avatarUrl = user.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivProfile);
            }
        }
    }

    private void saveProfile() {
        // Validate inputs
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String bio = etBio.getText() != null ? etBio.getText().toString().trim() : "";
        String website = etWebsite.getText() != null ? etWebsite.getText().toString().trim() : "";
        String github = etGithub.getText() != null ? etGithub.getText().toString().trim() : "";
        String twitter = etTwitter.getText() != null ? etTwitter.getText().toString().trim() : "";

        // Remove @ from username if present
        if (username.startsWith("@")) {
            username = username.substring(1);
        }

        // Basic validation
        if (fullName.isEmpty()) {
            etFullName.setError("Name is required");
            etFullName.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (bio.length() > MAX_BIO_LENGTH) {
            etBio.setError("Bio is too long");
            etBio.requestFocus();
            return;
        }

        // Show loading
        showLoading(true);

        // Get location value
        String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";

        // Call API to update profile
        profileRepository.updateProfile(
                fullName,
                username,
                bio,
                website,
                github,
                twitter,
                location,
                selectedImagePath
        ).observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                // Still loading
                return;
            }

            showLoading(false);

            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                // Show error message
                String errorMsg = resource.message != null ? resource.message : "Failed to update profile";
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        btnSaveChanges.setEnabled(!show);
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            File directory = new File(getFilesDir(), "profile_pics");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "profile_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            outputStream.flush();
            outputStream.close();
            if (inputStream != null) {
                inputStream.close();
            }

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        KeyboardUtils.handleTouchOutsideEditText(this, event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        // Check if there are unsaved changes
        // For now, just finish
        super.onBackPressed();
    }
}
