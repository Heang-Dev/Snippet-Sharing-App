package group.eleven.snippet_sharing_app.utils;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.Executors;

import group.eleven.snippet_sharing_app.BuildConfig;

/**
 * Helper class for Google Sign-In using Credential Manager API.
 * Uses GetSignInWithGoogleOption (shows Google's Sign-In UI) as primary,
 * falls back to GetGoogleIdOption for returning users.
 */
public class GoogleSignInHelper {

    private static final String TAG = "GoogleSignInHelper";
    private final CredentialManager credentialManager;
    private final Activity activity;

    public interface GoogleSignInCallback {
        void onSuccess(String idToken);
        void onError(String errorMessage);
    }

    public GoogleSignInHelper(Activity activity) {
        this.activity = activity;
        this.credentialManager = CredentialManager.create(activity);
    }

    /**
     * Launch Google Sign-In flow.
     * First tries one-tap (returning users), then falls back to full Sign-In UI.
     */
    public void signIn(GoogleSignInCallback callback) {
        // Try one-tap for returning users first
        GetGoogleIdOption oneTapOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest oneTapRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(oneTapOption)
                .build();

        credentialManager.getCredentialAsync(
                activity,
                oneTapRequest,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignInResult(result, callback);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.d(TAG, "One-tap failed, launching full Sign-In UI", e);
                        // Fall back to full Google Sign-In button/bottom sheet
                        launchSignInWithGoogle(callback);
                    }
                }
        );
    }

    /**
     * Launch the full "Sign in with Google" bottom sheet UI.
     * This is more reliable — only requires Web Client ID, no Android OAuth client needed.
     */
    private void launchSignInWithGoogle(GoogleSignInCallback callback) {
        GetSignInWithGoogleOption signInOption = new GetSignInWithGoogleOption.Builder(
                BuildConfig.GOOGLE_WEB_CLIENT_ID
        ).build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build();

        credentialManager.getCredentialAsync(
                activity,
                request,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignInResult(result, callback);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "Google Sign-In failed", e);
                        String message;
                        if (e instanceof NoCredentialException) {
                            message = "No Google account found. Please sign in to a Google account on this device first.";
                        } else {
                            message = "Google Sign-In failed: " + e.getMessage();
                        }
                        callback.onError(message);
                    }
                }
        );
    }

    private void handleSignInResult(GetCredentialResponse response, GoogleSignInCallback callback) {
        Credential credential = response.getCredential();

        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                try {
                    GoogleIdTokenCredential googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(customCredential.getData());
                    String idToken = googleIdTokenCredential.getIdToken();
                    Log.d(TAG, "Google ID token obtained successfully");
                    callback.onSuccess(idToken);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse Google ID token", e);
                    callback.onError("Failed to parse Google credential");
                }
            } else {
                callback.onError("Unexpected credential type: " + customCredential.getType());
            }
        } else {
            callback.onError("Unexpected credential type");
        }
    }
}
