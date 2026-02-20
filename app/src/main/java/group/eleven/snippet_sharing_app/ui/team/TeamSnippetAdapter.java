package group.eleven.snippet_sharing_app.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.TeamSnippet;

public class TeamSnippetAdapter extends RecyclerView.Adapter<TeamSnippetAdapter.ViewHolder> {

    private List<TeamSnippet> teamSnippets;
    private OnTeamSnippetClickListener listener;

    public interface OnTeamSnippetClickListener {
        void onTeamSnippetClick(TeamSnippet teamSnippet);
    }

    public TeamSnippetAdapter(OnTeamSnippetClickListener listener) {
        this.teamSnippets = new ArrayList<>();
        this.listener = listener;
    }

    public void setTeamSnippets(List<TeamSnippet> teamSnippets) {
        this.teamSnippets = teamSnippets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_snippet_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeamSnippet teamSnippet = teamSnippets.get(position);
        holder.bind(teamSnippet, listener);
    }

    @Override
    public int getItemCount() {
        return teamSnippets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLanguageBadge;
        private final TextView tvSnippetTitle;
        private final TextView tvSnippetTime;
        private final TextView tvCodePreview;
        private final MaterialCardView cardSnippet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguageBadge = itemView.findViewById(R.id.tvLanguageBadge);
            tvSnippetTitle = itemView.findViewById(R.id.tvSnippetTitle);
            tvSnippetTime = itemView.findViewById(R.id.tvSnippetTime);
            tvCodePreview = itemView.findViewById(R.id.tvCode);
            cardSnippet = itemView.findViewById(R.id.cardSnippet);
        }

        public void bind(TeamSnippet teamSnippet, OnTeamSnippetClickListener listener) {
            // Set language badge (abbreviate if needed)
            String lang = teamSnippet.getLanguage();
            if (lang != null && lang.length() > 4) {
                tvLanguageBadge.setText(lang.substring(0, 4));
            } else {
                tvLanguageBadge.setText(lang != null ? lang : "");
            }

            tvSnippetTitle.setText(teamSnippet.getTitle());
            tvSnippetTime.setText(teamSnippet.getUpdatedAt());
            tvCodePreview.setText(teamSnippet.getCode());

            if (listener != null && cardSnippet != null) {
                cardSnippet.setOnClickListener(v -> listener.onTeamSnippetClick(teamSnippet));
            }
        }
    }
}
