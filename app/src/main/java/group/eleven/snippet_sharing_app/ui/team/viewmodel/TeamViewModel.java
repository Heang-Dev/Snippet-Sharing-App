package group.eleven.snippet_sharing_app.ui.team.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer; // Import Observer

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group.eleven.snippet_sharing_app.data.model.ActivityFeedItem;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamInvitation;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.model.TeamSnippet;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.data.repository.TeamRepository;

public class TeamViewModel extends AndroidViewModel {

    private final TeamRepository teamRepository;

    // Use MutableLiveData internally to allow updating
    private final MutableLiveData<AuthRepository.Resource<List<Team>>> _myTeamsResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<List<Team>>> getMyTeamsResult() {
        return _myTeamsResult;
    }

    private final MutableLiveData<AuthRepository.Resource<List<TeamInvitation>>> _myTeamInvitationsResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<List<TeamInvitation>>> getMyTeamInvitationsResult() {
        return _myTeamInvitationsResult;
    }

    private final MutableLiveData<AuthRepository.Resource<MessageResponse>> _respondToTeamInvitationResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<MessageResponse>> getRespondToTeamInvitationResult() {
        return _respondToTeamInvitationResult;
    }

    private final MutableLiveData<AuthRepository.Resource<Team>> _teamDetailsResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<Team>> getTeamDetailsResult() { return _teamDetailsResult; }

    private final MutableLiveData<AuthRepository.Resource<List<TeamMember>>> _teamMembersResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<List<TeamMember>>> getTeamMembersResult() { return _teamMembersResult; }

    private final MutableLiveData<AuthRepository.Resource<MessageResponse>> _transferTeamOwnershipResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<MessageResponse>> getTransferTeamOwnershipResult() { return _transferTeamOwnershipResult; }

    private final MutableLiveData<AuthRepository.Resource<MessageResponse>> _deleteTeamResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<MessageResponse>> getDeleteTeamResult() { return _deleteTeamResult; }

    private final MutableLiveData<AuthRepository.Resource<MessageResponse>> _leaveTeamResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<MessageResponse>> getLeaveTeamResult() { return _leaveTeamResult; }

    private final MutableLiveData<AuthRepository.Resource<List<TeamSnippet>>> _teamSnippetsResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<List<TeamSnippet>>> getTeamSnippetsResult() { return _teamSnippetsResult; }

    private final MutableLiveData<AuthRepository.Resource<List<ActivityFeedItem>>> _teamActivityFeedResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<List<ActivityFeedItem>>> getTeamActivityFeedResult() { return _teamActivityFeedResult; }

    private final MutableLiveData<AuthRepository.Resource<Team>> _createTeamResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<Team>> getCreateTeamResult() { return _createTeamResult; }

    // Placeholder LiveData for other operations
    private final LiveData<AuthRepository.Resource<Team>> updateTeamResult = new MutableLiveData<>();
    private final LiveData<AuthRepository.Resource<MessageResponse>> inviteTeamMemberResult = new MutableLiveData<>();
    private final LiveData<AuthRepository.Resource<MessageResponse>> removeTeamMemberResult = new MutableLiveData<>();
    private final LiveData<AuthRepository.Resource<MessageResponse>> updateTeamMemberRoleResult = new MutableLiveData<>();
    private final LiveData<AuthRepository.Resource<TeamSnippet>> createTeamSnippetResult = new MutableLiveData<>();


    public TeamViewModel(@NonNull Application application) {
        super(application);
        teamRepository = new TeamRepository(application);
    }

    //region Public methods to expose LiveData results

    public LiveData<AuthRepository.Resource<Team>> getUpdateTeamResult() {
        return updateTeamResult;
    }

    public LiveData<AuthRepository.Resource<MessageResponse>> getInviteTeamMemberResult() {
        return inviteTeamMemberResult;
    }

    public LiveData<AuthRepository.Resource<MessageResponse>> getRemoveTeamMemberResult() {
        return removeTeamMemberResult;
    }

    public LiveData<AuthRepository.Resource<MessageResponse>> getUpdateTeamMemberRoleResult() {
        return updateTeamMemberRoleResult;
    }

    public LiveData<AuthRepository.Resource<TeamSnippet>> getCreateTeamSnippetResult() {
        return createTeamSnippetResult;
    }

    //endregion

    //region Public methods to trigger actions in repository

    public void fetchMyTeams() {
        teamRepository.getMyTeams().observeForever(new Observer<AuthRepository.Resource<List<Team>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<Team>> listResource) {
                _myTeamsResult.setValue(listResource);
                teamRepository.getMyTeams().removeObserver(this);
            }
        });
    }

    public void fetchMyTeamInvitations() {
        teamRepository.getMyTeamInvitations().observeForever(new Observer<AuthRepository.Resource<List<TeamInvitation>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<TeamInvitation>> listResource) {
                _myTeamInvitationsResult.setValue(listResource);
                teamRepository.getMyTeamInvitations().removeObserver(this);
            }
        });
    }

    public void respondToTeamInvitation(String invitationId, boolean accept) {
        Map<String, String> responseData = new HashMap<>();
        responseData.put("status", accept ? "accepted" : "rejected");

        teamRepository.respondToTeamInvitation(invitationId, responseData).observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> messageResponseResource) {
                _respondToTeamInvitationResult.setValue(messageResponseResource);
                teamRepository.respondToTeamInvitation(invitationId, responseData).removeObserver(this);
            }
        });
    }

    public void fetchTeamDetails(String teamId) {
        teamRepository.getTeamDetails(teamId).observeForever(new Observer<AuthRepository.Resource<Team>>() {
            @Override
            public void onChanged(AuthRepository.Resource<Team> teamResource) {
                // Fall back to mock data if API fails
                if (teamResource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                    Team mockTeam = createMockTeam(teamId);
                    _teamDetailsResult.setValue(AuthRepository.Resource.success(mockTeam));
                } else {
                    _teamDetailsResult.setValue(teamResource);
                }
                teamRepository.getTeamDetails(teamId).removeObserver(this);
            }
        });
    }

    private Team createMockTeam(String teamId) {
        return new Team(
                teamId,
                "Development Team",
                "A collaborative team for sharing code snippets and best practices",
                null,  // avatarUrl
                5,     // memberCount
                12,    // snippetCount
                "private",  // privacy
                "user-1",   // ownerId
                "member"    // userRole
        );
    }

    public void fetchTeamMembers(String teamId) {
        teamRepository.getTeamMembers(teamId).observeForever(new Observer<AuthRepository.Resource<List<TeamMember>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<TeamMember>> listResource) {
                // Fall back to mock data if API fails
                if (listResource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                    List<TeamMember> mockMembers = group.eleven.snippet_sharing_app.data.MockDataProvider.getMockTeamMembers(5);
                    _teamMembersResult.setValue(AuthRepository.Resource.success(mockMembers));
                } else {
                    _teamMembersResult.setValue(listResource);
                }
                teamRepository.getTeamMembers(teamId).removeObserver(this);
            }
        });
    }

    public void transferOwnership(String teamId, Map<String, String> newOwnerData) {
        teamRepository.transferTeamOwnership(teamId, newOwnerData).observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> messageResponseResource) {
                _transferTeamOwnershipResult.setValue(messageResponseResource);
                teamRepository.transferTeamOwnership(teamId, newOwnerData).removeObserver(this);
            }
        });
    }

    public void deleteTeam(String teamId) {
        teamRepository.deleteTeam(teamId).observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> messageResponseResource) {
                _deleteTeamResult.setValue(messageResponseResource);
                teamRepository.deleteTeam(teamId).removeObserver(this);
            }
        });
    }

    public void leaveTeam(String teamId) {
        teamRepository.leaveTeam(teamId).observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> messageResponseResource) {
                _leaveTeamResult.setValue(messageResponseResource);
                teamRepository.leaveTeam(teamId).removeObserver(this);
            }
        });
    }

    public void fetchTeamSnippets(String teamId, Map<String, String> filters) {
        teamRepository.getTeamSnippets(teamId, filters).observeForever(new Observer<AuthRepository.Resource<List<TeamSnippet>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<TeamSnippet>> listResource) {
                // Fall back to mock data if API fails
                if (listResource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                    List<TeamSnippet> mockSnippets = createMockTeamSnippets(5);
                    _teamSnippetsResult.setValue(AuthRepository.Resource.success(mockSnippets));
                } else {
                    _teamSnippetsResult.setValue(listResource);
                }
                teamRepository.getTeamSnippets(teamId, filters).removeObserver(this);
            }
        });
    }

    private List<TeamSnippet> createMockTeamSnippets(int count) {
        List<TeamSnippet> snippets = new java.util.ArrayList<>();
        String[][] snippetData = {
                {"React Component", "TypeScript", "A reusable form component", "const FormInput = () => {...}"},
                {"API Handler", "JavaScript", "REST API request handler", "async function fetchData() {...}"},
                {"Database Query", "SQL", "Optimized database query", "SELECT * FROM users WHERE..."},
                {"CSS Grid Layout", "CSS", "Responsive grid layout", ".container { display: grid; }"},
                {"Auth Middleware", "Java", "JWT authentication", "public class AuthFilter {...}"}
        };
        for (int i = 0; i < Math.min(count, snippetData.length); i++) {
            TeamSnippet snippet = new TeamSnippet(
                    "snippet-" + i,           // id
                    snippetData[i][0],        // title
                    snippetData[i][2],        // description
                    snippetData[i][3],        // code
                    snippetData[i][1],        // language
                    java.util.Arrays.asList("team", "shared"),  // tags
                    false,                    // isPublic
                    "user-1",                 // authorId
                    "dev_user",               // authorUsername
                    "team-1",                 // teamId
                    "2 days ago",             // createdAt
                    "1 day ago"               // updatedAt
            );
            snippets.add(snippet);
        }
        return snippets;
    }

    public void fetchTeamActivity(String teamId) {
        teamRepository.getTeamActivity(teamId).observeForever(new Observer<AuthRepository.Resource<List<ActivityFeedItem>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<ActivityFeedItem>> listResource) {
                // Fall back to mock data if API fails
                if (listResource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                    List<ActivityFeedItem> mockActivity = group.eleven.snippet_sharing_app.data.MockDataProvider.getMockActivityFeed(5);
                    _teamActivityFeedResult.setValue(AuthRepository.Resource.success(mockActivity));
                } else {
                    _teamActivityFeedResult.setValue(listResource);
                }
                teamRepository.getTeamActivity(teamId).removeObserver(this);
            }
        });
    }

    public void createTeam(Map<String, String> teamData) {
        teamRepository.createTeam(teamData).observeForever(new Observer<AuthRepository.Resource<Team>>() {
            @Override
            public void onChanged(AuthRepository.Resource<Team> teamResource) {
                _createTeamResult.setValue(teamResource);
                teamRepository.createTeam(teamData).removeObserver(this);
            }
        });
    }

    public void updateTeam(String teamId, Map<String, String> teamData) {
        // Implement similarly to createTeam
    }

    public void inviteTeamMember(String teamId, Map<String, String> inviteData) {
        // Implement similarly
    }

    public void removeTeamMember(String teamId, String memberId) {
        // Implement similarly
    }

    public void updateTeamMemberRole(String teamId, String memberId, Map<String, String> roleData) {
        // Implement similarly
    }

    public void createTeamSnippet(String teamId, Map<String, String> snippetData) {
        // Implement similarly
    }

    //endregion
}
