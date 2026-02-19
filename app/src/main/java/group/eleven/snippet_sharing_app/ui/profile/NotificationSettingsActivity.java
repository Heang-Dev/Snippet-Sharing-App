package group.eleven.snippet_sharing_app.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import group.eleven.snippet_sharing_app.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationSettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "NotificationSettings";
    private List<MaterialSwitch> switches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setupNavigation();
        initSwitches();
        loadSettings();
        
        findViewById(R.id.btnSavePreferences).setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(this, "Notification preferences saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void initSwitches() {
        // Collect all switches from the layout automatically
        // In a real app, you'd ID them individually, but let's find them all for now
        // to ensure the toggle logic works everywhere.
        findSwitchesRecursive((android.view.ViewGroup) findViewById(android.R.id.content));
    }

    private void findSwitchesRecursive(android.view.ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            android.view.View child = parent.getChildAt(i);
            if (child instanceof MaterialSwitch) {
                switches.add((MaterialSwitch) child);
            } else if (child instanceof android.view.ViewGroup) {
                findSwitchesRecursive((android.view.ViewGroup) child);
            }
        }
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        for (int i = 0; i < switches.size(); i++) {
            switches.get(i).setChecked(prefs.getBoolean("switch_" + i, true));
        }
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        for (int i = 0; i < switches.size(); i++) {
            editor.putBoolean("switch_" + i, switches.get(i).isChecked());
        }
        editor.apply();
    }
}
