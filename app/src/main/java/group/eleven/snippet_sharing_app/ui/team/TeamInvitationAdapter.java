package group.eleven.snippet_sharing_app.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.TeamInvitation;

public class TeamInvitationAdapter extends RecyclerView.Adapter<TeamInvitationAdapter.InvitationViewHolder> {

    private List<TeamInvitation> invitations = new ArrayList<>();
    private OnInvitationActionListener listener;

    public interface OnInvitationActionListener {
        void onAcceptInvitation(TeamInvitation invitation);
        void onRejectInvitation(TeamInvitation invitation);
    }

    public TeamInvitationAdapter(OnInvitationActionListener listener) {
        this.listener = listener;
    }

    public void setInvitations(List<TeamInvitation> invitations) {
        this.invitations = invitations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_invitation, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        TeamInvitation invitation = invitations.get(position);
        holder.bind(invitation);
    }

    @Override
    public int getItemCount() {
        return invitations.size();
    }

    class InvitationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInvitationTitle;
        private TextView tvInvitedBy;
        private TextView tvCreatedAt;
        private Button btnAcceptInvitation;
        private Button btnRejectInvitation;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvitationTitle = itemView.findViewById(R.id.tv_invitation_title);
            tvInvitedBy = itemView.findViewById(R.id.tv_invited_by);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            btnAcceptInvitation = itemView.findViewById(R.id.btn_accept_invitation);
            btnRejectInvitation = itemView.findViewById(R.id.btn_reject_invitation);
        }

        public void bind(final TeamInvitation invitation) {
            tvInvitationTitle.setText(String.format("Invitation to '%s'", invitation.getTeamName()));
            tvInvitedBy.setText(String.format("Invited by %s (%s)", invitation.getInvitedByUsername(), invitation.getInvitedEmail()));
            tvCreatedAt.setText(String.format("Received: %s", invitation.getCreatedAt())); // You might want to format this date better

            btnAcceptInvitation.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptInvitation(invitation);
                }
            });

            btnRejectInvitation.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRejectInvitation(invitation);
                }
            });
        }
    }
}
