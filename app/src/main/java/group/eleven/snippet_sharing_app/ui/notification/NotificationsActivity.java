package group.eleven.snippet_sharing_app.ui.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.NotificationItem;
import group.eleven.snippet_sharing_app.data.repository.NotificationRepository;
import group.eleven.snippet_sharing_app.utils.Resource;

public class NotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private static final String TAG = "NotificationsActivity";

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;
    private SwipeRefreshLayout swipeRefresh;

    private NotificationAdapter adapter;
    private NotificationRepository notificationRepository;
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
        setupSwipeRefresh();
        loadNotifications();
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
        notificationRepository = new NotificationRepository(this);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        swipeRefresh = findViewById(R.id.swipeRefresh);
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

    private void setupSwipeRefresh() {
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.primary);
            swipeRefresh.setOnRefreshListener(this::loadNotifications);
        }
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

    private void showLoading(boolean show) {
        // Loading state handled by swipeRefresh
        if (swipeRefresh != null && !show) {
            swipeRefresh.setRefreshing(false);
        }
    }

    /**
     * Load notifications from API
     */
    private void loadNotifications() {
        showLoading(true);

        notificationRepository.getNotifications(30).observe(this, resource -> {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            showLoading(false);

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allNotifications.clear();
                allNotifications.addAll(resource.data);
                filterNotifications();
                Log.d(TAG, "Loaded " + allNotifications.size() + " notifications from API");
            } else if (resource.status == Resource.Status.ERROR) {
                Log.e(TAG, "Failed to load notifications: " + resource.message);
                // Show empty state when API fails
                allNotifications.clear();
                filterNotifications();
                Toast.makeText(this, "Unable to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNotificationClick(NotificationItem notification) {
        // Mark as read via API
        markNotificationAsRead(notification);

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

    /**
     * Mark notification as read via API
     */
    private void markNotificationAsRead(NotificationItem notification) {
        if (notification.isRead()) return;

        // Optimistically update UI
        notification.setRead(true);
        adapter.notifyDataSetChanged();

        // Call API to mark as read
        notificationRepository.markAsRead(notification.getId()).observe(this, resource -> {
            if (resource.status == Resource.Status.ERROR) {
                Log.w(TAG, "Failed to mark notification as read: " + resource.message);
                // Revert UI on error
                notification.setRead(false);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
