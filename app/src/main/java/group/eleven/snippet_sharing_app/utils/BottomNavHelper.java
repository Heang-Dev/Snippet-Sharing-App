package group.eleven.snippet_sharing_app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.User;

/**
 * Helper utility for BottomNavigationView customization.
 * Provides functionality to display user's real profile avatar in the profile tab.
 */
public class BottomNavHelper {

    private static final String TAG = "BottomNavHelper";
    private static final int AVATAR_SIZE_DP = 24; // Size in dp for bottom nav icon

    /**
     * Convert dp to pixels based on device density.
     */
    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    /**
     * Setup the profile avatar in the bottom navigation.
     * Loads the user's avatar image and replaces the default profile icon.
     *
     * @param context        Application context
     * @param bottomNav      BottomNavigationView instance
     * @param sessionManager SessionManager to get user data
     */
    public static void setupProfileAvatar(Context context,
                                          BottomNavigationView bottomNav,
                                          SessionManager sessionManager) {
        if (context == null || bottomNav == null || sessionManager == null) {
            Log.w(TAG, "setupProfileAvatar: Null parameter provided");
            return;
        }

        User user = sessionManager.getUser();
        String avatarUrl = user != null ? user.getAvatarUrl() : null;

        Log.d(TAG, "setupProfileAvatar: User=" + (user != null ? user.getUsername() : "null") +
                ", avatarUrl=" + avatarUrl);

        MenuItem profileItem = bottomNav.getMenu().findItem(R.id.nav_profile);
        if (profileItem == null) {
            Log.w(TAG, "setupProfileAvatar: nav_profile menu item not found");
            return;
        }

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            loadAvatarIntoMenuItem(context, avatarUrl, profileItem);
        } else {
            Log.d(TAG, "setupProfileAvatar: No avatar URL, using default icon");
        }
    }

    /**
     * Load avatar image into menu item using Glide.
     */
    private static void loadAvatarIntoMenuItem(Context context, String avatarUrl, MenuItem menuItem) {
        // Use application context to avoid lifecycle issues
        Context appContext = context.getApplicationContext();
        int avatarSizePx = dpToPx(appContext, AVATAR_SIZE_DP);
        Log.d(TAG, "loadAvatarIntoMenuItem: Loading avatar, size=" + avatarSizePx + "px, url=" + avatarUrl);

        Glide.with(appContext)
                .asBitmap()
                .load(avatarUrl)
                .circleCrop()
                .override(avatarSizePx, avatarSizePx)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(new CustomTarget<Bitmap>(avatarSizePx, avatarSizePx) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        Log.d(TAG, "onResourceReady: Avatar loaded successfully, " +
                                "bitmap size=" + resource.getWidth() + "x" + resource.getHeight());
                        Drawable avatarDrawable = new BitmapDrawable(appContext.getResources(), resource);
                        menuItem.setIcon(avatarDrawable);
                        // Disable icon tinting to show the actual avatar colors
                        menuItem.setIconTintList(null);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d(TAG, "onLoadCleared: Load cleared");
                        menuItem.setIcon(R.drawable.ic_person);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Log.e(TAG, "onLoadFailed: Failed to load avatar from " + avatarUrl);
                        menuItem.setIcon(R.drawable.ic_person);
                    }
                });
    }

    /**
     * Refresh the profile avatar. Call this when user updates their profile picture.
     */
    public static void refreshProfileAvatar(Context context,
                                            BottomNavigationView bottomNav,
                                            SessionManager sessionManager) {
        setupProfileAvatar(context, bottomNav, sessionManager);
    }
}
