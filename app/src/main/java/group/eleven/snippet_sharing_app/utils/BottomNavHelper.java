package group.eleven.snippet_sharing_app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    private static final int AVATAR_SIZE_PX = 48; // 24dp * 2 for density

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
            return;
        }

        User user = sessionManager.getUser();
        String avatarUrl = user != null ? user.getAvatarUrl() : null;

        MenuItem profileItem = bottomNav.getMenu().findItem(R.id.nav_profile);
        if (profileItem == null) {
            return;
        }

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            loadAvatarIntoMenuItem(context, avatarUrl, profileItem);
        }
        // If no avatar URL, keep the default ic_person icon
    }

    /**
     * Load avatar image into menu item using Glide.
     */
    private static void loadAvatarIntoMenuItem(Context context, String avatarUrl, MenuItem menuItem) {
        Glide.with(context)
                .asBitmap()
                .load(avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(new CustomTarget<Bitmap>(AVATAR_SIZE_PX, AVATAR_SIZE_PX) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        Drawable avatarDrawable = new BitmapDrawable(context.getResources(), resource);
                        menuItem.setIcon(avatarDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Keep or restore default icon
                        menuItem.setIcon(R.drawable.ic_person);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        // Keep default icon on error
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
