package group.eleven.snippet_sharing_app;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Application class for global app initialization
 */
public class SnippetApp extends Application {

    private static final String TAG = "SnippetApp";
    private Thread.UncaughtExceptionHandler defaultHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");

        // Save the default handler
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // Set up global exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "===========================================");
            Log.e(TAG, "UNCAUGHT EXCEPTION!");
            Log.e(TAG, "Thread: " + thread.getName());
            Log.e(TAG, "Exception: " + throwable.getClass().getSimpleName());
            Log.e(TAG, "Message: " + throwable.getMessage());
            Log.e(TAG, "Stack trace:", throwable);
            Log.e(TAG, "===========================================");

            // Print full stack trace
            throwable.printStackTrace();

            // Try to show a toast (might not work if not on main thread)
            try {
                android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                handler.post(() -> {
                    Toast.makeText(getApplicationContext(),
                            "CRASH: " + throwable.getClass().getSimpleName() + " - " + throwable.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
                // Give time for toast to show
                Thread.sleep(2000);
            } catch (Exception ignored) {}

            // Call the default handler (will crash the app but show in logcat)
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        });

        // Clear old session data for fresh start
        clearOldSessionData();

        Log.d(TAG, "Application initialized successfully");
    }

    /**
     * Clear old session data that might cause issues
     */
    private void clearOldSessionData() {
        try {
            SharedPreferences migrationPrefs = getSharedPreferences("migration_prefs", MODE_PRIVATE);
            // Use v2 to force a new clean
            boolean hasMigrated = migrationPrefs.getBoolean("cleared_session_v2", false);

            if (!hasMigrated) {
                Log.d(TAG, "Clearing old session data...");

                // Clear all session data
                SharedPreferences sessionPrefs = getSharedPreferences("SnippetAppSession", MODE_PRIVATE);
                sessionPrefs.edit().clear().commit();

                // Delete old encrypted preference files if they exist
                File sharedPrefsDir = new File(getApplicationInfo().dataDir, "shared_prefs");
                if (sharedPrefsDir.exists()) {
                    File[] files = sharedPrefsDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            String name = file.getName();
                            if (name.contains("__androidx_security_crypto")) {
                                Log.d(TAG, "Deleting encrypted pref file: " + name);
                                file.delete();
                            }
                        }
                    }
                }

                // Mark migration as done
                migrationPrefs.edit().putBoolean("cleared_session_v2", true).apply();
                Log.d(TAG, "Old session data cleared successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing old session data", e);
        }
    }
}
