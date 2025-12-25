package group.eleven.snippet_sharing_app.api;

import android.content.Context;

import group.eleven.snippet_sharing_app.BuildConfig;
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

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
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
}
