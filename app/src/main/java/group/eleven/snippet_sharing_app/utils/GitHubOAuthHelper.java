package group.eleven.snippet_sharing_app.utils;

import android.content.Context;
import android.net.Uri;

import androidx.browser.customtabs.CustomTabsIntent;

import group.eleven.snippet_sharing_app.BuildConfig;

/**
 * Helper class for GitHub OAuth using Custom Chrome Tabs.
 * Flow: App → Chrome Tab (GitHub auth) → Backend callback → Deep link back to app
 */
public class GitHubOAuthHelper {

    private static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String REDIRECT_URI = BuildConfig.API_BASE_URL + "auth/github/callback/mobile";
    private static final String SCOPE = "user:email";

    /**
     * Launch GitHub OAuth flow in a Custom Chrome Tab
     */
    public static void launchGitHubAuth(Context context) {
        String state = generateState();

        Uri authUri = Uri.parse(GITHUB_AUTH_URL)
                .buildUpon()
                .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("scope", SCOPE)
                .appendQueryParameter("state", state)
                .build();

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build();

        customTabsIntent.launchUrl(context, authUri);
    }

    /**
     * Parse the deep link callback to extract the access token
     *
     * @param uri The deep link URI (snippetapp://auth/callback?provider=github&token=...)
     * @return The access token, or null if not found
     */
    public static String parseCallbackToken(Uri uri) {
        if (uri == null) return null;
        return uri.getQueryParameter("token");
    }

    /**
     * Parse the provider from the deep link callback
     */
    public static String parseCallbackProvider(Uri uri) {
        if (uri == null) return null;
        return uri.getQueryParameter("provider");
    }

    /**
     * Parse any error from the deep link callback
     */
    public static String parseCallbackError(Uri uri) {
        if (uri == null) return null;
        return uri.getQueryParameter("error");
    }

    private static String generateState() {
        return java.util.UUID.randomUUID().toString().substring(0, 16);
    }
}
