package group.eleven.snippet_sharing_app.ui.team;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.model.TeamModel;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private List<TeamModel> teams = new ArrayList<>();
    private OnTeamClickListener listener;

    public interface OnTeamClickListener {
        void onTeamClick(TeamModel team);
    }

    public TeamAdapter(OnTeamClickListener listener) {
        this.listener = listener;
    }

    public void setTeams(List<TeamModel> teams) {
        this.teams = teams;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_row, parent, false);
        return new TeamViewHolder(updateView(view));
    }

    // Helper to allow finding views from viewholder
    private View updateView(View v) {
        return v;
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        holder.bind(teams.get(position));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvMembers;
        ImageView ivSelection;
        View rootView;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvName = itemView.findViewById(R.id.tvTeamName);
            tvMembers = itemView.findViewById(R.id.tvMemberCount);
            ivSelection = itemView.findViewById(R.id.ivSelection);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTeamClick(teams.get(pos));
                }
            });
        }

        public void bind(TeamModel team) {
            tvName.setText(team.getName());
            tvMembers.setText(team.getMemberCount() + " members");
            tvAvatar.setText(team.getInitials());

            // Set Avatar Color
            GradientDrawable avatarBg = (GradientDrawable) tvAvatar.getBackground().mutate();
            avatarBg.setColor(team.getColorHex());

            if (team.isSelected()) {
                // Green border + faint green fill
                rootView.setBackgroundResource(R.drawable.bg_team_item_selected);
                ivSelection.setImageResource(R.drawable.ic_check_circle);
                ivSelection.setColorFilter(Color.parseColor("#3DD68C")); // Green tint
            } else {
                // Transparent
                rootView.setBackgroundResource(0); // or transparent
                ivSelection.setImageResource(R.drawable.ic_radio_unchecked); // Need proper icon or shape
                ivSelection.clearColorFilter();
                ivSelection.setColorFilter(Color.parseColor("#889990")); // Grey tint
            }
        }
    }
}
