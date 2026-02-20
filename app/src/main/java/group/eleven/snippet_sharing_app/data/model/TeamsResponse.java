package group.eleven.snippet_sharing_app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Response model for teams list API
 * Backend returns owned teams and teams user is member of separately
 */
public class TeamsResponse {
    @SerializedName("owned")
    private List<Team> owned;

    @SerializedName("member_of")
    private List<Team> memberOf;

    public List<Team> getOwned() {
        return owned != null ? owned : new ArrayList<>();
    }

    public void setOwned(List<Team> owned) {
        this.owned = owned;
    }

    public List<Team> getMemberOf() {
        return memberOf != null ? memberOf : new ArrayList<>();
    }

    public void setMemberOf(List<Team> memberOf) {
        this.memberOf = memberOf;
    }

    /**
     * Get all teams combined (owned + member of)
     */
    public List<Team> getAllTeams() {
        List<Team> allTeams = new ArrayList<>();
        if (owned != null) {
            allTeams.addAll(owned);
        }
        if (memberOf != null) {
            allTeams.addAll(memberOf);
        }
        return allTeams;
    }
}
