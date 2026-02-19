package group.eleven.snippet_sharing_app.ui.profile;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import group.eleven.snippet_sharing_app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private EditText etFullName, etUsername, etEmail, etBio, etLocation, etWebsite;
    private ActivityResultLauncher<String> getContent;
    private static final String PREFS_NAME = "UserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        loadExistingData();

        // Initialize Image Picker with permanent storage handling
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String savedPath = saveImageToInternalStorage(uri);
                        if (savedPath != null) {
                            ivProfile.setImageURI(Uri.fromFile(new File(savedPath)));
                            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString("profile_image_path", savedPath);
                            editor.apply();
                            Toast.makeText(this, "Photo updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Toolbar back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Save Changes button
        if (findViewById(R.id.btnSaveChanges) != null) {
            findViewById(R.id.btnSaveChanges).setOnClickListener(v -> {
                saveData();
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        }

        // Change Photo trigger
        if (findViewById(R.id.flChangePhoto) != null) {
            findViewById(R.id.flChangePhoto).setOnClickListener(v -> getContent.launch("image/*"));
        }
    }

    private void initViews() {
        ivProfile = findViewById(R.id.ivProfile);
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etBio = findViewById(R.id.etBio);
        etLocation = findViewById(R.id.etLocation);
        etWebsite = findViewById(R.id.etWebsite);
    }

    private void loadExistingData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        etFullName.setText(prefs.getString("full_name", "Alex Dev"));
        etUsername.setText(prefs.getString("username", "@alexcodes"));
        etEmail.setText(prefs.getString("email", "alex@example.com"));
        etBio.setText(prefs.getString("bio", "Full-stack wizard building tools for builders. Love React, Python, and dark coffee."));
        etLocation.setText(prefs.getString("location", "Seattle, WA"));
        etWebsite.setText(prefs.getString("website", "https://alex.dev"));
        
        String imagePath = prefs.getString("profile_image_path", null);
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                ivProfile.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("full_name", etFullName.getText().toString());
        editor.putString("username", etUsername.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.putString("bio", etBio.getText().toString());
        editor.putString("location", etLocation.getText().toString());
        editor.putString("website", etWebsite.getText().toString());
        editor.apply();
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            File directory = new File(getFilesDir(), "profile_pics");
            if (!directory.exists()) directory.mkdirs();
            
            File file = new File(directory, "profile.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
