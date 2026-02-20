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
import group.eleven.snippet_sharing_app.data.model.Notification;
import group.eleven.snippet_sharing_app.data.model.NotificationItem;
import group.eleven.snippet_sharing_app.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for notification-related API calls
 */
public class NotificationRepository {

    private final ApiService apiService;

    public NotificationRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
    }

    /**
     * Get notifications as NotificationItem list for UI
     */
    public LiveData<Resource<List<NotificationItem>>> getNotifications(int perPage) {
        MutableLiveData<Resource<List<NotificationItem>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> params = new HashMap<>();
        params.put("per_page", String.valueOf(perPage));

        apiService.getNotifications(params).enqueue(new Callback<ApiResponse<List<Notification>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Notification>>> call, Response<ApiResponse<List<Notification>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Notification> notifications = response.body().getData();
                    List<NotificationItem> items = new ArrayList<>();

                    if (notifications != null) {
                        for (Notification n : notifications) {
                            items.add(mapToNotificationItem(n));
                        }
                    }

                    result.setValue(Resource.success(items));
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load notifications";
                    result.setValue(Resource.error(message, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Notification>>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Get unread notification count
     */
    public LiveData<Resource<Integer>> getUnreadCount() {
        MutableLiveData<Resource<Integer>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getUnreadNotificationCount().enqueue(new Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(response.body().getData()));
                } else {
                    result.setValue(Resource.error("Failed to get unread count", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Mark a notification as read
     */
    public LiveData<Resource<Boolean>> markAsRead(String notificationId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.markNotificationAsRead(notificationId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to mark as read", null));
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
     * Mark all notifications as read
     */
    public LiveData<Resource<Boolean>> markAllAsRead() {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.markAllNotificationsAsRead().enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to mark all as read", null));
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
     * Delete a notification
     */
    public LiveData<Resource<Boolean>> deleteNotification(String notificationId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteNotification(notificationId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to delete notification", null));
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
     * Map API Notification model to UI NotificationItem model
     */
    private NotificationItem mapToNotificationItem(Notification n) {
        String actorName = "User";
        String actorAvatar = null;

        if (n.getActor() != null) {
            actorName = n.getActor().getFullName() != null
                    ? n.getActor().getFullName()
                    : n.getActor().getUsername();
            actorAvatar = n.getActor().getAvatarUrl();
        }

        NotificationItem item = new NotificationItem(
                n.getId(),
                n.getType() != null ? n.getType() : "notification",
                n.getTitle() != null ? n.getTitle() : "",
                n.getMessage() != null ? n.getMessage() : "",
                formatTimestamp(n.getCreatedAt()),
                n.isRead(),
                actorName
        );

        item.setActorAvatar(actorAvatar);
        item.setTargetId(n.getRelatedResourceId());
        item.setTargetName(n.getRelatedResourceType());

        return item;
    }

    /**
     * Format timestamp for display
     */
    private String formatTimestamp(String timestamp) {
        if (timestamp == null) return "";
        // Simple format - in production use proper date parsing
        return "recently";
    }
}
