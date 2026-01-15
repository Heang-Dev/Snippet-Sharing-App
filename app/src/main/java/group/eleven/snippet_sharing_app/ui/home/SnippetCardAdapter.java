package group.eleven.snippet_sharing_app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;

/**
 * Adapter for snippet cards RecyclerView
 */
public class SnippetCardAdapter extends RecyclerView.Adapter<SnippetCardAdapter.ViewHolder> {

    private final List<SnippetCard> snippets;
    private OnSnippetClickListener listener;

    public interface OnSnippetClickListener {
        void onSnippetClick(SnippetCard snippet);
    }

    public SnippetCardAdapter(List<SnippetCard> snippets) {
        this.snippets = snippets;
    }

    public void setOnSnippetClickListener(OnSnippetClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_snippet_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SnippetCard snippet = snippets.get(position);
        holder.bind(snippet, listener);
    }

    @Override
    public int getItemCount() {
        return snippets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLanguageBadge;
        private final TextView tvSnippetTitle;
        private final TextView tvSnippetTime;
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

        public void bind(SnippetCard snippet, OnSnippetClickListener listener) {
            tvLanguageBadge.setText(snippet.getLanguageBadge());
            tvSnippetTitle.setText(snippet.getTitle());
            tvSnippetTime.setText(snippet.getUpdatedTime());
            tvCodePreview.setText(snippet.getCodePreview());

            if (listener != null) {
                cardView.setOnClickListener(v -> listener.onSnippetClick(snippet));
            }
        }
    }
}
