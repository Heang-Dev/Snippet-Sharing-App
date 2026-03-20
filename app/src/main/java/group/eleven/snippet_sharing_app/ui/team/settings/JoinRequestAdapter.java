package group.eleven.snippet_sharing_app.ui.team.settings;

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
import group.eleven.snippet_sharing_app.data.model.TeamJoinRequest;

public class JoinRequestAdapter extends RecyclerView.Adapter<JoinRequestAdapter.ViewHolder> {

    public interface OnActionListener {
        void onApprove(TeamJoinRequest request, int position);
        void onReject(TeamJoinRequest request, int position);
    }

    private List<TeamJoinRequest> requests = new ArrayList<>();
    private final OnActionListener listener;

    public JoinRequestAdapter(OnActionListener listener) {
        this.listener = listener;
    }

    public void setRequests(List<TeamJoinRequest> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        if (position >= 0 && position < requests.size()) {
            requests.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_join_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(requests.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CircleImageView ivAvatar;
        final TextView tvUsername;
        final TextView tvRequestedAt;
        final TextView tvMessage;
        final MaterialButton btnApprove;
        final MaterialButton btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvRequestedAt = itemView.findViewById(R.id.tvRequestedAt);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }

        void bind(TeamJoinRequest request, OnActionListener listener) {
            TeamJoinRequest.Requester user = request.getUser();
            if (user != null) {
                tvUsername.setText(user.getDisplayName());
                String avatarUrl = user.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(ApiClient.getFullStorageUrl(avatarUrl))
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(ivAvatar);
                } else {
                    ivAvatar.setImageResource(R.drawable.ic_person);
                }
            }

            // Show created_at in a friendly format
            String createdAt = request.getCreatedAt();
            if (createdAt != null && createdAt.length() >= 10) {
                tvRequestedAt.setText("Requested on " + createdAt.substring(0, 10));
            } else {
                tvRequestedAt.setText("Pending");
            }

            // Show message if present
            String message = request.getMessage();
            if (message != null && !message.isEmpty()) {
                tvMessage.setText("\"" + message + "\"");
                tvMessage.setVisibility(View.VISIBLE);
            } else {
                tvMessage.setVisibility(View.GONE);
            }

            btnApprove.setOnClickListener(v -> {
                if (listener != null) listener.onApprove(request, getAdapterPosition());
            });
            btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(request, getAdapterPosition());
            });
        }
    }
}
