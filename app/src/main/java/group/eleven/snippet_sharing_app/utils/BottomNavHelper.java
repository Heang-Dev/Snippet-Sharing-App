package group.eleven.snippet_sharing_app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
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

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            loadAvatarIntoBottomNav(context, avatarUrl, bottomNav);
        } else {
            Log.d(TAG, "setupProfileAvatar: No avatar URL, using default icon");
        }
    }

    /**
     * Load avatar image into bottom navigation profile item.
     * Uses direct ImageView manipulation to bypass icon tinting.
     */
    private static void loadAvatarIntoBottomNav(Context context, String avatarUrl, BottomNavigationView bottomNav) {
        Context appContext = context.getApplicationContext();
        int avatarSizePx = dpToPx(appContext, AVATAR_SIZE_DP);
        Log.d(TAG, "loadAvatarIntoBottomNav: Loading avatar, size=" + avatarSizePx + "px, url=" + avatarUrl);

        // Find the profile item's ImageView directly
        try {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNav.getChildAt(0);
            if (menuView == null) {
                Log.e(TAG, "loadAvatarIntoBottomNav: MenuView is null");
                return;
            }

            // Find the profile item index (nav_profile is the last item, index 4)
            int profileIndex = -1;
            for (int i = 0; i < bottomNav.getMenu().size(); i++) {
                if (bottomNav.getMenu().getItem(i).getItemId() == R.id.nav_profile) {
                    profileIndex = i;
                    break;
                }
            }

            if (profileIndex == -1) {
                Log.e(TAG, "loadAvatarIntoBottomNav: nav_profile not found in menu");
                return;
            }

            View itemView = menuView.getChildAt(profileIndex);
            if (!(itemView instanceof BottomNavigationItemView)) {
                Log.e(TAG, "loadAvatarIntoBottomNav: Item view is not BottomNavigationItemView");
                return;
            }

            BottomNavigationItemView navItemView = (BottomNavigationItemView) itemView;

            // Find the icon ImageView inside the item view
            ImageView iconView = navItemView.findViewById(com.google.android.material.R.id.navigation_bar_item_icon_view);
            if (iconView == null) {
                Log.e(TAG, "loadAvatarIntoBottomNav: Icon ImageView not found");
                return;
            }

            // Load avatar directly into the ImageView
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
                            Log.d(TAG, "onResourceReady: Avatar loaded successfully");
                            new Handler(Looper.getMainLooper()).post(() -> {
                                BitmapDrawable avatarDrawable = new BitmapDrawable(appContext.getResources(), resource);
                                iconView.setImageDrawable(avatarDrawable);
                                // Clear any color filter/tint on the ImageView
                                iconView.setColorFilter(null);
                                iconView.setImageTintList(null);
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.d(TAG, "onLoadCleared: Load cleared");
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            Log.e(TAG, "onLoadFailed: Failed to load avatar from " + avatarUrl);
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "loadAvatarIntoBottomNav: Error setting avatar", e);
        }
    }

    /**
     * Refresh the profile avatar. Call this when user updates their profile picture.
     */
    public static void refreshProfileAvatar(Context context,
                                            BottomNavigationView bottomNav,
                                            SessionManager sessionManager) {
        setupProfileAvatar(context, bottomNav, sessionManager);
    }

    /**
     * Apply system navigation bar insets as bottom padding to the bottom nav container.
     * This prevents the bottom nav from being hidden behind the system gesture bar.
     *
     * @param bottomNavContainer The LinearLayout wrapping the BottomNavigationView
     */
    public static void applyNavigationBarInsets(View bottomNavContainer) {
        if (bottomNavContainer == null) return;

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavContainer, (v, windowInsets) -> {
            Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), 0);
            return windowInsets;
        });
    }

    /**
     * Apply system navigation bar insets to a FAB's bottom margin.
     * Adds the gesture bar height to the FAB's existing bottom margin so it stays above the bottom nav.
     *
     * @param fab The FloatingActionButton to adjust
     */
    public static void applyFabNavigationBarInsets(View fab) {
        if (fab == null) return;

        ViewCompat.setOnApplyWindowInsetsListener(fab, (v, windowInsets) -> {
            Insets navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            android.view.ViewGroup.MarginLayoutParams params =
                    (android.view.ViewGroup.MarginLayoutParams) v.getLayoutParams();
            int baseMargin = dpToPx(v.getContext(), 96);
            params.bottomMargin = baseMargin;
            v.setLayoutParams(params);
            return windowInsets;
        });
    }
}
