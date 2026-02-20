package group.eleven.snippet_sharing_app.ui.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.NotificationItem;

public class NotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;

    private NotificationAdapter adapter;
    private List<NotificationItem> allNotifications = new ArrayList<>();
    private boolean showUnreadOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        setupStatusBar();
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupTabs();
        loadMockNotifications();
    }

    private void setupStatusBar() {
        android.view.Window window = getWindow();
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(R.attr.appBackgroundColor, typedValue, true);
        int backgroundColor = typedValue.data;
        window.setStatusBarColor(backgroundColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            boolean isLightBackground = isColorLight(backgroundColor);
            controller.setAppearanceLightStatusBars(isLightBackground);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color)
                + 0.587 * android.graphics.Color.green(color)
                + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutEmpty);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(this);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showUnreadOnly = tab.getPosition() == 1;
                filterNotifications();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterNotifications() {
        List<NotificationItem> filtered;
        if (showUnreadOnly) {
            filtered = allNotifications.stream()
                    .filter(n -> !n.isRead())
                    .collect(Collectors.toList());
        } else {
            filtered = new ArrayList<>(allNotifications);
        }

        adapter.setNotifications(filtered);
        updateEmptyState(filtered.isEmpty());
    }

    private void updateEmptyState(boolean isEmpty) {
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvNotifications.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void loadMockNotifications() {
        allNotifications = new ArrayList<>();

        // Add mock notifications
        allNotifications.add(new NotificationItem(
                "1",
                NotificationItem.TYPE_FOLLOW,
                "New follower",
                "Sarah Chen started following you",
                "Just now",
                false,
                "Sarah Chen"
        ));

        allNotifications.add(new NotificationItem(
                "2",
                NotificationItem.TYPE_COMMENT,
                "New comment",
                "Mike Johnson commented on your snippet 'Python CSV Parser'",
                "5 min ago",
                false,
                "Mike Johnson"
        ));

        allNotifications.add(new NotificationItem(
                "3",
                NotificationItem.TYPE_FORK,
                "Snippet forked",
                "Alex Rivera forked your snippet 'React Hook Utils'",
                "1 hour ago",
                false,
                "Alex Rivera"
        ));

        allNotifications.add(new NotificationItem(
                "4",
                NotificationItem.TYPE_TEAM_INVITE,
                "Team invitation",
                "You've been invited to join 'Frontend Masters' team",
                "2 hours ago",
                true,
                "Frontend Masters"
        ));

        allNotifications.add(new NotificationItem(
                "5",
                NotificationItem.TYPE_LIKE,
                "New star",
                "Emma Wilson starred your snippet 'Bash Init Script'",
                "3 hours ago",
                true,
                "Emma Wilson"
        ));

        allNotifications.add(new NotificationItem(
                "6",
                NotificationItem.TYPE_MENTION,
                "Mentioned you",
                "David Lee mentioned you in a comment",
                "Yesterday",
                true,
                "David Lee"
        ));

        allNotifications.add(new NotificationItem(
                "7",
                NotificationItem.TYPE_COMMENT,
                "New comment",
                "Lisa Park replied to your comment",
                "Yesterday",
                true,
                "Lisa Park"
        ));

        allNotifications.add(new NotificationItem(
                "8",
                NotificationItem.TYPE_FOLLOW,
                "New follower",
                "James Wong started following you",
                "2 days ago",
                true,
                "James Wong"
        ));

        filterNotifications();
    }

    @Override
    public void onNotificationClick(NotificationItem notification) {
        // Mark as read
        notification.setRead(true);
        adapter.notifyDataSetChanged();

        // Handle notification click based on type
        switch (notification.getType()) {
            case NotificationItem.TYPE_FOLLOW:
                Toast.makeText(this, "View " + notification.getActorName() + "'s profile", Toast.LENGTH_SHORT).show();
                break;
            case NotificationItem.TYPE_COMMENT:
            case NotificationItem.TYPE_MENTION:
                Toast.makeText(this, "View comment", Toast.LENGTH_SHORT).show();
                break;
            case NotificationItem.TYPE_FORK:
            case NotificationItem.TYPE_LIKE:
                Toast.makeText(this, "View snippet", Toast.LENGTH_SHORT).show();
                break;
            case NotificationItem.TYPE_TEAM_INVITE:
                Toast.makeText(this, "View team invitation", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
