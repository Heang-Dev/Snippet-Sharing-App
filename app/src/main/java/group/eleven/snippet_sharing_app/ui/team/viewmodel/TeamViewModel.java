package group.eleven.snippet_sharing_app.ui.team.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer; // Import Observer

import java.util.List;
import java.util.Map;

import group.eleven.snippet_sharing_app.data.model.ActivityFeedItem;
import group.eleven.snippet_sharing_app.data.model.MessageResponse;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamInvitation;
import group.eleven.snippet_sharing_app.data.model.TeamMember;
import group.eleven.snippet_sharing_app.data.model.TeamSnippet;
import group.eleven.snippet_sharing_app.data.model.TeamsResponse;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.data.repository.TeamRepository;

public class TeamViewModel extends AndroidViewModel {

    private final TeamRepository teamRepository;

    // Use MutableLiveData internally to allow updating
    private final MutableLiveData<AuthRepository.Resource<TeamsResponse>> _myTeamsResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<TeamsResponse>> getMyTeamsResult() {
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
        teamRepository.getMyTeams().observeForever(new Observer<AuthRepository.Resource<TeamsResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<TeamsResponse> teamsResponseResource) {
                _myTeamsResult.setValue(teamsResponseResource);
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
        LiveData<AuthRepository.Resource<MessageResponse>> liveData = accept
                ? teamRepository.acceptTeamInvitation(invitationId)
                : teamRepository.declineTeamInvitation(invitationId);

        liveData.observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> messageResponseResource) {
                _respondToTeamInvitationResult.setValue(messageResponseResource);
                liveData.removeObserver(this);
            }
        });
    }

    public void fetchTeamDetails(String teamId) {
        teamRepository.getTeamDetails(teamId).observeForever(new Observer<AuthRepository.Resource<Team>>() {
            @Override
            public void onChanged(AuthRepository.Resource<Team> teamResource) {
                _teamDetailsResult.setValue(teamResource);
                teamRepository.getTeamDetails(teamId).removeObserver(this);
            }
        });
    }

    public void fetchTeamMembers(String teamId) {
        teamRepository.getTeamMembers(teamId).observeForever(new Observer<AuthRepository.Resource<List<TeamMember>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<TeamMember>> listResource) {
                _teamMembersResult.setValue(listResource);
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
                _teamSnippetsResult.setValue(listResource);
                teamRepository.getTeamSnippets(teamId, filters).removeObserver(this);
            }
        });
    }

    public void fetchTeamActivity(String teamId) {
        teamRepository.getTeamActivity(teamId).observeForever(new Observer<AuthRepository.Resource<List<ActivityFeedItem>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<ActivityFeedItem>> listResource) {
                _teamActivityFeedResult.setValue(listResource);
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
