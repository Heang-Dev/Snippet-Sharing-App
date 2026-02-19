package group.eleven.snippet_sharing_app.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.TeamSnippet;
import group.eleven.snippet_sharing_app.ui.home.SnippetCardAdapter;

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
                .inflate(R.layout.item_snippet_card, parent, false); // Reuse existing snippet card layout
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
        private final TextView tvSnippetTime; // Assuming team snippets also have updated_at
        private final TextView tvCodePreview;
        private final CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvLanguageBadge = itemView.findViewById(R.id.tvLanguageBadge);
            tvSnippetTitle = itemView.findViewById(R.id.tvSnippetTitle);
            tvSnippetTime = itemView.findViewById(R.id.tvSnippetTime);
            tvCodePreview = itemView.findViewById(R.id.tvCodePreview);
        }

        public void bind(TeamSnippet teamSnippet, OnTeamSnippetClickListener listener) {
            tvLanguageBadge.setText(teamSnippet.getLanguage()); // Assuming getLanguage() for badge
            tvSnippetTitle.setText(teamSnippet.getTitle());
            tvSnippetTime.setText(teamSnippet.getUpdatedAt()); // Using updatedAt for now
            tvCodePreview.setText(teamSnippet.getCode()); // Using getCode() for preview

            if (listener != null) {
                cardView.setOnClickListener(v -> listener.onTeamSnippetClick(teamSnippet));
            }
        }
    }
}
