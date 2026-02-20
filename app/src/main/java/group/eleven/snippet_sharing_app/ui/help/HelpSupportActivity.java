package group.eleven.snippet_sharing_app.ui.help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.databinding.ActivityHelpSupportBinding;

/**
 * HelpSupportActivity - Help and support options for users
 */
public class HelpSupportActivity extends AppCompatActivity {

    private ActivityHelpSupportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHelpSupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupStatusBar();
        setupToolbar();
        setupClickListeners();
    }

    private void setupStatusBar() {
        android.view.Window window = getWindow();
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(R.attr.surfaceColor, typedValue, true);
        int statusBarColor;
        if (typedValue.resourceId != 0) {
            statusBarColor = androidx.core.content.ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            statusBarColor = typedValue.data;
        }
        window.setStatusBarColor(statusBarColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            boolean isLightBackground = isColorLight(statusBarColor);
            controller.setAppearanceLightStatusBars(isLightBackground);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color)
                + 0.587 * android.graphics.Color.green(color)
                + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupClickListeners() {
        binding.cardFaq.setOnClickListener(v -> {
            // TODO: Navigate to FAQ page or open web link
        });

        binding.cardContactUs.setOnClickListener(v -> {
            // Open email client
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@snippetg11.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Snippet G11");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        binding.cardReportBug.setOnClickListener(v -> {
            // TODO: Open bug report form
        });

        binding.cardFeatureRequest.setOnClickListener(v -> {
            // TODO: Open feature request form
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
