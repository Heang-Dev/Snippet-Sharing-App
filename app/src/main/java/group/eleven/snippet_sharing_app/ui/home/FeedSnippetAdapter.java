package group.eleven.snippet_sharing_app.ui.home;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.utils.SyntaxHighlighter;

/**
 * Adapter for Facebook-style feed snippet cards
 */
public class FeedSnippetAdapter extends RecyclerView.Adapter<FeedSnippetAdapter.ViewHolder> {

    private List<SnippetCard> snippets;
    private OnFeedItemClickListener listener;
    private Context context;

    public interface OnFeedItemClickListener {
        void onSnippetClick(SnippetCard snippet);
        void onLikeClick(SnippetCard snippet, int position);
        void onCommentClick(SnippetCard snippet);
        void onShareClick(SnippetCard snippet);
        void onAuthorClick(SnippetCard snippet);
        void onMoreOptionsClick(SnippetCard snippet, View anchor);
        default void onSaveClick(SnippetCard snippet, int position) {}
    }

    public FeedSnippetAdapter(List<SnippetCard> snippets) {
        this.snippets = snippets != null ? snippets : new ArrayList<>();
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener listener) {
        this.listener = listener;
    }

    public void setSnippets(List<SnippetCard> snippets) {
        this.snippets = snippets != null ? snippets : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void filterList(List<SnippetCard> filteredList) {
        this.snippets = filteredList != null ? filteredList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_feed_snippet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SnippetCard snippet = snippets.get(position);
        holder.bind(snippet, position);
    }

    @Override
    public int getItemCount() {
        return snippets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAuthorAvatar;
        TextView tvAuthorName, tvTimeAgo;
        ImageView ivVisibility, ivMoreOptions;
        TextView tvSnippetTitle, tvSnippetDescription;
        TextView tvLanguageBadge, tvCodePreview;
        LinearLayout tagsContainer;
        TextView tvTag1, tvTag2;
        TextView tvLikesCount, tvCommentsCount;
        LinearLayout btnLike, btnComment, btnShare, btnSave;
        ImageView ivLike, ivSave;
        TextView tvLike, tvSave;
        SyntaxHighlighter syntaxHighlighter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            syntaxHighlighter = new SyntaxHighlighter(itemView.getContext());

            // Author header
            ivAuthorAvatar = itemView.findViewById(R.id.ivAuthorAvatar);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            ivVisibility = itemView.findViewById(R.id.ivVisibility);
            ivMoreOptions = itemView.findViewById(R.id.ivMoreOptions);

            // Snippet content
            tvSnippetTitle = itemView.findViewById(R.id.tvSnippetTitle);
            tvSnippetDescription = itemView.findViewById(R.id.tvSnippetDescription);

            // Code preview
            tvLanguageBadge = itemView.findViewById(R.id.tvLanguageBadge);
            tvCodePreview = itemView.findViewById(R.id.tvCodePreview);

            // Tags
            tagsContainer = itemView.findViewById(R.id.tagsContainer);
            tvTag1 = itemView.findViewById(R.id.tvTag1);
            tvTag2 = itemView.findViewById(R.id.tvTag2);

            // Stats
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
            tvCommentsCount = itemView.findViewById(R.id.tvCommentsCount);

            // Action buttons
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnSave = itemView.findViewById(R.id.btnSave);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLike = itemView.findViewById(R.id.tvLike);
            ivSave = itemView.findViewById(R.id.ivSave);
            tvSave = itemView.findViewById(R.id.tvSave);
        }

        public void bind(SnippetCard snippet, int position) {
            // Author info
            String authorName = snippet.getAuthorName();
            if (authorName == null || authorName.isEmpty()) {
                authorName = snippet.getAuthorUsername();
            }
            if (authorName == null || authorName.isEmpty()) {
                authorName = "Anonymous";
            }
            tvAuthorName.setText(authorName);

            // Author avatar
            String avatarUrl = snippet.getAuthorAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(context)
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivAuthorAvatar);
            } else {
                ivAuthorAvatar.setImageResource(R.drawable.ic_person);
            }

            // Time ago
            tvTimeAgo.setText(snippet.getUpdatedTime());

            // Visibility icon
            String visibility = snippet.getVisibility();
            if ("private".equals(visibility)) {
                ivVisibility.setImageResource(R.drawable.ic_lock);
            } else if ("team".equals(visibility)) {
                ivVisibility.setImageResource(R.drawable.ic_users);
            } else {
                ivVisibility.setImageResource(R.drawable.ic_globe);
            }

            // Snippet title
            tvSnippetTitle.setText(snippet.getTitle());

            // Description
            if (snippet.hasDescription()) {
                tvSnippetDescription.setText(snippet.getDescription());
                tvSnippetDescription.setVisibility(View.VISIBLE);
            } else {
                tvSnippetDescription.setVisibility(View.GONE);
            }

            // Language badge with color
            tvLanguageBadge.setText(snippet.getLanguageBadge());
            try {
                GradientDrawable badgeBg = new GradientDrawable();
                badgeBg.setCornerRadius(8f);
                int langColor = snippet.getLanguageColor();
                if (langColor != 0) {
                    badgeBg.setColor(langColor);
                } else {
                    badgeBg.setColor(ContextCompat.getColor(context, R.color.primary));
                }
                tvLanguageBadge.setBackground(badgeBg);
            } catch (Exception e) {
                // Use default background
            }

            // Code preview with syntax highlighting
            String code = snippet.getCodePreview();
            if (code != null && !code.isEmpty()) {
                SpannableString highlightedCode = syntaxHighlighter.highlightForLanguage(
                        code, snippet.getLanguageBadge());
                tvCodePreview.setText(highlightedCode);
            } else {
                tvCodePreview.setText("// No code preview available");
            }

            // Tags
            String[] tags = snippet.getTags();
            if (tags != null && tags.length > 0) {
                tagsContainer.setVisibility(View.VISIBLE);
                tvTag1.setText("#" + tags[0].replace("#", ""));
                tvTag1.setVisibility(View.VISIBLE);

                if (tags.length > 1) {
                    tvTag2.setText("#" + tags[1].replace("#", ""));
                    tvTag2.setVisibility(View.VISIBLE);
                } else {
                    tvTag2.setVisibility(View.GONE);
                }
            } else {
                tagsContainer.setVisibility(View.GONE);
            }

            // Stats
            tvLikesCount.setText(snippet.getFormattedLikes());
            String commentsText = snippet.getFormattedComments();
            if (commentsText.isEmpty()) {
                tvCommentsCount.setVisibility(View.GONE);
            } else {
                tvCommentsCount.setText(commentsText);
                tvCommentsCount.setVisibility(View.VISIBLE);
            }

            // Like button state
            updateLikeState(snippet.isLiked());

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onSnippetClick(snippet);
            });

            ivAuthorAvatar.setOnClickListener(v -> {
                if (listener != null) listener.onAuthorClick(snippet);
            });

            tvAuthorName.setOnClickListener(v -> {
                if (listener != null) listener.onAuthorClick(snippet);
            });

            ivMoreOptions.setOnClickListener(v -> {
                if (listener != null) listener.onMoreOptionsClick(snippet, v);
            });

            btnLike.setOnClickListener(v -> {
                if (listener != null) listener.onLikeClick(snippet, position);
            });

            btnComment.setOnClickListener(v -> {
                if (listener != null) listener.onCommentClick(snippet);
            });

            btnShare.setOnClickListener(v -> {
                if (listener != null) listener.onShareClick(snippet);
            });

            // Save (favorite) button
            updateSaveState(snippet.isLiked());
            btnSave.setOnClickListener(v -> {
                if (listener != null) listener.onSaveClick(snippet, getAdapterPosition());
            });
        }

        private void updateSaveState(boolean isSaved) {
            if (ivSave != null) {
                ivSave.setImageResource(isSaved ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
                ivSave.setColorFilter(isSaved
                        ? ContextCompat.getColor(context, R.color.selective_yellow)
                        : null);
            }
            if (tvSave != null) {
                tvSave.setText(isSaved ? "Saved" : "Save");
                tvSave.setTextColor(isSaved
                        ? ContextCompat.getColor(context, R.color.selective_yellow)
                        : ContextCompat.getColor(context, android.R.color.darker_gray));
            }
        }

        private void updateLikeState(boolean isLiked) {
            if (isLiked) {
                ivLike.setImageResource(R.drawable.ic_heart_filled);
                ivLike.setColorFilter(ContextCompat.getColor(context, R.color.error));
                tvLike.setTextColor(ContextCompat.getColor(context, R.color.error));
                tvLike.setText("Liked");
            } else {
                ivLike.setImageResource(R.drawable.ic_heart);
                ivLike.setColorFilter(null);
                tvLike.setText("Like");
            }
        }
    }

    public void updateSaveState(int position, boolean isSaved) {
        if (position >= 0 && position < snippets.size()) {
            snippets.get(position).setLiked(isSaved);
            notifyItemChanged(position);
        }
    }

    // Update single item like state
    public void updateLikeState(int position, boolean isLiked, int newLikesCount) {
        if (position >= 0 && position < snippets.size()) {
            SnippetCard snippet = snippets.get(position);
            snippet.setLiked(isLiked);
            snippet.setLikesCount(newLikesCount);
            notifyItemChanged(position);
        }
    }

    /**
     * Update the comment count for a specific snippet
     */
    public void updateCommentCount(String snippetId, int newCount) {
        for (int i = 0; i < snippets.size(); i++) {
            SnippetCard snippet = snippets.get(i);
            if (snippet.getId() != null && snippet.getId().equals(snippetId)) {
                snippet.setCommentsCount(newCount);
                notifyItemChanged(i);
                break;
            }
        }
    }
}
