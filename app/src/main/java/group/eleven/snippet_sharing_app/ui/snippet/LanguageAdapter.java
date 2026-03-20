package group.eleven.snippet_sharing_app.ui.snippet;

import android.content.Context;
import android.graphics.Color;
import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.utils.ThemeManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.model.Language;

public class LanguageAdapter extends BaseAdapter {

    private Context context;
    private List<Language> originalList;
    private List<Language> filteredList;

    public LanguageAdapter(Context context, List<Language> languages) {
        this.context = context;
        this.originalList = languages;
        this.filteredList = new ArrayList<>(languages);
    }

    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            for (Language lang : originalList) {
                if (lang.getName().toLowerCase().contains(query.toLowerCase()) ||
                        lang.getShortCode().toLowerCase().contains(query.toLowerCase()) ||
                        lang.getMime().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(lang);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Language getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_language, parent, false);
        }

        Language language = getItem(position);

        TextView tvIcon = convertView.findViewById(R.id.tvLangIcon);
        TextView tvName = convertView.findViewById(R.id.tvLangName);
        TextView tvMime = convertView.findViewById(R.id.tvLangMime);
        ImageView ivCheck = convertView.findViewById(R.id.ivCheck);

        tvIcon.setText(language.getShortCode());
        try {
            tvIcon.setTextColor(Color.parseColor(language.getColorHex()));
        } catch (IllegalArgumentException e) {
            tvIcon.setTextColor(Color.WHITE); // Default fallback
        }
        tvName.setText(language.getName());
        tvMime.setText(language.getMime());

        if (language.isSelected()) {
            ivCheck.setVisibility(View.VISIBLE);
            convertView.setBackgroundResource(R.drawable.bg_badge_draft);
            convertView
                    .setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                            androidx.core.graphics.ColorUtils.setAlphaComponent(
                                    ThemeManager.getThemeColor(convertView.getContext(), R.attr.accentColor), 38)));
        } else {
            ivCheck.setVisibility(View.GONE);
            convertView.setBackground(null); // Or transparent
        }

        return convertView;
    }
}
