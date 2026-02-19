package group.eleven.snippet_sharing_app.ui.tags;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.model.Tag;

public class SuggestionTagsAdapter extends ListAdapter<Tag, SuggestionTagsAdapter.ViewHolder> {

    private final OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(Tag tag);
    }

    public SuggestionTagsAdapter(OnTagClickListener listener) {
        super(new DiffUtil.ItemCallback<Tag>() {
            @Override
            public boolean areItemsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
                return oldItem.getName().equals(newItem.getName());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
                return oldItem.getName().equals(newItem.getName())
                        && oldItem.getSnippetCount() == newItem.getSnippetCount();
            }
        });
        this.listener = listener;
    }

    public void setTags(List<Tag> tags) {
        submitList(tags != null ? new ArrayList<>(tags) : null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = getItem(position);
        holder.bind(tag);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagName;
        TextView tvSnippetCount;
        TextView tvHashtag;

        ViewHolder(View itemView) {
            super(itemView);
            tvTagName = itemView.findViewById(R.id.tvTagName);
            tvSnippetCount = itemView.findViewById(R.id.tvSnippetCount);
            tvHashtag = itemView.findViewById(R.id.tvHashtag);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTagClick(getItem(position));
                }
            });
        }

        void bind(Tag tag) {
            if (tag.getSnippetCount() == -1) {
                // Create new tag mode
                tvTagName.setText("Create \"" + tag.getName() + "\"");
                tvSnippetCount.setVisibility(View.GONE);
                tvHashtag.setText("+");
            } else {
                tvTagName.setText(tag.getName());
                tvSnippetCount.setVisibility(View.VISIBLE);
                tvSnippetCount.setText(tag.getSnippetCount() + " snippets");
                tvHashtag.setText("#");
            }
        }
    }
}
