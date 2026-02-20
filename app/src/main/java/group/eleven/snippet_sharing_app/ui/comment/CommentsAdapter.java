package group.eleven.snippet_sharing_app.ui.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.data.model.Comment;

/**
 * Adapter for displaying comments in a list with reply support and collapsible replies
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_COMMENT = 0;
    private static final int VIEW_TYPE_VIEW_MORE = 1;
    private static final int MAX_VISIBLE_REPLIES = 2;

    private List<Object> displayItems; // Can hold Comment or ViewMoreItem
    private List<Comment> originalComments; // Store original comments for expansion
    private Set<String> expandedParentIds; // Track which parent comments have expanded replies
    private Context context;
    private OnCommentActionListener listener;

    /**
     * Represents a "View X more replies" item
     */
    private static class ViewMoreItem {
        String parentId;
        int remainingCount;

        ViewMoreItem(String parentId, int remainingCount) {
            this.parentId = parentId;
            this.remainingCount = remainingCount;
        }
    }

    public interface OnCommentActionListener {
        void onLikeClick(Comment comment, int position);
        void onReplyClick(Comment comment);
        void onAuthorClick(Comment comment);
        void onEditClick(Comment comment, int position);
        void onDeleteClick(Comment comment, int position);
    }

    private String currentUserId;

    public CommentsAdapter() {
        this.displayItems = new ArrayList<>();
        this.originalComments = new ArrayList<>();
        this.expandedParentIds = new HashSet<>();
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.listener = listener;
    }

    /**
     * Set comments and flatten replies for display with collapsible support
     */
    public void setComments(List<Comment> comments) {
        originalComments = comments != null ? new ArrayList<>(comments) : new ArrayList<>();
        rebuildDisplayList();
    }

    /**
     * Rebuild the display list based on expanded state
     */
    private void rebuildDisplayList() {
        displayItems = new ArrayList<>();
        for (Comment comment : originalComments) {
            displayItems.add(comment);
            if (comment.hasReplies()) {
                List<Comment> replies = comment.getReplies();
                boolean isExpanded = expandedParentIds.contains(comment.getId());

                if (isExpanded || replies.size() <= MAX_VISIBLE_REPLIES) {
                    // Show all replies
                    displayItems.addAll(replies);
                } else {
                    // Show only first MAX_VISIBLE_REPLIES and add "View more" item
                    for (int i = 0; i < MAX_VISIBLE_REPLIES; i++) {
                        displayItems.add(replies.get(i));
                    }
                    int remaining = replies.size() - MAX_VISIBLE_REPLIES;
                    displayItems.add(new ViewMoreItem(comment.getId(), remaining));
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Expand replies for a parent comment
     */
    private void expandReplies(String parentId) {
        expandedParentIds.add(parentId);
        rebuildDisplayList();
    }

    public void addComment(Comment comment) {
        if (!comment.isReply()) {
            // Add new root comment at top
            originalComments.add(0, comment);
        } else {
            // Add reply to parent's replies list
            for (Comment parent : originalComments) {
                if (parent.getId().equals(comment.getParentId())) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<>());
                    }
                    parent.getReplies().add(comment);
                    // Auto-expand when user posts a reply
                    expandedParentIds.add(parent.getId());
                    break;
                }
            }
        }
        rebuildDisplayList();
    }

    public void updateComment(int position, Comment comment) {
        if (position >= 0 && position < displayItems.size()) {
            Object item = displayItems.get(position);
            if (item instanceof Comment) {
                displayItems.set(position, comment);
                notifyItemChanged(position);
            }
        }
    }

    /**
     * Remove a comment from the adapter
     * @return number of items removed (including replies if root comment)
     */
    public int removeComment(Comment comment) {
        int removedCount = 0;

        if (comment.isReply()) {
            // Remove reply from parent's replies list
            for (Comment parent : originalComments) {
                if (parent.getId().equals(comment.getParentId()) && parent.getReplies() != null) {
                    parent.getReplies().removeIf(r -> r.getId().equals(comment.getId()));
                    removedCount = 1;
                    break;
                }
            }
        } else {
            // Remove root comment and count its replies
            for (int i = 0; i < originalComments.size(); i++) {
                if (originalComments.get(i).getId().equals(comment.getId())) {
                    Comment removed = originalComments.remove(i);
                    removedCount = 1 + (removed.hasReplies() ? removed.getRepliesCount() : 0);
                    break;
                }
            }
        }

        if (removedCount > 0) {
            rebuildDisplayList();
        }
        return removedCount;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayItems.get(position);
        return item instanceof ViewMoreItem ? VIEW_TYPE_VIEW_MORE : VIEW_TYPE_COMMENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == VIEW_TYPE_VIEW_MORE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_view_more_replies, parent, false);
            return new ViewMoreViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = displayItems.get(position);
        if (holder instanceof CommentViewHolder && item instanceof Comment) {
            ((CommentViewHolder) holder).bind((Comment) item, position);
        } else if (holder instanceof ViewMoreViewHolder && item instanceof ViewMoreItem) {
            ((ViewMoreViewHolder) holder).bind((ViewMoreItem) item);
        }
    }

    @Override
    public int getItemCount() {
        return displayItems.size();
    }

    /**
     * ViewHolder for "View X more replies" item
     */
    public class ViewMoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvViewMore;
        View rootView;

        public ViewMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            tvViewMore = itemView.findViewById(R.id.tvViewMore);
        }

        public void bind(ViewMoreItem item) {
            String text = "View " + item.remainingCount + " more " +
                         (item.remainingCount == 1 ? "reply" : "replies");
            tvViewMore.setText(text);

            rootView.setOnClickListener(v -> expandReplies(item.parentId));
        }
    }

    /**
     * ViewHolder for comment items
     */
    public class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAuthorAvatar;
        TextView tvAuthorName, tvTimeAgo, tvCommentText, tvLikeText, tvLikesCount;
        LinearLayout btnLike, btnReply;
        ImageView ivLike;
        ImageButton btnMoreOptions;
        View rootView;

        public CommentViewHolder(@NonNull View itemView) {
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
            btnMoreOptions = itemView.findViewById(R.id.btnMoreOptions);
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

            // Show more options button only for user's own comments
            boolean isOwnComment = currentUserId != null && currentUserId.equals(comment.getUserId());
            btnMoreOptions.setVisibility(isOwnComment ? View.VISIBLE : View.GONE);

            if (isOwnComment) {
                btnMoreOptions.setOnClickListener(v -> showPopupMenu(v, comment, position));
            }
        }

        private void showPopupMenu(View anchor, Comment comment, int position) {
            PopupMenu popup = new PopupMenu(context, anchor);
            popup.getMenuInflater().inflate(R.menu.menu_comment_options, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_edit) {
                    if (listener != null) listener.onEditClick(comment, position);
                    return true;
                } else if (id == R.id.action_delete) {
                    if (listener != null) listener.onDeleteClick(comment, position);
                    return true;
                }
                return false;
            });
            popup.show();
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
