package group.eleven.snippet_sharing_app.ui.category;

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
import group.eleven.snippet_sharing_app.model.CategoryModel;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryModel> categories;
    private String selectedCategoryId;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryModel category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.categories = new ArrayList<>();
        this.listener = listener;
    }

    public void setCategories(List<CategoryModel> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setSelectedCategoryId(String id) {
        this.selectedCategoryId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_row, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvSnippetCount;
        ImageView ivIcon, ivCheck;
        View clRoot;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvSnippetCount = itemView.findViewById(R.id.tvSnippetCount);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            clRoot = itemView.findViewById(R.id.clRoot);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(categories.get(getAdapterPosition()));
                }
            });
        }

        public void bind(CategoryModel category) {
            tvCategoryName.setText(category.getName());
            tvSnippetCount.setText(category.getSnippetCount() + " snippets");

            if (category.getIconResId() != 0) {
                ivIcon.setImageResource(category.getIconResId());
            } else {
                ivIcon.setImageResource(R.drawable.ic_folder);
            }

            boolean isSelected = category.getId().equals(selectedCategoryId);

            if (isSelected) {
                clRoot.setBackgroundResource(R.drawable.bg_category_selected);
                ivCheck.setVisibility(View.VISIBLE);

                // Ensure text is visible/correct color on selected bg
                // Assuming white text works on both backgrounds
            } else {
                clRoot.setBackgroundResource(0); // Transparent
                ivCheck.setVisibility(View.GONE);
            }
        }
    }
}
