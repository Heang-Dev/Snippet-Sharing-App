package group.eleven.snippet_sharing_app.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import group.eleven.snippet_sharing_app.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final OnboardingItem[] items;

    public OnboardingAdapter(OnboardingItem[] items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivIllustration;
        private final TextView tvTitle;
        private final TextView tvDescription;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIllustration = itemView.findViewById(R.id.ivIllustration);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        void bind(OnboardingItem item) {
            ivIllustration.setImageResource(item.getImageRes());
            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
        }
    }
}
