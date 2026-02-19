package group.eleven.snippet_sharing_app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * ThemeManager - Centralized theme management for the Snippet Sharing app.
 *
 * Handles:
 * - Theme mode switching (dark/light/system)
 * - Status bar and navigation bar styling
 * - Theme preferences persistence
 */
public class ThemeManager {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";

    // Theme mode constants
    public static final int MODE_SYSTEM = 0;  // Follow system theme
    public static final int MODE_DARK = 1;    // Always dark
    public static final int MODE_LIGHT = 2;   // Always light (not used in this app)

    private final Context context;
    private final SharedPreferences prefs;

    private static ThemeManager instance;

    private ThemeManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get singleton instance
     */
    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        return instance;
    }

    /**
     * Initialize theme on app startup.
     * Call this in Application.onCreate() or before setContentView()
     */
    public void applyTheme() {
        int mode = getThemeMode();
        switch (mode) {
            case MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Set theme mode and apply immediately
     */
    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
        applyTheme();
    }

    /**
     * Get current theme mode
     */
    public int getThemeMode() {
        // Default to dark mode for this app
        return prefs.getInt(KEY_THEME_MODE, MODE_DARK);
    }

    /**
     * Check if current theme is dark
     */
    public boolean isDarkMode() {
        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Apply edge-to-edge styling to an activity.
     * Makes status bar and navigation bar transparent and draws behind them.
     */
    public static void applyEdgeToEdge(Activity activity) {
        Window window = activity.getWindow();

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false);

        // Make system bars transparent
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        // Set appropriate icon colors based on theme
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            // For dark theme, use light (white) icons
            controller.setAppearanceLightStatusBars(false);
            controller.setAppearanceLightNavigationBars(false);
        }
    }

    /**
     * Apply standard dark status bar styling (non-edge-to-edge)
     */
    public static void applyDarkStatusBar(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusBarColor);

        // Light icons on dark background
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }
    }

    /**
     * Apply standard dark navigation bar styling
     */
    public static void applyDarkNavigationBar(Activity activity, int navBarColor) {
        Window window = activity.getWindow();
        window.setNavigationBarColor(navBarColor);

        // Light icons on dark background
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightNavigationBars(false);
        }
    }

    /**
     * Get color from theme attribute
     */
    public static int getThemeColor(Context context, int attrResId) {
        android.util.TypedValue typedValue = new android.util.TypedValue();
        context.getTheme().resolveAttribute(attrResId, typedValue, true);
        return typedValue.data;
    }

    /**
     * Get drawable from theme attribute
     */
    public static int getThemeDrawable(Context context, int attrResId) {
        android.util.TypedValue typedValue = new android.util.TypedValue();
        context.getTheme().resolveAttribute(attrResId, typedValue, true);
        return typedValue.resourceId;
    }

    // ============ THEME COLORS HELPER METHODS ============

    /**
     * Get primary accent color
     */
    public int getAccentColor() {
        return getThemeColor(context, group.eleven.snippet_sharing_app.R.attr.accentColor);
    }

    /**
     * Get primary text color
     */
    public int getTextPrimaryColor() {
        return getThemeColor(context, group.eleven.snippet_sharing_app.R.attr.textPrimaryColor);
    }

    /**
     * Get secondary text color
     */
    public int getTextSecondaryColor() {
        return getThemeColor(context, group.eleven.snippet_sharing_app.R.attr.textSecondaryColor);
    }

    /**
     * Get app background color
     */
    public int getBackgroundColor() {
        return getThemeColor(context, group.eleven.snippet_sharing_app.R.attr.appBackgroundColor);
    }

    /**
     * Get surface color
     */
    public int getSurfaceColor() {
        return getThemeColor(context, group.eleven.snippet_sharing_app.R.attr.surfaceColor);
    }

    /**
     * Get card surface color
     */
    public int getCardSurfaceColor() {
        return getThemeColor(context, group.eleven.snippet_sharing_app.R.attr.cardSurfaceColor);
    }

    /**
     * Get error color
     */
    public int getErrorColor() {
        return getThemeColor(context, group.eleven.snippet_sharing_app.R.attr.errorColor);
    }

    /**
     * Get success color
     */
    public int getSuccessColor() {
        return getThemeColor(context, group.eleven.snippet_sharing_app.R.attr.successColor);
    }
}
