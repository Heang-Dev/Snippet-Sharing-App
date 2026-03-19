package group.eleven.snippet_sharing_app.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import group.eleven.snippet_sharing_app.BuildConfig;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.utils.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class for Retrofit API client configuration
 */
public class ApiClient {
    private static final String BASE_URL = BuildConfig.API_BASE_URL;
    private static final String STORAGE_BASE_URL = BuildConfig.STORAGE_BASE_URL;
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    private ApiClient() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Retrofit getClient(Context context) {
        if (retrofit == null) {
            // Logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Session manager for token
            SessionManager sessionManager = new SessionManager(context);

            // OkHttp client with interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Accept", "application/json")
                                .header("Content-Type", "application/json");

                        // Add auth token if available
                        String token = sessionManager.getAuthToken();
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }

                        return chain.proceed(requestBuilder.build());
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Snippet.SnippetLanguage.class, new Snippet.SnippetLanguageDeserializer())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static synchronized ApiService getApiService(Context context) {
        if (apiService == null) {
            apiService = getClient(context).create(ApiService.class);
        }
        return apiService;
    }

    /**
     * Reset the client (useful when token changes)
     */
    public static synchronized void resetClient() {
        retrofit = null;
        apiService = null;
    }

    /**
     * Get the storage base URL for constructing full URLs from relative paths
     */
    public static String getStorageBaseUrl() {
        return STORAGE_BASE_URL;
    }

    /**
     * Convert a relative storage path to a full URL
     * @param relativePath The relative path (e.g., "/storage/avatars/image.jpg")
     * @return The full URL (e.g., "http://10.0.2.2:8000/storage/avatars/image.jpg")
     */
    public static String getFullStorageUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        // If already a full URL, return as-is
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }
        // If it's a local file path, return as-is
        if (relativePath.startsWith("file://")) {
            return relativePath;
        }
        // Remove leading slash if present for proper concatenation
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return STORAGE_BASE_URL + relativePath;
    }
}
