package group.eleven.snippet_sharing_app.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.api.ApiService;
import group.eleven.snippet_sharing_app.data.model.ActivityFeedItem;
import group.eleven.snippet_sharing_app.data.model.ApiResponse;
import group.eleven.snippet_sharing_app.data.model.ErrorResponse;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamInvitation;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.model.TeamSnippet;
import group.eleven.snippet_sharing_app.utils.SessionManager; // Import SessionManager

import static group.eleven.snippet_sharing_app.data.repository.AuthRepository.Resource; // Explicitly import Resource

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for team management operations
 */
public class TeamRepository {
    private final ApiService apiService;
    private final Gson gson;
    private final SessionManager sessionManager; // Add SessionManager

    public TeamRepository(Context context) {
        this.apiService = ApiClient.getApiService(context);
        this.gson = new Gson();
        this.sessionManager = new SessionManager(context); // Initialize SessionManager
    }

    /**
     * Get a list of teams the current user is a member of.
     */
    public LiveData<AuthRepository.Resource<List<Team>>> getMyTeams() {
        MutableLiveData<AuthRepository.Resource<List<Team>>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.getMyTeams().enqueue(new Callback<ApiResponse<List<Team>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Team>>> call, Response<ApiResponse<List<Team>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Team>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Team>>> call, Throwable t) {
                // Fallback to mock data for testing
                List<Team> mockTeams = group.eleven.snippet_sharing_app.data.MockDataProvider.getMockTeams(6);
                result.setValue(AuthRepository.Resource.success(mockTeams));
            }
        });
        return result;
    }

    /**
     * Create a new team.
     */
    public LiveData<AuthRepository.Resource<Team>> createTeam(Map<String, String> teamData) {
        MutableLiveData<AuthRepository.Resource<Team>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.createTeam(teamData).enqueue(new Callback<ApiResponse<Team>>() {
            @Override
            public void onResponse(Call<ApiResponse<Team>> call, Response<ApiResponse<Team>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Team> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Team>> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Get details for a specific team.
     */
    public LiveData<AuthRepository.Resource<Team>> getTeamDetails(String teamId) {
        MutableLiveData<AuthRepository.Resource<Team>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.getTeamDetails(teamId).enqueue(new Callback<ApiResponse<Team>>() {
            @Override
            public void onResponse(Call<ApiResponse<Team>> call, Response<ApiResponse<Team>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Team> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Team>> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Update details for a specific team.
     */
    public LiveData<AuthRepository.Resource<Team>> updateTeam(String teamId, Map<String, String> teamData) {
        MutableLiveData<AuthRepository.Resource<Team>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.updateTeam(teamId, teamData).enqueue(new Callback<ApiResponse<Team>>() {
            @Override
            public void onResponse(Call<ApiResponse<Team>> call, Response<ApiResponse<Team>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Team> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Team>> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Delete a specific team.
     */
    public LiveData<AuthRepository.Resource<MessageResponse>> deleteTeam(String teamId) {
        MutableLiveData<AuthRepository.Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.deleteTeam(teamId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    if (messageResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(messageResponse));
                    } else {
                        result.setValue(AuthRepository.Resource.error(messageResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Invite a member to a specific team.
     */
    public LiveData<AuthRepository.Resource<MessageResponse>> inviteTeamMember(String teamId, Map<String, String> inviteData) {
        MutableLiveData<AuthRepository.Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.inviteTeamMember(teamId, inviteData).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    if (messageResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(messageResponse));
                    } else {
                        result.setValue(AuthRepository.Resource.error(messageResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Remove a member from a specific team.
     */
    public LiveData<AuthRepository.Resource<MessageResponse>> removeTeamMember(String teamId, String memberId) {
        MutableLiveData<AuthRepository.Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.removeTeamMember(teamId, memberId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    if (messageResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(messageResponse));
                    } else {
                        result.setValue(AuthRepository.Resource.error(messageResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Update a member's role in a specific team.
     */
    public LiveData<AuthRepository.Resource<MessageResponse>> updateTeamMemberRole(String teamId, String memberId, Map<String, String> roleData) {
        MutableLiveData<AuthRepository.Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.updateTeamMemberRole(teamId, memberId, roleData).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    if (messageResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(messageResponse));
                    } else {
                        result.setValue(AuthRepository.Resource.error(messageResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Get a list of members for a specific team.
     */
    public LiveData<AuthRepository.Resource<List<TeamMember>>> getTeamMembers(String teamId) {
        MutableLiveData<AuthRepository.Resource<List<TeamMember>>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.getTeamMembers(teamId).enqueue(new Callback<ApiResponse<List<TeamMember>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TeamMember>>> call, Response<ApiResponse<List<TeamMember>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TeamMember>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TeamMember>>> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Get a list of pending invitations for the current user.
     */
    public LiveData<AuthRepository.Resource<List<TeamInvitation>>> getMyTeamInvitations() {
        MutableLiveData<AuthRepository.Resource<List<TeamInvitation>>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.getMyTeamInvitations().enqueue(new Callback<ApiResponse<List<TeamInvitation>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TeamInvitation>>> call, Response<ApiResponse<List<TeamInvitation>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TeamInvitation>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TeamInvitation>>> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Respond to a team invitation (accept/reject).
     */
    public LiveData<AuthRepository.Resource<MessageResponse>> respondToTeamInvitation(String invitationId, Map<String, String> responseData) {
        MutableLiveData<AuthRepository.Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.respondToTeamInvitation(invitationId, responseData).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    if (messageResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(messageResponse));
                    } else {
                        result.setValue(AuthRepository.Resource.error(messageResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Get a list of snippets for a specific team.
     */
    public LiveData<AuthRepository.Resource<List<TeamSnippet>>> getTeamSnippets(String teamId, Map<String, String> filters) {
        MutableLiveData<AuthRepository.Resource<List<TeamSnippet>>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.getTeamSnippets(teamId, filters).enqueue(new Callback<ApiResponse<List<TeamSnippet>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TeamSnippet>>> call, Response<ApiResponse<List<TeamSnippet>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TeamSnippet>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TeamSnippet>>> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Create a new snippet for a specific team.
     */
    public LiveData<AuthRepository.Resource<TeamSnippet>> createTeamSnippet(String teamId, Map<String, String> snippetData) {
        MutableLiveData<AuthRepository.Resource<TeamSnippet>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.createTeamSnippet(teamId, snippetData).enqueue(new Callback<ApiResponse<TeamSnippet>>() {
            @Override
            public void onResponse(Call<ApiResponse<TeamSnippet>> call, Response<ApiResponse<TeamSnippet>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TeamSnippet> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TeamSnippet>> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Transfer ownership of a team.
     */
    public LiveData<AuthRepository.Resource<MessageResponse>> transferTeamOwnership(String teamId, Map<String, String> newOwnerData) {
        MutableLiveData<AuthRepository.Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.transferTeamOwnership(teamId, newOwnerData).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    if (messageResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(messageResponse));
                    } else {
                        result.setValue(AuthRepository.Resource.error(messageResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Current user leaves a specific team.
     */
    public LiveData<AuthRepository.Resource<MessageResponse>> leaveTeam(String teamId) {
        MutableLiveData<AuthRepository.Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        String currentUserId = sessionManager.getCurrentUser() != null ? sessionManager.getCurrentUser().getId() : null;

        if (currentUserId == null) {
            result.setValue(AuthRepository.Resource.error("User not logged in. Cannot leave team."));
            return result;
        }

        apiService.removeTeamMember(teamId, currentUserId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    if (messageResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(messageResponse));
                    } else {
                        result.setValue(AuthRepository.Resource.error(messageResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }

    /**
     * Get team activity feed.
     */
    public LiveData<AuthRepository.Resource<List<ActivityFeedItem>>> getTeamActivity(String teamId) {
        MutableLiveData<AuthRepository.Resource<List<ActivityFeedItem>>> result = new MutableLiveData<>();
        result.setValue(AuthRepository.Resource.loading());

        apiService.getTeamActivity(teamId).enqueue(new Callback<ApiResponse<List<ActivityFeedItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ActivityFeedItem>>> call, Response<ApiResponse<List<ActivityFeedItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ActivityFeedItem>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(AuthRepository.Resource.success(apiResponse.getData()));
                    } else {
                        result.setValue(AuthRepository.Resource.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(AuthRepository.Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ActivityFeedItem>>> call, Throwable t) {
                result.setValue(AuthRepository.Resource.error(getNetworkError(t)));
            }
        });
        return result;
    }


    /**
     * Helper method to parse error response
     */
    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                ErrorResponse errorResponse = gson.fromJson(
                        response.errorBody().string(),
                        ErrorResponse.class
                );
                return errorResponse.getFirstError();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "An error occurred. Please try again.";
    }

    /**
     * Helper method to get network error message
     */
    private String getNetworkError(Throwable t) {
        if (t instanceof IOException) {
            return "Network error. Please check your connection.";
        }
        return "An error occurred. Please try again.";
    }
}
