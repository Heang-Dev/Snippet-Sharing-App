package group.eleven.snippet_sharing_app.ui.profile;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import group.eleven.snippet_sharing_app.R;

public class AccountSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Toolbar back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Save Changes button
        if (findViewById(R.id.btnSaveChanges) != null) {
            findViewById(R.id.btnSaveChanges).setOnClickListener(v -> {
                Toast.makeText(this, "Account settings saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        }

        // Disconnect Google
        if (findViewById(R.id.btnDisconnectGoogle) != null) {
            findViewById(R.id.btnDisconnectGoogle).setOnClickListener(v -> 
                Toast.makeText(this, "Google account disconnected", Toast.LENGTH_SHORT).show()
            );
        }

        // Revoke All Sessions
        if (findViewById(R.id.tvRevokeAll) != null) {
            findViewById(R.id.tvRevokeAll).setOnClickListener(v -> 
                Toast.makeText(this, "All sessions revoked", Toast.LENGTH_SHORT).show()
            );
        }

        // Delete Account
        if (findViewById(R.id.btnDeleteAccount) != null) {
            findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> 
                Toast.makeText(this, "Account deletion requested", Toast.LENGTH_LONG).show()
            );
        }
    }
}
