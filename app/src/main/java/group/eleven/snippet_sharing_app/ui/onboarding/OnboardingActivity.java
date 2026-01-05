package group.eleven.snippet_sharing_app.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.databinding.ActivityOnboardingBinding;
import group.eleven.snippet_sharing_app.ui.auth.LoginActivity;

public class OnboardingActivity extends AppCompatActivity {

    private static final String PREF_NAME = "onboarding_pref";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";

    private ActivityOnboardingBinding binding;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if onboarding already completed
        if (isOnboardingCompleted()) {
            navigateToLogin();
            return;
        }

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupOnboardingItems();
        setupIndicators();
        setupListeners();
    }

    private void setupOnboardingItems() {
        adapter = new OnboardingAdapter(getOnboardingItems());
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
                updateButtonText(position);
            }
        });
    }

    private OnboardingItem[] getOnboardingItems() {
        return new OnboardingItem[]{
                new OnboardingItem(
                        R.drawable.ic_onboarding_share,
                        getString(R.string.onboarding_title_1),
                        getString(R.string.onboarding_desc_1)
                ),
                new OnboardingItem(
                        R.drawable.ic_onboarding_discover,
                        getString(R.string.onboarding_title_2),
                        getString(R.string.onboarding_desc_2)
                ),
                new OnboardingItem(
                        R.drawable.ic_onboarding_organize,
                        getString(R.string.onboarding_title_3),
                        getString(R.string.onboarding_desc_3)
                )
        };
    }

    private void setupIndicators() {
        int count = adapter.getItemCount();
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            binding.indicatorContainer.addView(indicators[i]);
        }

        // Set first indicator as active
        updateIndicators(0);
    }

    private void updateIndicators(int position) {
        int childCount = binding.indicatorContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView indicator = (ImageView) binding.indicatorContainer.getChildAt(i);
            if (i == position) {
                indicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active));
            } else {
                indicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            }
        }
    }

    private void updateButtonText(int position) {
        if (position == adapter.getItemCount() - 1) {
            binding.btnNext.setText(R.string.onboarding_get_started);
        } else {
            binding.btnNext.setText(R.string.onboarding_next);
        }
    }

    private void setupListeners() {
        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current < adapter.getItemCount() - 1) {
                binding.viewPager.setCurrentItem(current + 1);
            } else {
                completeOnboarding();
            }
        });

        binding.tvSkip.setOnClickListener(v -> completeOnboarding());
    }

    private void completeOnboarding() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply();
        navigateToLogin();
    }

    private boolean isOnboardingCompleted() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
