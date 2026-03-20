package group.eleven.snippet_sharing_app.ui.team.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.net.Uri;

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

    private final MutableLiveData<AuthRepository.Resource<Team>> _updateTeamResult = new MutableLiveData<>();
    private final MutableLiveData<AuthRepository.Resource<MessageResponse>> _inviteTeamMemberResult = new MutableLiveData<>();
    private final MutableLiveData<AuthRepository.Resource<MessageResponse>> _removeTeamMemberResult = new MutableLiveData<>();
    private final MutableLiveData<AuthRepository.Resource<MessageResponse>> _updateTeamMemberRoleResult = new MutableLiveData<>();
    private final MutableLiveData<AuthRepository.Resource<TeamSnippet>> _createTeamSnippetResult = new MutableLiveData<>();

    private final MutableLiveData<AuthRepository.Resource<List<Team>>> _discoverTeamsResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<List<Team>>> getDiscoverTeamsResult() { return _discoverTeamsResult; }

    private final MutableLiveData<AuthRepository.Resource<Object>> _requestJoinTeamResult = new MutableLiveData<>();
    public LiveData<AuthRepository.Resource<Object>> getRequestJoinTeamResult() { return _requestJoinTeamResult; }


    public TeamViewModel(@NonNull Application application) {
        super(application);
        teamRepository = new TeamRepository(application);
    }

    //region Public methods to expose LiveData results

    public LiveData<AuthRepository.Resource<Team>> getUpdateTeamResult() {
        return _updateTeamResult;
    }

    public LiveData<AuthRepository.Resource<MessageResponse>> getInviteTeamMemberResult() {
        return _inviteTeamMemberResult;
    }

    public LiveData<AuthRepository.Resource<MessageResponse>> getRemoveTeamMemberResult() {
        return _removeTeamMemberResult;
    }

    public LiveData<AuthRepository.Resource<MessageResponse>> getUpdateTeamMemberRoleResult() {
        return _updateTeamMemberRoleResult;
    }

    public LiveData<AuthRepository.Resource<TeamSnippet>> getCreateTeamSnippetResult() {
        return _createTeamSnippetResult;
    }

    //endregion

    //region Public methods to trigger actions in repository

    public void fetchMyTeams() {
        LiveData<AuthRepository.Resource<TeamsResponse>> source = teamRepository.getMyTeams();
        source.observeForever(new Observer<AuthRepository.Resource<TeamsResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<TeamsResponse> resource) {
                _myTeamsResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void fetchMyTeamInvitations() {
        LiveData<AuthRepository.Resource<List<TeamInvitation>>> source = teamRepository.getMyTeamInvitations();
        source.observeForever(new Observer<AuthRepository.Resource<List<TeamInvitation>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<TeamInvitation>> resource) {
                _myTeamInvitationsResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void respondToTeamInvitation(String invitationId, boolean accept) {
        LiveData<AuthRepository.Resource<MessageResponse>> source = accept
                ? teamRepository.acceptTeamInvitation(invitationId)
                : teamRepository.declineTeamInvitation(invitationId);

        source.observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> resource) {
                _respondToTeamInvitationResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void fetchTeamDetails(String teamId) {
        LiveData<AuthRepository.Resource<Team>> source = teamRepository.getTeamDetails(teamId);
        source.observeForever(new Observer<AuthRepository.Resource<Team>>() {
            @Override
            public void onChanged(AuthRepository.Resource<Team> resource) {
                _teamDetailsResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void fetchTeamMembers(String teamId) {
        LiveData<AuthRepository.Resource<List<TeamMember>>> source = teamRepository.getTeamMembers(teamId);
        source.observeForever(new Observer<AuthRepository.Resource<List<TeamMember>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<TeamMember>> resource) {
                _teamMembersResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void transferOwnership(String teamId, Map<String, String> newOwnerData) {
        LiveData<AuthRepository.Resource<MessageResponse>> source = teamRepository.transferTeamOwnership(teamId, newOwnerData);
        source.observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> resource) {
                _transferTeamOwnershipResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void deleteTeam(String teamId) {
        LiveData<AuthRepository.Resource<MessageResponse>> source = teamRepository.deleteTeam(teamId);
        source.observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> resource) {
                _deleteTeamResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void leaveTeam(String teamId) {
        LiveData<AuthRepository.Resource<MessageResponse>> source = teamRepository.leaveTeam(teamId);
        source.observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> resource) {
                _leaveTeamResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void fetchTeamSnippets(String teamId, Map<String, String> filters) {
        LiveData<AuthRepository.Resource<List<TeamSnippet>>> source = teamRepository.getTeamSnippets(teamId, filters);
        source.observeForever(new Observer<AuthRepository.Resource<List<TeamSnippet>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<TeamSnippet>> resource) {
                _teamSnippetsResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void fetchTeamActivity(String teamId) {
        LiveData<AuthRepository.Resource<List<ActivityFeedItem>>> source = teamRepository.getTeamActivity(teamId);
        source.observeForever(new Observer<AuthRepository.Resource<List<ActivityFeedItem>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<ActivityFeedItem>> resource) {
                _teamActivityFeedResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void createTeam(Map<String, String> teamData) {
        LiveData<AuthRepository.Resource<Team>> source = teamRepository.createTeam(teamData);
        source.observeForever(new Observer<AuthRepository.Resource<Team>>() {
            @Override
            public void onChanged(AuthRepository.Resource<Team> resource) {
                _createTeamResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void updateTeam(String teamId, Map<String, String> teamData) {
        LiveData<AuthRepository.Resource<Team>> source = teamRepository.updateTeam(teamId, teamData);
        source.observeForever(new Observer<AuthRepository.Resource<Team>>() {
            @Override
            public void onChanged(AuthRepository.Resource<Team> resource) {
                _updateTeamResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void inviteTeamMember(String teamId, Map<String, String> inviteData) {
        LiveData<AuthRepository.Resource<MessageResponse>> source = teamRepository.inviteTeamMember(teamId, inviteData);
        source.observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> resource) {
                _inviteTeamMemberResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void removeTeamMember(String teamId, String memberId) {
        LiveData<AuthRepository.Resource<MessageResponse>> source = teamRepository.removeTeamMember(teamId, memberId);
        source.observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> resource) {
                _removeTeamMemberResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void updateTeamMemberRole(String teamId, String memberId, Map<String, String> roleData) {
        LiveData<AuthRepository.Resource<MessageResponse>> source = teamRepository.updateTeamMemberRole(teamId, memberId, roleData);
        source.observeForever(new Observer<AuthRepository.Resource<MessageResponse>>() {
            @Override
            public void onChanged(AuthRepository.Resource<MessageResponse> resource) {
                _updateTeamMemberRoleResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void createTeamWithAvatar(String name, String description, String privacy, Uri avatarUri) {
        LiveData<AuthRepository.Resource<Team>> source = teamRepository.createTeamWithAvatar(
                getApplication(), name, description, privacy, avatarUri);
        source.observeForever(new Observer<AuthRepository.Resource<Team>>() {
            @Override
            public void onChanged(AuthRepository.Resource<Team> resource) {
                _createTeamResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void discoverTeams(Map<String, String> filters) {
        LiveData<AuthRepository.Resource<List<Team>>> source = teamRepository.discoverTeams(filters);
        source.observeForever(new Observer<AuthRepository.Resource<List<Team>>>() {
            @Override
            public void onChanged(AuthRepository.Resource<List<Team>> resource) {
                _discoverTeamsResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void requestJoinTeam(String teamId, Map<String, String> body) {
        LiveData<AuthRepository.Resource<Object>> source = teamRepository.requestJoinTeam(teamId, body);
        source.observeForever(new Observer<AuthRepository.Resource<Object>>() {
            @Override
            public void onChanged(AuthRepository.Resource<Object> resource) {
                _requestJoinTeamResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    public void createTeamSnippet(String teamId, Map<String, String> snippetData) {
        LiveData<AuthRepository.Resource<TeamSnippet>> source = teamRepository.createTeamSnippet(teamId, snippetData);
        source.observeForever(new Observer<AuthRepository.Resource<TeamSnippet>>() {
            @Override
            public void onChanged(AuthRepository.Resource<TeamSnippet> resource) {
                _createTeamSnippetResult.setValue(resource);
                if (resource.getStatus() != AuthRepository.Resource.Status.LOADING) {
                    source.removeObserver(this);
                }
            }
        });
    }

    //endregion
}
