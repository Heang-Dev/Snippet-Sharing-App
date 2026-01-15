package group.eleven.snippet_sharing_app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.ActivityFeedItem;

/**
 * Adapter for activity feed RecyclerView
 */
public class ActivityFeedAdapter extends RecyclerView.Adapter<ActivityFeedAdapter.ViewHolder> {

    private final List<ActivityFeedItem> items;

    public ActivityFeedAdapter(List<ActivityFeedItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityFeedItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivActivityIcon;
        private final TextView tvActivityDescription;
        private final TextView tvActivityTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivActivityIcon = itemView.findViewById(R.id.ivActivityIcon);
            tvActivityDescription = itemView.findViewById(R.id.tvActivityDescription);
            tvActivityTime = itemView.findViewById(R.id.tvActivityTime);
        }

        public void bind(ActivityFeedItem item) {
            ivActivityIcon.setImageResource(item.getIconResId());
            tvActivityDescription.setText(item.getDescription());
            tvActivityTime.setText(item.getTimestamp());
        }
    }
}
