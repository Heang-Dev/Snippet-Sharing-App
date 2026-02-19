package group.eleven.snippet_sharing_app.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import group.eleven.snippet_sharing_app.R;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    private TextView tabSnippets, tabStars, tabCollections;
    private TextView tvName, tvUsername, tvBio, tvLocation, tvWebsite;
    private ImageView ivProfile;
    private LinearLayout llContentList;
    private static final String PREFS_NAME = "UserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupNavigation();
        setupTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void initViews() {
        tabSnippets = findViewById(R.id.tabSnippets);
        tabStars = findViewById(R.id.tabStars);
        tabCollections = findViewById(R.id.tabCollections);
        llContentList = findViewById(R.id.llContentList);
        
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvBio = findViewById(R.id.tvBio);
        tvLocation = findViewById(R.id.tvLocation);
        tvWebsite = findViewById(R.id.tvWebsite);
        ivProfile = findViewById(R.id.ivProfile);
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        if (tvName != null) tvName.setText(prefs.getString("full_name", "Alex Dev"));
        if (tvUsername != null) tvUsername.setText(prefs.getString("username", "@alexcodes"));
        if (tvBio != null) tvBio.setText(prefs.getString("bio", "Full-stack wizard building tools for builders. Love React, Python, and dark coffee."));
        if (tvLocation != null) tvLocation.setText(prefs.getString("location", "Seattle, WA"));
        if (tvWebsite != null) tvWebsite.setText(prefs.getString("website", "alex.dev"));
        
        String imagePath = prefs.getString("profile_image_path", null);
        if (imagePath != null && ivProfile != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                ivProfile.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

    private void setupNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        View settingsIcon = findViewById(R.id.ivSettings);
        if (settingsIcon != null) {
            settingsIcon.setOnClickListener(v -> 
                startActivity(new Intent(ProfileActivity.this, AccountSettingsActivity.class))
            );
        }

        View profileImageFrame = findViewById(R.id.flProfileImage);
        if (profileImageFrame != null) {
            profileImageFrame.setOnClickListener(v -> 
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class))
            );
        }

        View messageIcon = findViewById(R.id.cvMessage);
        if (messageIcon != null) {
            messageIcon.setOnClickListener(v -> 
                startActivity(new Intent(ProfileActivity.this, NotificationSettingsActivity.class))
            );
        }

        View followBtn = findViewById(R.id.btnFollow);
        if (followBtn != null) {
            followBtn.setOnClickListener(v -> 
                startActivity(new Intent(ProfileActivity.this, PrivacySettingsActivity.class))
            );
        }
    }

    private void setupTabs() {
        tabSnippets.setOnClickListener(v -> {
            updateTabUI(tabSnippets);
            showContent("snippets");
        });

        tabStars.setOnClickListener(v -> {
            updateTabUI(tabStars);
            showContent("stars");
        });

        tabCollections.setOnClickListener(v -> {
            updateTabUI(tabCollections);
            showContent("collections");
        });
    }

    private void updateTabUI(TextView selectedTab) {
        resetTabs();
        selectedTab.setBackgroundResource(R.drawable.bg_pill_badge);
        selectedTab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.profile_primary));
        selectedTab.setTextColor(ContextCompat.getColor(this, R.color.profile_bg));
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void showContent(String type) {
        llContentList.removeAllViews();
        if (type.equals("snippets")) {
            getLayoutInflater().inflate(R.layout.item_snippet_card_dark, llContentList);
            getLayoutInflater().inflate(R.layout.item_snippet_card_dark_python, llContentList);
        } else if (type.equals("stars")) {
            getLayoutInflater().inflate(R.layout.item_snippet_card_dark_python, llContentList);
        } else if (type.equals("collections")) {
            getLayoutInflater().inflate(R.layout.item_snippet_card_dark, llContentList);
        }
    }

    private void resetTabs() {
        TextView[] tabs = {tabSnippets, tabStars, tabCollections};
        for (TextView tab : tabs) {
            if (tab != null) {
                tab.setBackground(null);
                tab.setTextColor(ContextCompat.getColor(this, R.color.profile_text_muted));
                tab.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }
}
