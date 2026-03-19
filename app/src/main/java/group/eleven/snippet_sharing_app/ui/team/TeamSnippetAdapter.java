package group.eleven.snippet_sharing_app.ui.team;

import android.text.SpannableString;
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
import group.eleven.snippet_sharing_app.utils.SyntaxHighlighter;

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
        private final SyntaxHighlighter syntaxHighlighter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguageBadge = itemView.findViewById(R.id.tvLanguageBadge);
            tvSnippetTitle = itemView.findViewById(R.id.tvSnippetTitle);
            tvSnippetTime = itemView.findViewById(R.id.tvSnippetTime);
            tvCodePreview = itemView.findViewById(R.id.tvCode);
            cardSnippet = itemView.findViewById(R.id.cardSnippet);
            syntaxHighlighter = new SyntaxHighlighter(itemView.getContext());
        }

        public void bind(TeamSnippet teamSnippet, OnTeamSnippetClickListener listener) {
            // Set language badge
            String langName = teamSnippet.getLanguageName();
            if (langName != null && langName.length() > 4) {
                tvLanguageBadge.setText(langName.substring(0, 4));
            } else {
                tvLanguageBadge.setText(langName != null ? langName : "");
            }

            tvSnippetTitle.setText(teamSnippet.getTitle());
            tvSnippetTime.setText(teamSnippet.getTimeAgo());

            // Apply syntax highlighting to code
            String code = teamSnippet.getCode();
            String langSlug = teamSnippet.getLanguage();
            if (code != null && !code.isEmpty()) {
                SpannableString highlightedCode = syntaxHighlighter.highlightForLanguage(code, langSlug);
                tvCodePreview.setText(highlightedCode);
            } else {
                tvCodePreview.setText("// No code preview");
            }

            if (listener != null && cardSnippet != null) {
                cardSnippet.setOnClickListener(v -> listener.onTeamSnippetClick(teamSnippet));
            }
        }
    }
}
