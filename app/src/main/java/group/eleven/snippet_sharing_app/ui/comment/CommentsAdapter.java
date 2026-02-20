package group.eleven.snippet_sharing_app.ui.comment;

import android.content.Context;
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
import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.data.model.Comment;

/**
 * Adapter for displaying comments in a list with reply support
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> flattenedComments;
    private Context context;
    private OnCommentActionListener listener;

    public interface OnCommentActionListener {
        void onLikeClick(Comment comment, int position);
        void onReplyClick(Comment comment);
        void onAuthorClick(Comment comment);
    }

    public CommentsAdapter() {
        this.flattenedComments = new ArrayList<>();
    }

    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.listener = listener;
    }

    /**
     * Set comments and flatten replies for display
     */
    public void setComments(List<Comment> comments) {
        flattenedComments = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                flattenedComments.add(comment);
                // Add replies after parent comment
                if (comment.hasReplies()) {
                    flattenedComments.addAll(comment.getReplies());
                }
            }
        }
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        // Add new comment at top if it's a root comment
        if (!comment.isReply()) {
            flattenedComments.add(0, comment);
            notifyItemInserted(0);
        } else {
            // Add reply after its parent
            for (int i = 0; i < flattenedComments.size(); i++) {
                if (flattenedComments.get(i).getId().equals(comment.getParentId())) {
                    // Find the end of replies for this parent
                    int insertPos = i + 1;
                    while (insertPos < flattenedComments.size() &&
                           flattenedComments.get(insertPos).isReply() &&
                           comment.getParentId().equals(flattenedComments.get(insertPos).getParentId())) {
                        insertPos++;
                    }
                    flattenedComments.add(insertPos, comment);
                    notifyItemInserted(insertPos);
                    return;
                }
            }
            // If parent not found, add at top
            flattenedComments.add(0, comment);
            notifyItemInserted(0);
        }
    }

    public void updateComment(int position, Comment comment) {
        if (position >= 0 && position < flattenedComments.size()) {
            flattenedComments.set(position, comment);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = flattenedComments.get(position);
        holder.bind(comment, position);
    }

    @Override
    public int getItemCount() {
        return flattenedComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAuthorAvatar;
        TextView tvAuthorName, tvTimeAgo, tvCommentText, tvLikeText, tvLikesCount;
        LinearLayout btnLike, btnReply;
        ImageView ivLike;
        View rootView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            ivAuthorAvatar = itemView.findViewById(R.id.ivAuthorAvatar);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnReply = itemView.findViewById(R.id.btnReply);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikeText = itemView.findViewById(R.id.tvLikeText);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
        }

        public void bind(Comment comment, int position) {
            // Apply indentation for replies
            int basePadding = (int) (16 * context.getResources().getDisplayMetrics().density);
            int replyIndent = (int) (40 * context.getResources().getDisplayMetrics().density);
            if (comment.isReply()) {
                rootView.setPadding(basePadding + replyIndent,
                    (int) (8 * context.getResources().getDisplayMetrics().density),
                    basePadding,
                    (int) (8 * context.getResources().getDisplayMetrics().density));
                // Smaller avatar for replies
                ViewGroup.LayoutParams params = ivAuthorAvatar.getLayoutParams();
                params.width = (int) (28 * context.getResources().getDisplayMetrics().density);
                params.height = (int) (28 * context.getResources().getDisplayMetrics().density);
                ivAuthorAvatar.setLayoutParams(params);
            } else {
                rootView.setPadding(basePadding,
                    (int) (12 * context.getResources().getDisplayMetrics().density),
                    basePadding,
                    (int) (12 * context.getResources().getDisplayMetrics().density));
                // Normal avatar size for main comments
                ViewGroup.LayoutParams params = ivAuthorAvatar.getLayoutParams();
                params.width = (int) (36 * context.getResources().getDisplayMetrics().density);
                params.height = (int) (36 * context.getResources().getDisplayMetrics().density);
                ivAuthorAvatar.setLayoutParams(params);
            }

            // Author name with edited indicator
            String displayName = comment.getDisplayName();
            if (comment.isEdited()) {
                displayName += " (edited)";
            }
            tvAuthorName.setText(displayName);

            // Formatted time ago
            tvTimeAgo.setText(comment.getFormattedTime());

            // Comment text
            tvCommentText.setText(comment.getContent());

            // Author avatar with proper URL
            String avatarUrl = comment.getAuthorAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                String fullUrl = ApiClient.getFullStorageUrl(avatarUrl);
                Glide.with(context)
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivAuthorAvatar);
            } else {
                ivAuthorAvatar.setImageResource(R.drawable.ic_person);
            }

            // Like state
            updateLikeState(comment.isLiked());

            // Likes count
            String likesText = comment.getFormattedLikes();
            if (likesText.isEmpty()) {
                tvLikesCount.setVisibility(View.GONE);
            } else {
                tvLikesCount.setText(likesText);
                tvLikesCount.setVisibility(View.VISIBLE);
            }

            // Click listeners
            ivAuthorAvatar.setOnClickListener(v -> {
                if (listener != null) listener.onAuthorClick(comment);
            });

            tvAuthorName.setOnClickListener(v -> {
                if (listener != null) listener.onAuthorClick(comment);
            });

            btnLike.setOnClickListener(v -> {
                if (listener != null) listener.onLikeClick(comment, position);
            });

            btnReply.setOnClickListener(v -> {
                if (listener != null) listener.onReplyClick(comment);
            });
        }

        private void updateLikeState(boolean isLiked) {
            if (isLiked) {
                ivLike.setImageResource(R.drawable.ic_heart_filled);
                ivLike.setColorFilter(ContextCompat.getColor(context, R.color.error));
                tvLikeText.setText("Liked");
                tvLikeText.setTextColor(ContextCompat.getColor(context, R.color.error));
            } else {
                ivLike.setImageResource(R.drawable.ic_heart);
                ivLike.setColorFilter(null);
                tvLikeText.setText("Like");
                tvLikeText.setTextColor(ContextCompat.getColor(context,
                    android.R.color.secondary_text_dark));
            }
        }
    }
}
