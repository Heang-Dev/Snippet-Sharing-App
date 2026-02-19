package group.eleven.snippet_sharing_app.ui.search;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.model.SearchResult;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<SearchResult> items = new ArrayList<>();

    public void setItems(List<SearchResult> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLanguageInitial, tvTitle, tvSubtitle, tvCode, tvUsername, tvStars, tvForks;
        CardView cvLanguage;
        ImageView ivBookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguageInitial = itemView.findViewById(R.id.tvLanguageInitial);
            cvLanguage = itemView.findViewById(R.id.cvLanguage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvStars = itemView.findViewById(R.id.tvStars);
            tvForks = itemView.findViewById(R.id.tvForks);
            ivBookmark = itemView.findViewById(R.id.ivBookmark);
        }

        public void bind(SearchResult item) {
            tvLanguageInitial
                    .setText(item.getLanguage().substring(0, Math.min(2, item.getLanguage().length())).toUpperCase());
            try {
                cvLanguage.setCardBackgroundColor(Color.parseColor(item.getLanguageColor()));
            } catch (Exception e) {
                cvLanguage.setCardBackgroundColor(Color.GRAY);
            }

            tvTitle.setText(item.getTitle());
            tvSubtitle.setText(item.getSubtitle());
            tvCode.setText(item.getCodeSnippet());
            tvUsername.setText(item.getUsername() + " • " + item.getTimestamp());
            tvStars.setText(String.valueOf(item.getStars()));
            tvForks.setText(String.valueOf(item.getForks()));

            // Bookmark state?
            // item doesn't have isBookmarked state per prompt, so just default grey or
            // tint.
            ivBookmark.setColorFilter(Color.parseColor("#889990"));

            // Private lock logic?
            if (item.isPrivate()) {
                // Add lock icon or similar. The prompt mentioned "Lock icon" for OAuth2.
                // Maybe add to subtitle or replace Bookmark?
                // The prompt 2nd Mock Item: "User: @alex_code, Private Lock icon".
                // Assuming next to user or subtitle.
                // I'll append "🔒" to subtitle or title if private.
                if (!item.getSubtitle().contains("Private")) {
                    tvSubtitle.setText(item.getSubtitle() + " • Private 🔒");
                }
            }
        }
    }
}
