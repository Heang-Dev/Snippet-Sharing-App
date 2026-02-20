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
import group.eleven.snippet_sharing_app.data.model.Category;
import group.eleven.snippet_sharing_app.data.model.Language;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.data.model.Tag;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamsResponse;
import group.eleven.snippet_sharing_app.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for snippet creation related API calls
 */
public class SnippetCreationRepository {

    private final ApiService apiService;

    public SnippetCreationRepository(Context context) {
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
                    result.setValue(Resource.error("Failed to load languages", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Language>>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get all tags
     */
    public LiveData<Resource<List<Tag>>> getTags() {
        MutableLiveData<Resource<List<Tag>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getTags().enqueue(new Callback<ApiResponse<List<Tag>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Tag>>> call, Response<ApiResponse<List<Tag>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load tags", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Tag>>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get popular tags
     */
    public LiveData<Resource<List<Tag>>> getPopularTags() {
        MutableLiveData<Resource<List<Tag>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getPopularTags().enqueue(new Callback<ApiResponse<List<Tag>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Tag>>> call, Response<ApiResponse<List<Tag>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load popular tags", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Tag>>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get all categories (tree structure)
     */
    public LiveData<Resource<List<Category>>> getCategories() {
        MutableLiveData<Resource<List<Category>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getCategoriesTree().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to load categories", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get user's joined teams
     */
    public LiveData<Resource<List<Team>>> getJoinedTeams() {
        MutableLiveData<Resource<List<Team>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getMyTeams().enqueue(new Callback<ApiResponse<TeamsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TeamsResponse>> call, Response<ApiResponse<TeamsResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    TeamsResponse teamsData = response.body().getData();
                    List<Team> teams = teamsData != null ? teamsData.getAllTeams() : new java.util.ArrayList<>();
                    result.setValue(Resource.success(teams));
                } else {
                    result.setValue(Resource.error("Failed to load teams", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TeamsResponse>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Create a new snippet
     */
    public LiveData<Resource<Snippet>> createSnippet(
            String title,
            String content,
            String languageId,
            String visibility,
            String description,
            List<String> tags,
            String categoryId,
            String teamId
    ) {
        MutableLiveData<Resource<Snippet>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("content", content);
        data.put("language_id", languageId);
        data.put("visibility", visibility.toLowerCase());

        if (description != null && !description.isEmpty()) {
            data.put("description", description);
        }

        if (tags != null && !tags.isEmpty()) {
            data.put("tags", tags);
        }

        if (categoryId != null && !categoryId.isEmpty()) {
            data.put("category_id", categoryId);
        }

        if (teamId != null && !teamId.isEmpty() && visibility.equalsIgnoreCase("team")) {
            data.put("team_id", teamId);
        }

        apiService.createSnippet(data).enqueue(new Callback<ApiResponse<Snippet>>() {
            @Override
            public void onResponse(Call<ApiResponse<Snippet>> call, Response<ApiResponse<Snippet>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String errorMsg = "Failed to create snippet";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    result.setValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Snippet>> call, Throwable t) {
                result.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return result;
    }
}
