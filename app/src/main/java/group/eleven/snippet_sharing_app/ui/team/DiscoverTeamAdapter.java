package group.eleven.snippet_sharing_app.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.data.model.Team;

public class DiscoverTeamAdapter extends RecyclerView.Adapter<DiscoverTeamAdapter.ViewHolder> {

    private List<Team> teams;
    private final OnJoinClickListener listener;

    public interface OnJoinClickListener {
        void onJoinClick(Team team, int position);
    }

    public DiscoverTeamAdapter(OnJoinClickListener listener) {
        this.teams = new ArrayList<>();
        this.listener = listener;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
        notifyDataSetChanged();
    }

    public void updateTeamRequestStatus(int position) {
        if (position >= 0 && position < teams.size()) {
            teams.get(position).setHasPendingRequest(true);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discover_team, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(teams.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView ivTeamAvatar;
        private final TextView tvTeamName;
        private final TextView tvTeamDescription;
        private final TextView tvMemberCount;
        private final TextView tvSnippetCount;
        private final MaterialButton btnJoin;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTeamAvatar = itemView.findViewById(R.id.ivTeamAvatar);
            tvTeamName = itemView.findViewById(R.id.tvTeamName);
            tvTeamDescription = itemView.findViewById(R.id.tvTeamDescription);
            tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
            tvSnippetCount = itemView.findViewById(R.id.tvSnippetCount);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }

        void bind(Team team, OnJoinClickListener listener) {
            tvTeamName.setText(team.getName());

            String desc = team.getDescription();
            if (desc != null && !desc.isEmpty()) {
                tvTeamDescription.setText(desc);
                tvTeamDescription.setVisibility(View.VISIBLE);
            } else {
                tvTeamDescription.setVisibility(View.GONE);
            }

            tvMemberCount.setText(team.getMemberCount() + " members");
            tvSnippetCount.setText(team.getSnippetCount() + " snippets");

            // Load avatar
            String avatarUrl = team.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                String fullUrl = ApiClient.getFullStorageUrl(avatarUrl);
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_users)
                        .error(R.drawable.ic_users)
                        .into(ivTeamAvatar);
            } else {
                ivTeamAvatar.setImageResource(R.drawable.ic_users);
            }

            // Button state
            if (team.hasPendingRequest()) {
                btnJoin.setText("Requested");
                btnJoin.setEnabled(false);
                btnJoin.setAlpha(0.5f);
            } else {
                btnJoin.setText("Join");
                btnJoin.setEnabled(true);
                btnJoin.setAlpha(1.0f);
                btnJoin.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onJoinClick(team, getAdapterPosition());
                    }
                });
            }
        }
    }
}
