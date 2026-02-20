package group.eleven.snippet_sharing_app.ui.shared;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.databinding.ActivitySharedWithMeBinding;

/**
 * SharedWithMeActivity - Display snippets shared with the current user
 */
public class SharedWithMeActivity extends AppCompatActivity {

    private ActivitySharedWithMeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySharedWithMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupStatusBar();
        setupToolbar();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
