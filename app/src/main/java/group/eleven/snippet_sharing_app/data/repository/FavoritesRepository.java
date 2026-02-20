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
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for favorites/starred snippets API calls
 */
public class FavoritesRepository {

    private final ApiService apiService;

    public FavoritesRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    /**
     * Get user's favorite/starred snippets
     */
    public LiveData<Resource<List<SnippetCard>>> getFavoriteSnippets(int perPage) {
        MutableLiveData<Resource<List<SnippetCard>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.getFavoriteSnippets(params).enqueue(new Callback<ApiResponse<List<Snippet>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Snippet>>> call, Response<ApiResponse<List<Snippet>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Snippet> snippets = response.body().getData();
                    List<SnippetCard> cards = new ArrayList<>();

                    if (snippets != null) {
                        for (Snippet snippet : snippets) {
                            cards.add(snippet.toSnippetCard());
                        }
                    }

                    result.setValue(Resource.success(cards));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load favorites";
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
     * Add a snippet to favorites
     */
    public LiveData<Resource<Boolean>> addToFavorites(String snippetId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.addToFavorites(snippetId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to add to favorites", null));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Remove a snippet from favorites
     */
    public LiveData<Resource<Boolean>> removeFromFavorites(String snippetId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.removeFromFavorites(snippetId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to remove from favorites", null));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
