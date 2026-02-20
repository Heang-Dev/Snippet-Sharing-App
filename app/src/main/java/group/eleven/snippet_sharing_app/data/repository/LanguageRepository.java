package group.eleven.snippet_sharing_app.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.api.ApiService;
import group.eleven.snippet_sharing_app.data.model.ApiResponse;
import group.eleven.snippet_sharing_app.data.model.Language;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;

/**
 * Repository for language-related API calls
 */
public class LanguageRepository {

    private final ApiService apiService;

    public LanguageRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    /**
     * Get all programming languages
     */
    public LiveData<Resource<List<Language>>> getLanguages() {
        MutableLiveData<Resource<List<Language>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getLanguages().enqueue(new Callback<ApiResponse<List<Language>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Language>>> call, Response<ApiResponse<List<Language>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load languages";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Language>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get popular programming languages
     */
    public LiveData<Resource<List<Language>>> getPopularLanguages() {
        MutableLiveData<Resource<List<Language>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getPopularLanguages().enqueue(new Callback<ApiResponse<List<Language>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Language>>> call, Response<ApiResponse<List<Language>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load popular languages";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Language>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get language by slug
     */
    public LiveData<Resource<Language>> getLanguageBySlug(String slug) {
        MutableLiveData<Resource<Language>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getLanguageBySlug(slug).enqueue(new Callback<ApiResponse<Language>>() {
            @Override
            public void onResponse(Call<ApiResponse<Language>> call, Response<ApiResponse<Language>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Language not found", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Language>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get snippets by language
     */
    public LiveData<Resource<List<SnippetCard>>> getSnippetsByLanguage(String languageSlug, int perPage) {
        MutableLiveData<Resource<List<SnippetCard>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.getSnippetsByLanguage(languageSlug, params).enqueue(new Callback<ApiResponse<List<Snippet>>>() {
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
                    result.setValue(Resource.error("Failed to load snippets", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Snippet>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
