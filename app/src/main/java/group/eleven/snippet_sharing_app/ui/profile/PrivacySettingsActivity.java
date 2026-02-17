package group.eleven.snippet_sharing_app.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import group.eleven.snippet_sharing_app.R;

public class PrivacySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Handle Back Navigation
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        // Handle Save Button
        findViewById(R.id.btnSaveSettings).setOnClickListener(v -> {
            Toast.makeText(this, "Privacy settings updated", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Setup Selection Logic for Public/Private cards
        View cardPublic = findViewById(R.id.cardPublic);
        View cardPrivate = findViewById(R.id.cardPrivate);

        if (cardPublic != null) {
            cardPublic.setOnClickListener(v -> {
                updateSelection(true);
            });
        }

        if (cardPrivate != null) {
            cardPrivate.setOnClickListener(v -> {
                updateSelection(false);
            });
        }
    }

    private void updateSelection(boolean isPublic) {
        // Here you would normally update the UI state/borders
        Toast.makeText(this, "Visibility set to " + (isPublic ? "Public" : "Private"), Toast.LENGTH_SHORT).show();
    }
}
