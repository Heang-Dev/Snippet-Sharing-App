package group.eleven.snippet_sharing_app.ui.team;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.ui.team.settings.TeamSettingsDangerZoneFragment;
import group.eleven.snippet_sharing_app.ui.team.settings.TeamSettingsGeneralFragment;
import group.eleven.snippet_sharing_app.ui.team.settings.TeamSettingsMembersFragment;

public class TeamSettingsActivity extends AppCompatActivity {

    public static final String EXTRA_TEAM_ID = "extra_team_id";
    public static final String EXTRA_TAB_INDEX = "extra_tab_index";

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private String teamId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_settings);

        teamId = getIntent().getStringExtra(EXTRA_TEAM_ID);
        if (teamId == null) {
            Toast.makeText(this, "Team ID is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupViewPagerAndTabs();

        int tabIndex = getIntent().getIntExtra(EXTRA_TAB_INDEX, 0);
        if (tabIndex > 0) {
            viewPager.setCurrentItem(tabIndex);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Team Settings");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupViewPagerAndTabs() {
        TeamSettingsPagerAdapter pagerAdapter = new TeamSettingsPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("General"); break;
                        case 1: tab.setText("Members"); break;
                        case 2: tab.setText("Danger Zone"); break;
                    }
                }).attach();
    }

    public String getTeamId() {
        return teamId;
    }

    private static class TeamSettingsPagerAdapter extends FragmentStateAdapter {

        private static final int NUM_TABS = 3;

        public TeamSettingsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new TeamSettingsGeneralFragment();
                case 1: return new TeamSettingsMembersFragment();
                case 2: return new TeamSettingsDangerZoneFragment();
                default: return new TeamSettingsGeneralFragment();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_TABS;
        }
    }
}
