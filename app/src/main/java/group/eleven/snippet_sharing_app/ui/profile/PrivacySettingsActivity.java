package group.eleven.snippet_sharing_app.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import group.eleven.snippet_sharing_app.R;

public class PrivacySettingsActivity extends AppCompatActivity {

    private MaterialCardView cardPublic, cardPrivate;
    private ImageView ivCheckPublic, ivCheckPrivate, ivGlobe, ivLock;
    private TextView tvPublicLabel, tvPrivateLabel;
    private MaterialSwitch swShowEmail, swShowActivity, swAllowForking;
    private boolean isPublicSelected = true;
    private static final String PREFS_NAME = "UserProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        loadSettings();
        setupVisibilityToggle();
        
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        findViewById(R.id.btnSaveSettings).setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, "Privacy settings saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void initViews() {
        cardPublic = findViewById(R.id.cardPublic);
        cardPrivate = findViewById(R.id.cardPrivate);
        ivCheckPublic = findViewById(R.id.ivCheckPublic);
        ivCheckPrivate = findViewById(R.id.ivCheckPrivate);
        ivGlobe = findViewById(R.id.ivGlobe);
        ivLock = findViewById(R.id.ivLock);
        tvPublicLabel = findViewById(R.id.tvPublicLabel);
        tvPrivateLabel = findViewById(R.id.tvPrivateLabel);
        
        swShowEmail = findViewById(R.id.swShowEmail);
        swShowActivity = findViewById(R.id.swShowActivity);
        swAllowForking = findViewById(R.id.swAllowForking);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isPublicSelected = prefs.getBoolean("profile_public", true);
        updateUI(isPublicSelected);

        if (swShowEmail != null) swShowEmail.setChecked(prefs.getBoolean("show_email", true));
        if (swShowActivity != null) swShowActivity.setChecked(prefs.getBoolean("show_activity", true));
        if (swAllowForking != null) swAllowForking.setChecked(prefs.getBoolean("allow_forking", true));
    }

    private void setupVisibilityToggle() {
        cardPublic.setOnClickListener(v -> {
            isPublicSelected = true;
            updateUI(true);
        });
        cardPrivate.setOnClickListener(v -> {
            isPublicSelected = false;
            updateUI(false);
        });
    }

    private void updateUI(boolean isPublic) {
        int activeColor = ContextCompat.getColor(this, R.color.profile_primary);
        int inactiveColor = ContextCompat.getColor(this, R.color.profile_text_muted);
        int activeBg = android.graphics.Color.parseColor("#1A1400");
        int normalBg = ContextCompat.getColor(this, R.color.profile_card);

        if (isPublic) {
            // Public Active
            cardPublic.setStrokeWidth(dpToPx(2));
            cardPublic.setStrokeColor(activeColor);
            cardPublic.setCardBackgroundColor(activeBg);
            ivGlobe.setColorFilter(activeColor);
            tvPublicLabel.setTextColor(activeColor);
            ivCheckPublic.setVisibility(View.VISIBLE);

            // Private Inactive
            cardPrivate.setStrokeWidth(0);
            cardPrivate.setCardBackgroundColor(normalBg);
            ivLock.setColorFilter(inactiveColor);
            tvPrivateLabel.setTextColor(inactiveColor);
            ivCheckPrivate.setVisibility(View.GONE);
        } else {
            // Private Active
            cardPrivate.setStrokeWidth(dpToPx(2));
            cardPrivate.setStrokeColor(activeColor);
            cardPrivate.setCardBackgroundColor(activeBg);
            ivLock.setColorFilter(activeColor);
            tvPrivateLabel.setTextColor(activeColor);
            ivCheckPrivate.setVisibility(View.VISIBLE);

            // Public Inactive
            cardPublic.setStrokeWidth(0);
            cardPublic.setCardBackgroundColor(normalBg);
            ivGlobe.setColorFilter(inactiveColor);
            tvPublicLabel.setTextColor(inactiveColor);
            ivCheckPublic.setVisibility(View.GONE);
        }
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("profile_public", isPublicSelected);
        if (swShowEmail != null) editor.putBoolean("show_email", swShowEmail.isChecked());
        if (swShowActivity != null) editor.putBoolean("show_activity", swShowActivity.isChecked());
        if (swAllowForking != null) editor.putBoolean("allow_forking", swAllowForking.isChecked());
        editor.apply();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
