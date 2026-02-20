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
import group.eleven.snippet_sharing_app.data.model.Comment;

/**
 * Adapter for displaying comments in a list
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> comments;
    private Context context;
    private OnCommentActionListener listener;

    public interface OnCommentActionListener {
        void onLikeClick(Comment comment, int position);
        void onReplyClick(Comment comment);
        void onAuthorClick(Comment comment);
    }

    public CommentsAdapter() {
        this.comments = new ArrayList<>();
    }

    public void setOnCommentActionListener(OnCommentActionListener listener) {
        this.listener = listener;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments != null ? comments : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        comments.add(0, comment);
        notifyItemInserted(0);
    }

    public void updateComment(int position, Comment comment) {
        if (position >= 0 && position < comments.size()) {
            comments.set(position, comment);
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
        Comment comment = comments.get(position);
        holder.bind(comment, position);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAuthorAvatar;
        TextView tvAuthorName, tvTimeAgo, tvCommentText, tvLikeText, tvLikesCount;
        LinearLayout btnLike, btnReply;
        ImageView ivLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
            // Author name
            tvAuthorName.setText(comment.getDisplayName());

            // Time ago
            tvTimeAgo.setText(comment.getCreatedAt());

            // Comment text
            tvCommentText.setText(comment.getContent());

            // Author avatar
            String avatarUrl = comment.getAuthorAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(context)
                        .load(avatarUrl)
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
            }
        }
    }
}
