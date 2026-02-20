package group.eleven.snippet_sharing_app.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.TeamMember;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.TeamMemberViewHolder> {

    private List<TeamMember> teamMembers;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TeamMember member);
        void onMoreOptionsClick(TeamMember member, View view);
    }

    public TeamMemberAdapter(OnItemClickListener listener) {
        this.teamMembers = new ArrayList<>();
        this.listener = listener;
    }

    public void setTeamMembers(List<TeamMember> teamMembers) {
        this.teamMembers = teamMembers;
        notifyDataSetChanged();
    }

    public void addTeamMember(TeamMember member) {
        teamMembers.add(member);
        notifyItemInserted(teamMembers.size() - 1);
    }

    @NonNull
    @Override
    public TeamMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_member, parent, false);
        return new TeamMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamMemberViewHolder holder, int position) {
        TeamMember member = teamMembers.get(position);
        holder.bind(member, listener);
    }

    @Override
    public int getItemCount() {
        return teamMembers.size();
    }

    static class TeamMemberViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMemberAvatar;
        TextView tvMemberUsername;
        TextView tvMemberRole;
        ImageView ivMenuOptions;

        public TeamMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMemberAvatar = itemView.findViewById(R.id.iv_member_avatar);
            tvMemberUsername = itemView.findViewById(R.id.tv_member_username);
            tvMemberRole = itemView.findViewById(R.id.tv_member_role);
            ivMenuOptions = itemView.findViewById(R.id.iv_menu_options);
        }

        public void bind(final TeamMember member, final OnItemClickListener listener) {
            tvMemberUsername.setText(member.getUsername());
            tvMemberRole.setText(member.getRole());

            // Load member avatar using Glide
            if (member.getAvatarUrl() != null && !member.getAvatarUrl().isEmpty()) {
                ivMemberAvatar.setPadding(0, 0, 0, 0);
                ivMemberAvatar.setImageTintList(null);
                Glide.with(itemView.getContext())
                        .load(member.getAvatarUrl())
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .circleCrop()
                        .into(ivMemberAvatar);
            } else {
                // Show default icon with padding
                int padding = (int) (10 * itemView.getContext().getResources().getDisplayMetrics().density);
                ivMemberAvatar.setPadding(padding, padding, padding, padding);
                ivMemberAvatar.setImageResource(R.drawable.ic_person);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(member));
            ivMenuOptions.setOnClickListener(v -> listener.onMoreOptionsClick(member, v));
        }
    }
}
