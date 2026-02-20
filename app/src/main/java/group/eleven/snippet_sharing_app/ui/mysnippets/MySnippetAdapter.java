package group.eleven.snippet_sharing_app.ui.mysnippets;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
        ImageView ivPrivacy, btnEdit, btnDelete, btnShare, btnFavorite;
        Context context;

        public SnippetViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvTitle = itemView.findViewById(R.id.tvSnippetTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLanguage = itemView.findViewById(R.id.tvLanguageTag);
            tvVersion = itemView.findViewById(R.id.tvVersionTag);
            ivPrivacy = itemView.findViewById(R.id.ivPrivacy);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

            // Item click
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION)
                    listener.onSnippetClick(snippets.get(pos));
            });

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
            try {
                tvLanguage.setTextColor(Color.parseColor(snippet.getLanguageColor()));
            } catch (Exception e) {
                tvLanguage.setTextColor(getThemeColor(context, R.attr.textPrimaryColor));
            }

            // Privacy Icon - use theme-aware accent color
            int accentColor = getThemeColor(context, R.attr.accentColor);
            int secondaryColor = getThemeColor(context, R.attr.textSecondaryColor);
            int infoColor = ContextCompat.getColor(context, R.color.info);

            switch (snippet.getPrivacy()) {
                case "Public":
                    ivPrivacy.setImageResource(R.drawable.ic_globe);
                    ivPrivacy.setColorFilter(accentColor);
                    break;
                case "Private":
                    ivPrivacy.setImageResource(R.drawable.ic_lock);
                    ivPrivacy.setColorFilter(secondaryColor);
                    break;
                case "Team":
                    ivPrivacy.setImageResource(R.drawable.ic_users);
                    ivPrivacy.setColorFilter(infoColor);
                    break;
                default:
                    ivPrivacy.setImageResource(R.drawable.ic_globe);
                    ivPrivacy.setColorFilter(accentColor);
                    break;
            }

            // Favorite State
            int warningColor = ContextCompat.getColor(context, R.color.warning);
            if (snippet.isFavorite()) {
                btnFavorite.setImageResource(R.drawable.ic_star_filled);
                btnFavorite.setColorFilter(warningColor);
            } else {
                btnFavorite.setImageResource(R.drawable.ic_star_outline);
                btnFavorite.setColorFilter(secondaryColor);
            }
        }

        private int getThemeColor(Context context, int attr) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(attr, typedValue, true);
            if (typedValue.resourceId != 0) {
                return ContextCompat.getColor(context, typedValue.resourceId);
            }
            return typedValue.data;
        }
    }
}
