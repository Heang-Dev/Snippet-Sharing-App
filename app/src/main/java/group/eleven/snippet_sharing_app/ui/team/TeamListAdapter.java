package group.eleven.snippet_sharing_app.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // ADDED THIS IMPORT

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Team;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.TeamViewHolder> {

    private List<Team> teams;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Team team);
    }

    public TeamListAdapter(OnItemClickListener listener) {
        this.teams = new ArrayList<>();
        this.listener = listener;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
        notifyDataSetChanged();
    }

    public void addTeam(Team team) {
        teams.add(team);
        notifyItemInserted(teams.size() - 1);
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_card, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.bind(team, listener);
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTeamAvatar;
        TextView tvTeamName;
        TextView tvRoleBadge;
        TextView tvMemberCount;
        TextView tvSnippetCount;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTeamAvatar = itemView.findViewById(R.id.iv_team_avatar);
            tvTeamName = itemView.findViewById(R.id.tv_team_name);
            tvRoleBadge = itemView.findViewById(R.id.tv_role_badge);
            tvMemberCount = itemView.findViewById(R.id.tv_member_count);
            tvSnippetCount = itemView.findViewById(R.id.tv_snippet_count);
        }

        public void bind(final Team team, final OnItemClickListener listener) {
            tvTeamName.setText(team.getName());
            tvMemberCount.setText(itemView.getContext().getString(R.string.team_member_count, team.getMemberCount()));
            tvSnippetCount.setText(itemView.getContext().getString(R.string.team_snippet_count, team.getSnippetCount()));

            // Set role badge visibility and text
            if (team.getUserRole() != null && !team.getUserRole().isEmpty()) {
                tvRoleBadge.setText(team.getUserRole());
                tvRoleBadge.setVisibility(View.VISIBLE);
            } else {
                tvRoleBadge.setVisibility(View.GONE);
            }

            // Load team avatar using Glide
            if (team.getAvatarUrl() != null && !team.getAvatarUrl().isEmpty()) {
                ivTeamAvatar.setPadding(0, 0, 0, 0); // Remove padding for actual image
                ivTeamAvatar.setImageTintList(null); // Remove tint for actual image
                Glide.with(itemView.getContext())
                        .load(team.getAvatarUrl())
                        .placeholder(R.drawable.ic_users)
                        .error(R.drawable.ic_users)
                        .circleCrop()
                        .into(ivTeamAvatar);
            } else {
                // Show default icon with padding and tint
                int padding = (int) (12 * itemView.getContext().getResources().getDisplayMetrics().density);
                ivTeamAvatar.setPadding(padding, padding, padding, padding);
                ivTeamAvatar.setImageResource(R.drawable.ic_users);
            }


            itemView.setOnClickListener(v -> listener.onItemClick(team));
        }
    }
}
