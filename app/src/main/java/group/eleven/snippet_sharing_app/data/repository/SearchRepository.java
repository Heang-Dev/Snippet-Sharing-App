package group.eleven.snippet_sharing_app.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.api.ApiService;
import group.eleven.snippet_sharing_app.data.model.ApiResponse;
import group.eleven.snippet_sharing_app.data.model.SearchResult;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for search-related API calls
 */
public class SearchRepository {

    private final ApiService apiService;

    public SearchRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    /**
     * Global search for snippets and users
     */
    public LiveData<Resource<SearchResult>> search(String query, int perPage) {
        MutableLiveData<Resource<SearchResult>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.search(query, params).enqueue(new Callback<ApiResponse<SearchResult>>() {
            @Override
            public void onResponse(Call<ApiResponse<SearchResult>> call, Response<ApiResponse<SearchResult>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Search failed";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SearchResult>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Search snippets only
     */
    public LiveData<Resource<List<Snippet>>> searchSnippets(String query, int perPage) {
        MutableLiveData<Resource<List<Snippet>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.searchSnippets(query, params).enqueue(new Callback<ApiResponse<List<Snippet>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Snippet>>> call, Response<ApiResponse<List<Snippet>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Search failed";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Snippet>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Search users only
     */
    public LiveData<Resource<List<User>>> searchUsers(String query, int perPage) {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.searchUsers(query, params).enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Search failed";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get search autocomplete suggestions
     */
    public LiveData<Resource<List<String>>> getAutocomplete(String query) {
        MutableLiveData<Resource<List<String>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.searchAutocomplete(query).enqueue(new Callback<ApiResponse<List<String>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<String>>> call, Response<ApiResponse<List<String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to get suggestions", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<String>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
