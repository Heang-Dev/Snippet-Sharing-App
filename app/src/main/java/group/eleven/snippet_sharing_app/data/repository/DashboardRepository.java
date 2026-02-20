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
import group.eleven.snippet_sharing_app.data.model.ActivityFeedItem;
import group.eleven.snippet_sharing_app.data.model.ApiResponse;
import group.eleven.snippet_sharing_app.data.model.DashboardStats;
import group.eleven.snippet_sharing_app.data.model.FeedActivity;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.Snippet;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for dashboard-related API calls
 */
public class DashboardRepository {

    private final ApiService apiService;

    public DashboardRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    /**
     * Get dashboard statistics (snippets count, favorites, followers)
     */
    public LiveData<Resource<DashboardStats>> getDashboardStats() {
        MutableLiveData<Resource<DashboardStats>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getDashboardStats().enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call, Response<ApiResponse<DashboardStats>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load stats";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get personalized activity feed
     */
    public LiveData<Resource<List<ActivityFeedItem>>> getActivityFeed(int perPage) {
        MutableLiveData<Resource<List<ActivityFeedItem>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.getActivityFeed(params).enqueue(new Callback<ApiResponse<List<FeedActivity>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FeedActivity>>> call, Response<ApiResponse<List<FeedActivity>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<FeedActivity> activities = response.body().getData();
                    List<ActivityFeedItem> items = new ArrayList<>();

                    if (activities != null) {
                        for (FeedActivity activity : activities) {
                            items.add(activity.toActivityFeedItem());
                        }
                    }

                    result.setValue(Resource.success(items));
                } else {
                    // If personalized feed fails (e.g., not following anyone), try public feed
                    getPublicFeedFallback(result, perPage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FeedActivity>>> call, Throwable t) {
                // On network error, try public feed
                getPublicFeedFallback(result, perPage);
            }
        });

        return result;
    }

    private void getPublicFeedFallback(MutableLiveData<Resource<List<ActivityFeedItem>>> result, int perPage) {
        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.getPublicFeed(params).enqueue(new Callback<ApiResponse<List<FeedActivity>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FeedActivity>>> call, Response<ApiResponse<List<FeedActivity>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<FeedActivity> activities = response.body().getData();
                    List<ActivityFeedItem> items = new ArrayList<>();

                    if (activities != null) {
                        for (FeedActivity activity : activities) {
                            items.add(activity.toActivityFeedItem());
                        }
                    }

                    result.setValue(Resource.success(items));
                } else {
                    result.setValue(Resource.error("Failed to load activity feed", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FeedActivity>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }

    /**
     * Get user's recent snippets
     */
    public LiveData<Resource<List<SnippetCard>>> getRecentSnippets(int perPage) {
        MutableLiveData<Resource<List<SnippetCard>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));
        params.put("sort_by", "updated_at");
        params.put("sort_order", "desc");

        apiService.getMySnippets(params).enqueue(new Callback<ApiResponse<List<Snippet>>>() {
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
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load snippets";
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
     * Get public snippets from all users for social feed
     */
    public LiveData<Resource<List<SnippetCard>>> getPublicSnippets(int perPage) {
        MutableLiveData<Resource<List<SnippetCard>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));
        params.put("sort_by", "created_at");
        params.put("sort_order", "desc");

        apiService.getPublicSnippets(params).enqueue(new Callback<ApiResponse<List<Snippet>>>() {
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
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load public snippets";
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
     * Get public activity feed (trending/recent public activities)
     */
    public LiveData<Resource<List<ActivityFeedItem>>> getPublicFeed(int perPage) {
        MutableLiveData<Resource<List<ActivityFeedItem>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.getPublicFeed(params).enqueue(new Callback<ApiResponse<List<FeedActivity>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FeedActivity>>> call, Response<ApiResponse<List<FeedActivity>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<FeedActivity> activities = response.body().getData();
                    List<ActivityFeedItem> items = new ArrayList<>();

                    if (activities != null) {
                        for (FeedActivity activity : activities) {
                            items.add(activity.toActivityFeedItem());
                        }
                    }

                    result.setValue(Resource.success(items));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load public feed";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FeedActivity>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get trending/public snippets for discovery
     */
    public LiveData<Resource<List<SnippetCard>>> getTrendingSnippets(int limit) {
        MutableLiveData<Resource<List<SnippetCard>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));

        apiService.getTrendingSnippets(params).enqueue(new Callback<ApiResponse<List<Snippet>>>() {
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
                    result.setValue(Resource.error("Failed to load trending snippets", null));
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
     * Toggle like on a snippet (like if not liked, unlike if liked)
     */
    public LiveData<Resource<Boolean>> toggleLike(String snippetId, boolean currentlyLiked) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Call<MessageResponse> call;
        if (currentlyLiked) {
            call = apiService.unlikeSnippet(snippetId);
        } else {
            call = apiService.likeSnippet(snippetId);
        }

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Return the new like state (opposite of current)
                    result.setValue(Resource.success(!currentlyLiked));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to update like";
                    result.setValue(Resource.error(message, null));
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
     * Toggle favorite on a snippet
     */
    public LiveData<Resource<Boolean>> toggleFavorite(String snippetId, boolean currentlyFavorited) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Call<MessageResponse> call;
        if (currentlyFavorited) {
            call = apiService.removeFromFavorites(snippetId);
        } else {
            call = apiService.addToFavorites(snippetId);
        }

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(!currentlyFavorited));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to update favorite";
                    result.setValue(Resource.error(message, null));
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
