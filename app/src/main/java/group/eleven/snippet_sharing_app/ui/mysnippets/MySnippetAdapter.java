package group.eleven.snippet_sharing_app.ui.mysnippets;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.model.SnippetModel;

public class MySnippetAdapter extends RecyclerView.Adapter<MySnippetAdapter.SnippetViewHolder> {

    private List<SnippetModel> snippets = new ArrayList<>();
    private final OnSnippetActionListener listener;

    public interface OnSnippetActionListener {
        void onSnippetClick(SnippetModel snippet);

        void onFavoriteClick(SnippetModel snippet);

        void onEditClick(SnippetModel snippet);

        void onDeleteClick(SnippetModel snippet);

        void onShareClick(SnippetModel snippet);
    }

    public MySnippetAdapter(OnSnippetActionListener listener) {
        this.listener = listener;
    }

    public void setSnippets(List<SnippetModel> snippets) {
        this.snippets = snippets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SnippetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_snippet, parent, false);
        return new SnippetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SnippetViewHolder holder, int position) {
        holder.bind(snippets.get(position));
    }

    @Override
    public int getItemCount() {
        return snippets.size();
    }

    class SnippetViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvLanguage, tvVersion;
        ImageView ivSelection, ivPrivacy, btnEdit, btnDelete, btnShare, btnFavorite;

        public SnippetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvSnippetTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLanguage = itemView.findViewById(R.id.tvLanguageTag);
            tvVersion = itemView.findViewById(R.id.tvVersionTag);
            ivSelection = itemView.findViewById(R.id.ivSelection);
            ivPrivacy = itemView.findViewById(R.id.ivPrivacy);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

            // Click Listeners
            View.OnClickListener selectListener = v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION)
                    listener.onSnippetClick(snippets.get(pos));
            };
            ivSelection.setOnClickListener(selectListener);
            itemView.setOnClickListener(selectListener);

            btnFavorite.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION)
                    listener.onFavoriteClick(snippets.get(pos));
            });

            btnEdit.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION)
                    listener.onEditClick(snippets.get(pos));
            });

            btnDelete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION)
                    listener.onDeleteClick(snippets.get(pos));
            });

            btnShare.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION)
                    listener.onShareClick(snippets.get(pos));
            });
        }

        public void bind(SnippetModel snippet) {
            tvTitle.setText(snippet.getTitle());
            tvTime.setText(snippet.getLastModifiedTime());
            tvLanguage.setText(snippet.getLanguage());
            tvVersion.setText(snippet.getVersion());

            // Language Tag Color
            // Set dynamic text color
            try {
                tvLanguage.setTextColor(Color.parseColor(snippet.getLanguageColor()));
            } catch (Exception e) {
                tvLanguage.setTextColor(Color.WHITE);
            }

            // Privacy Icon
            switch (snippet.getPrivacy()) {
                case "Public":
                    ivPrivacy.setImageResource(R.drawable.ic_globe);
                    ivPrivacy.setColorFilter(Color.parseColor("#3DD68C")); // Green
                    break;
                case "Private":
                    ivPrivacy.setImageResource(R.drawable.ic_lock); // Assuming lock icon exists or using globe as
                                                                    // placeholder for now if lock missing. I saw
                                                                    // ic_lock exists.
                    ivPrivacy.setColorFilter(Color.parseColor("#889990")); // Grey
                    break;
                case "Team":
                    ivPrivacy.setImageResource(R.drawable.ic_users);
                    ivPrivacy.setColorFilter(Color.parseColor("#29B6F6")); // Blue-ish for Team? Or just Grey. Detailed
                                                                           // prompt says "Globe/Green for Public,
                                                                           // Lock/Grey for Private". Doesn't specify
                                                                           // Team color, I'll use a distinct one or
                                                                           // Grey.
                    break;
            }

            // Selection State
            if (snippet.isSelected()) {
                ivSelection.setImageResource(R.drawable.ic_check_circle);
                ivSelection.setColorFilter(Color.parseColor("#3DD68C"));
            } else {
                ivSelection.setImageResource(R.drawable.ic_radio_unchecked);
                ivSelection.setColorFilter(Color.parseColor("#889990"));
            }

            // Favorite State
            if (snippet.isFavorite()) {
                btnFavorite.setImageResource(R.drawable.ic_star_filled);
                btnFavorite.setColorFilter(Color.parseColor("#F0E68C")); // Yellow
            } else {
                btnFavorite.setImageResource(R.drawable.ic_star_outline);
                btnFavorite.setColorFilter(Color.parseColor("#889990"));
            }
        }
    }
}
