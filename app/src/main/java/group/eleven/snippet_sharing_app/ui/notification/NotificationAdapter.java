package group.eleven.snippet_sharing_app.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.NotificationItem;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> notifications = new ArrayList<>();
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem notification);
    }

    public NotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void setNotifications(List<NotificationItem> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public void addNotifications(List<NotificationItem> newNotifications) {
        int startPosition = notifications.size();
        notifications.addAll(newNotifications);
        notifyItemRangeInserted(startPosition, newNotifications.size());
    }

    public List<NotificationItem> getNotifications() {
        return notifications;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardNotification;
        private final ImageView ivNotificationIcon;
        private final TextView tvNotificationTitle;
        private final TextView tvNotificationMessage;
        private final TextView tvNotificationTime;
        private final View viewUnreadIndicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNotification = itemView.findViewById(R.id.cardNotification);
            ivNotificationIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvNotificationTime = itemView.findViewById(R.id.tvNotificationTime);
            viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
        }

        void bind(NotificationItem notification) {
            tvNotificationTitle.setText(notification.getTitle());
            tvNotificationMessage.setText(notification.getMessage());
            tvNotificationTime.setText(notification.getTimestamp());
            ivNotificationIcon.setImageResource(notification.getIconResId());

            // Show/hide unread indicator
            viewUnreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

            // Slightly different background for unread
            cardNotification.setAlpha(notification.isRead() ? 0.85f : 1.0f);

            // Click listener
            cardNotification.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification);
                }
            });
        }
    }
}
