package group.eleven.snippet_sharing_app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.MockDataProvider;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.data.model.User;
import group.eleven.snippet_sharing_app.ui.home.FeedSnippetAdapter;
import group.eleven.snippet_sharing_app.ui.notification.NotificationsActivity;
import group.eleven.snippet_sharing_app.utils.SessionManager;

/**
 * Profile Activity - Displays user profile with stats and snippets
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // Views
    private Toolbar toolbar;
    private ImageView ivSettings;
    private CircleImageView ivProfile;
    private FrameLayout flProfileImage;
    private View vStatusIndicator;
    private TextView tvName, tvUsername, tvBio;
    private LinearLayout llBadges;
    private TextView tvBadgeVerified;
    private TextView tvJoinedDate, tvLocation, tvWebsite;
    private LinearLayout llSocialLinks;
    private TextView tvSnippetsCount, tvFollowersCount, tvFollowingCount, tvLikesCount;
    private TextView tabSnippets, tabStars, tabCollections;
    private RecyclerView rvContent;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyTitle, tvEmptyMessage;
    private FrameLayout layoutLoading;

    // Data
    private SessionManager sessionManager;
    private FeedSnippetAdapter adapter;
    private String currentTab = "snippets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        initViews();
        setupToolbar();
        setupClickListeners();
        setupRecyclerView();
        setupTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
        loadContent(currentTab);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivSettings = findViewById(R.id.ivSettings);
        ivProfile = findViewById(R.id.ivProfile);
        flProfileImage = findViewById(R.id.flProfileImage);
        vStatusIndicator = findViewById(R.id.vStatusIndicator);
        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        tvBio = findViewById(R.id.tvBio);
        llBadges = findViewById(R.id.llBadges);
        tvBadgeVerified = findViewById(R.id.tvBadgeVerified);
        tvJoinedDate = findViewById(R.id.tvJoinedDate);
        tvLocation = findViewById(R.id.tvLocation);
        tvWebsite = findViewById(R.id.tvWebsite);
        llSocialLinks = findViewById(R.id.llSocialLinks);
        tvSnippetsCount = findViewById(R.id.tvSnippetsCount);
        tvFollowersCount = findViewById(R.id.tvFollowersCount);
        tvFollowingCount = findViewById(R.id.tvFollowingCount);
        tvLikesCount = findViewById(R.id.tvLikesCount);
        tabSnippets = findViewById(R.id.tabSnippets);
        tabStars = findViewById(R.id.tabStars);
        tabCollections = findViewById(R.id.tabCollections);
        rvContent = findViewById(R.id.rvContent);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        layoutLoading = findViewById(R.id.layoutLoading);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        toolbar.setNavigationOnClickListener(v -> navigateBackToHome());
    }

    private void navigateBackToHome() {
        Intent intent = new Intent(this, group.eleven.snippet_sharing_app.ui.home.HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        // Settings
        ivSettings.setOnClickListener(v ->
                startActivity(new Intent(this, AccountSettingsActivity.class)));

        // Edit Profile
        findViewById(R.id.btnEditProfile).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        // Profile image tap -> Edit Profile
        flProfileImage.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        // Notifications
        findViewById(R.id.cvNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        // Social links (if present)
        View cvGithub = findViewById(R.id.cvGithub);
        View cvTwitter = findViewById(R.id.cvTwitter);
        View cvWebsiteLink = findViewById(R.id.cvWebsiteLink);

        if (cvGithub != null) {
            cvGithub.setOnClickListener(v -> openSocialLink("github"));
        }
        if (cvTwitter != null) {
            cvTwitter.setOnClickListener(v -> openSocialLink("twitter"));
        }
        if (cvWebsiteLink != null) {
            cvWebsiteLink.setOnClickListener(v -> openSocialLink("website"));
        }
    }

    private void setupRecyclerView() {
        adapter = new FeedSnippetAdapter(new ArrayList<>());
        adapter.setOnFeedItemClickListener(new FeedSnippetAdapter.OnFeedItemClickListener() {
            @Override
            public void onSnippetClick(SnippetCard snippet) {
                // TODO: Navigate to snippet detail
            }

            @Override
            public void onLikeClick(SnippetCard snippet, int position) {
                boolean newLikeState = !snippet.isLiked();
                int newCount = snippet.getLikesCount() + (newLikeState ? 1 : -1);
                adapter.updateLikeState(position, newLikeState, newCount);
            }

            @Override
            public void onCommentClick(SnippetCard snippet) {
                // TODO: Show comments
            }

            @Override
            public void onShareClick(SnippetCard snippet) {
                shareSnippet(snippet);
            }

            @Override
            public void onAuthorClick(SnippetCard snippet) {
                // Already on profile, do nothing
            }

            @Override
            public void onMoreOptionsClick(SnippetCard snippet, View anchor) {
                // TODO: Show options menu
            }
        });
        rvContent.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setAdapter(adapter);
        rvContent.setNestedScrollingEnabled(false);
    }

    private void setupTabs() {
        tabSnippets.setOnClickListener(v -> {
            currentTab = "snippets";
            updateTabUI(tabSnippets);
            loadContent("snippets");
        });

        tabStars.setOnClickListener(v -> {
            currentTab = "stars";
            updateTabUI(tabStars);
            loadContent("stars");
        });

        tabCollections.setOnClickListener(v -> {
            currentTab = "collections";
            updateTabUI(tabCollections);
            loadContent("collections");
        });
    }

    private void loadProfileData() {
        User user = sessionManager.getUser();

        if (user != null) {
            // Name
            String displayName = user.getFullName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = user.getUsername();
            }
            tvName.setText(displayName != null ? displayName : "User");

            // Username
            String username = user.getUsername();
            tvUsername.setText(username != null ? "@" + username : "");

            // Bio
            String bio = user.getBio();
            if (bio != null && !bio.isEmpty()) {
                tvBio.setText(bio);
                tvBio.setVisibility(View.VISIBLE);
            } else {
                tvBio.setVisibility(View.GONE);
            }

            // Avatar
            String avatarUrl = user.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this)
                        .load(avatarUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(ivProfile);
            }

            // Joined date
            String createdAt = user.getCreatedAt();
            if (createdAt != null && !createdAt.isEmpty()) {
                tvJoinedDate.setText("Joined " + formatJoinDate(createdAt));
            } else {
                tvJoinedDate.setText("Joined recently");
            }

            // Website
            String websiteUrl = user.getWebsiteUrl();
            if (websiteUrl != null && !websiteUrl.isEmpty()) {
                tvWebsite.setText(formatWebsiteDisplay(websiteUrl));
                tvWebsite.setVisibility(View.VISIBLE);
            } else {
                tvWebsite.setVisibility(View.GONE);
            }

            // Location (not in current User model, hide for now)
            tvLocation.setVisibility(View.GONE);

            // Social links
            String githubUrl = user.getGithubUrl();
            String twitterUrl = user.getTwitterUrl();
            if ((githubUrl != null && !githubUrl.isEmpty()) ||
                    (twitterUrl != null && !twitterUrl.isEmpty()) ||
                    (websiteUrl != null && !websiteUrl.isEmpty())) {
                llSocialLinks.setVisibility(View.VISIBLE);
            } else {
                llSocialLinks.setVisibility(View.GONE);
            }

            // Badges (show verified if email verified)
            if (user.isEmailVerified()) {
                llBadges.setVisibility(View.VISIBLE);
                tvBadgeVerified.setText("Verified");
            } else {
                llBadges.setVisibility(View.GONE);
            }
        } else {
            // Fallback to mock data
            tvName.setText("Guest User");
            tvUsername.setText("@guest");
            tvBio.setVisibility(View.GONE);
            tvJoinedDate.setText("Joined recently");
            tvLocation.setVisibility(View.GONE);
            tvWebsite.setVisibility(View.GONE);
            llSocialLinks.setVisibility(View.GONE);
            llBadges.setVisibility(View.GONE);
        }

        // Load stats (mock data for now - would come from API)
        loadMockStats();
    }

    private void loadMockStats() {
        // Generate realistic mock stats
        Random random = new Random();
        int snippets = random.nextInt(50) + 5;
        int followers = random.nextInt(500) + 10;
        int following = random.nextInt(200) + 5;
        int likes = random.nextInt(1000) + 50;

        tvSnippetsCount.setText(String.valueOf(snippets));
        tvFollowersCount.setText(formatCount(followers));
        tvFollowingCount.setText(String.valueOf(following));
        tvLikesCount.setText(formatCount(likes));
    }

    private void loadContent(String type) {
        List<SnippetCard> snippets;

        switch (type) {
            case "stars":
                snippets = MockDataProvider.getMockSnippetCards(3);
                tvEmptyTitle.setText("No starred snippets");
                tvEmptyMessage.setText("Star snippets to save them for later!");
                break;
            case "collections":
                snippets = MockDataProvider.getMockSnippetCards(2);
                tvEmptyTitle.setText("No collections yet");
                tvEmptyMessage.setText("Create collections to organize your snippets!");
                break;
            default: // snippets
                snippets = MockDataProvider.getMockSnippetCards(5);
                tvEmptyTitle.setText("No snippets yet");
                tvEmptyMessage.setText("Create your first snippet to share!");
                break;
        }

        adapter.setSnippets(snippets);

        if (snippets.isEmpty()) {
            rvContent.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvContent.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void updateTabUI(TextView selectedTab) {
        resetTabs();
        selectedTab.setBackgroundResource(R.drawable.bg_pill_badge);
        selectedTab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary));
        selectedTab.setTextColor(ContextCompat.getColor(this, R.color.white));
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void resetTabs() {
        TextView[] tabs = {tabSnippets, tabStars, tabCollections};

        // Get the theme color for muted text
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.textMutedColor, typedValue, true);
        int mutedColor = typedValue.data;

        for (TextView tab : tabs) {
            if (tab != null) {
                tab.setBackground(null);
                tab.setBackgroundTintList(null);
                tab.setTextColor(mutedColor);
                tab.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void shareSnippet(SnippetCard snippet) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, snippet.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                snippet.getTitle() + "\n\n" + snippet.getCodePreview() + "\n\nShared via Snippet G11");
        startActivity(Intent.createChooser(shareIntent, "Share snippet"));
    }

    private void openSocialLink(String type) {
        User user = sessionManager.getUser();
        if (user == null) return;

        String url = null;
        switch (type) {
            case "github":
                url = user.getGithubUrl();
                break;
            case "twitter":
                url = user.getTwitterUrl();
                break;
            case "website":
                url = user.getWebsiteUrl();
                break;
        }

        if (url != null && !url.isEmpty()) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
            startActivity(intent);
        }
    }

    private String formatJoinDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return "recently";
        }
    }

    private String formatWebsiteDisplay(String url) {
        if (url == null) return "";
        return url.replace("https://", "")
                .replace("http://", "")
                .replace("www.", "");
    }

    private String formatCount(int count) {
        if (count >= 1000000) {
            return String.format(Locale.getDefault(), "%.1fM", count / 1000000.0);
        } else if (count >= 1000) {
            return String.format(Locale.getDefault(), "%.1fk", count / 1000.0);
        }
        return String.valueOf(count);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        // Navigate back to Home instead of just finishing
        navigateBackToHome();
    }
}
