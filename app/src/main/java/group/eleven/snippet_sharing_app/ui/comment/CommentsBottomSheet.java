package group.eleven.snippet_sharing_app.ui.comment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Comment;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.data.repository.CommentRepository;
import group.eleven.snippet_sharing_app.utils.Resource;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Bottom sheet fragment for displaying and adding comments
 */
public class CommentsBottomSheet extends BottomSheetDialogFragment implements CommentsAdapter.OnCommentActionListener {

    private static final String ARG_SNIPPET_ID = "snippet_id";
    private static final String ARG_SNIPPET_TITLE = "snippet_title";

    private String snippetId;
    private String snippetTitle;

    private RecyclerView rvComments;
    private LinearLayout layoutEmpty;
    private TextView tvCommentsTitle;
    private ImageView ivClose, ivSend;
    private CircleImageView ivUserAvatar;
    private TextInputEditText etComment;

    private CommentsAdapter adapter;
    private SessionManager sessionManager;
    private CommentRepository commentRepository;

    public static CommentsBottomSheet newInstance(String snippetId, String snippetTitle) {
        CommentsBottomSheet fragment = new CommentsBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SNIPPET_ID, snippetId);
        args.putString(ARG_SNIPPET_TITLE, snippetTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            snippetId = getArguments().getString(ARG_SNIPPET_ID);
            snippetTitle = getArguments().getString(ARG_SNIPPET_TITLE);
        }
        sessionManager = new SessionManager(requireContext());
        commentRepository = new CommentRepository(requireContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);

                // Set max height to 60% of screen
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int maxHeight = (int) (screenHeight * 0.6);

                // Apply max height to the bottom sheet
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = maxHeight;
                bottomSheet.setLayoutParams(layoutParams);

                // Set rounded corners background
                bottomSheet.setBackgroundResource(R.drawable.bg_bottom_sheet_rounded);

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setPeekHeight(maxHeight);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupUserAvatar();
        setupClickListeners();
        loadComments();
    }

    private void initViews(View view) {
        rvComments = view.findViewById(R.id.rvComments);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        tvCommentsTitle = view.findViewById(R.id.tvCommentsTitle);
        ivClose = view.findViewById(R.id.ivClose);
        ivSend = view.findViewById(R.id.ivSend);
        ivUserAvatar = view.findViewById(R.id.ivUserAvatar);
        etComment = view.findViewById(R.id.etComment);
    }

    private void setupRecyclerView() {
        adapter = new CommentsAdapter();
        adapter.setOnCommentActionListener(this);
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComments.setAdapter(adapter);
    }

    private void setupUserAvatar() {
        User user = sessionManager.getUser();
        if (user != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivUserAvatar);
        }
    }

    private void setupClickListeners() {
        ivClose.setOnClickListener(v -> dismiss());

        ivSend.setOnClickListener(v -> {
            String commentText = etComment.getText() != null ? etComment.getText().toString().trim() : "";
            if (!commentText.isEmpty()) {
                postComment(commentText);
            }
        });
    }

    private void loadComments() {
        commentRepository.getComments(snippetId, 50).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setComments(resource.data);
                // Count includes all comments (root + replies)
                int totalCount = countAllComments(resource.data);
                updateEmptyState(totalCount == 0);
                tvCommentsTitle.setText("Comments (" + totalCount + ")");
            } else if (resource.status == Resource.Status.ERROR) {
                // Show empty state when API fails
                adapter.setComments(new ArrayList<>());
                updateEmptyState(true);
                tvCommentsTitle.setText("Comments (0)");
                Toast.makeText(requireContext(), "Unable to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Count all comments including nested replies
     */
    private int countAllComments(List<Comment> comments) {
        int count = 0;
        for (Comment comment : comments) {
            count++; // Count the comment itself
            if (comment.hasReplies()) {
                count += comment.getRepliesCount(); // Count replies
            }
        }
        return count;
    }

    private void postComment(String content) {
        // Disable send button while posting
        ivSend.setEnabled(false);

        commentRepository.addComment(snippetId, content).observe(getViewLifecycleOwner(), resource -> {
            ivSend.setEnabled(true);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.addComment(resource.data);
                etComment.setText("");
                rvComments.scrollToPosition(0);
                updateEmptyState(false);
                updateCommentsCount();
                Toast.makeText(requireContext(), "Comment posted!", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Failed to post comment: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Update the comments count in the title
     */
    private void updateCommentsCount() {
        tvCommentsTitle.setText("Comments (" + adapter.getItemCount() + ")");
    }

    private void updateEmptyState(boolean isEmpty) {
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvComments.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLikeClick(Comment comment, int position) {
        // Optimistic UI update
        boolean originalLikeState = comment.isLiked();
        int originalCount = comment.getLikesCount();
        boolean newLikeState = !originalLikeState;
        int newCount = originalCount + (newLikeState ? 1 : -1);

        comment.setLiked(newLikeState);
        comment.setLikesCount(Math.max(0, newCount));
        adapter.updateComment(position, comment);

        // Call API to persist the like
        commentRepository.toggleLike(comment.getId(), originalLikeState).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.ERROR) {
                // Rollback on error
                comment.setLiked(originalLikeState);
                comment.setLikesCount(originalCount);
                adapter.updateComment(position, comment);
                Toast.makeText(requireContext(), "Failed to update like", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReplyClick(Comment comment) {
        // Pre-fill with @mention
        String mention = "@" + comment.getAuthorUsername() + " ";
        etComment.setText(mention);
        etComment.setSelection(mention.length());
        etComment.requestFocus();
    }

    @Override
    public void onAuthorClick(Comment comment) {
        Toast.makeText(requireContext(), "View profile: " + comment.getDisplayName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to user profile
    }
}
