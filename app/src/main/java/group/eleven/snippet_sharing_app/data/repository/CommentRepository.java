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
import group.eleven.snippet_sharing_app.data.model.Comment;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for comment-related API calls
 */
public class CommentRepository {

    private final ApiService apiService;

    public CommentRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    /**
     * Get comments for a snippet
     */
    public LiveData<Resource<List<Comment>>> getComments(String snippetId, int perPage) {
        MutableLiveData<Resource<List<Comment>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.getSnippetComments(snippetId, params).enqueue(new Callback<ApiResponse<List<Comment>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Comment>>> call, Response<ApiResponse<List<Comment>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load comments";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Comment>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Add a comment to a snippet
     */
    public LiveData<Resource<Comment>> addComment(String snippetId, String content) {
        MutableLiveData<Resource<Comment>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> data = new HashMap<>();
        data.put("content", content);

        apiService.addComment(snippetId, data).enqueue(new Callback<ApiResponse<Comment>>() {
            @Override
            public void onResponse(Call<ApiResponse<Comment>> call, Response<ApiResponse<Comment>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to add comment";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Comment>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Update a comment
     */
    public LiveData<Resource<Comment>> updateComment(String commentId, String content) {
        MutableLiveData<Resource<Comment>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> data = new HashMap<>();
        data.put("content", content);

        apiService.updateComment(commentId, data).enqueue(new Callback<ApiResponse<Comment>>() {
            @Override
            public void onResponse(Call<ApiResponse<Comment>> call, Response<ApiResponse<Comment>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to update comment", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Comment>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Delete a comment
     */
    public LiveData<Resource<Boolean>> deleteComment(String commentId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteComment(commentId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to delete comment", null));
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
