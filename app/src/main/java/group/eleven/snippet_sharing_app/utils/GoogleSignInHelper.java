package group.eleven.snippet_sharing_app.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.Executors;

import group.eleven.snippet_sharing_app.BuildConfig;

/**
 * Helper class for Google Sign-In using Credential Manager API
 */
public class GoogleSignInHelper {

    private static final String TAG = "GoogleSignInHelper";
    private final CredentialManager credentialManager;
    private final Context context;

    public interface GoogleSignInCallback {
        void onSuccess(String idToken);
        void onError(String errorMessage);
    }

    public GoogleSignInHelper(Context context) {
        this.context = context;
        this.credentialManager = CredentialManager.create(context);
    }

    /**
     * Launch Google Sign-In flow
     */
    public void signIn(GoogleSignInCallback callback) {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                context,
                request,
                null, // CancellationSignal
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignInResult(result, callback);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "Google Sign-In failed", e);
                        callback.onError("Google Sign-In failed: " + e.getMessage());
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
                callback.onError("Unexpected credential type");
            }
        } else {
            callback.onError("Unexpected credential type");
        }
    }
}
