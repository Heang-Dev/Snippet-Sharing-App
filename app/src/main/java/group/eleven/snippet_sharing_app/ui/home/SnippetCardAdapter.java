package group.eleven.snippet_sharing_app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;
import group.eleven.snippet_sharing_app.utils.SyntaxHighlighter;

/**
 * Adapter for snippet cards RecyclerView
 */
public class SnippetCardAdapter extends RecyclerView.Adapter<SnippetCardAdapter.ViewHolder> {

    private List<SnippetCard> snippets;
    private OnSnippetClickListener listener;

    // Map of language names to their abbreviations
    private static final Map<String, String> LANGUAGE_ABBREVIATIONS = new HashMap<>();
    // Map of language names to file extensions
    private static final Map<String, String> LANGUAGE_EXTENSIONS = new HashMap<>();

    static {
        LANGUAGE_ABBREVIATIONS.put("javascript", "JS");
        LANGUAGE_ABBREVIATIONS.put("typescript", "TS");
        LANGUAGE_ABBREVIATIONS.put("python", "Py");
        LANGUAGE_ABBREVIATIONS.put("java", "Java");
        LANGUAGE_ABBREVIATIONS.put("kotlin", "Kt");
        LANGUAGE_ABBREVIATIONS.put("swift", "Swft");
        LANGUAGE_ABBREVIATIONS.put("go", "Go");
        LANGUAGE_ABBREVIATIONS.put("rust", "Rs");
        LANGUAGE_ABBREVIATIONS.put("ruby", "Rb");
        LANGUAGE_ABBREVIATIONS.put("php", "PHP");
        LANGUAGE_ABBREVIATIONS.put("c#", "C#");
        LANGUAGE_ABBREVIATIONS.put("csharp", "C#");
        LANGUAGE_ABBREVIATIONS.put("c++", "C++");
        LANGUAGE_ABBREVIATIONS.put("cpp", "C++");
        LANGUAGE_ABBREVIATIONS.put("c", "C");
        LANGUAGE_ABBREVIATIONS.put("html", "HTML");
        LANGUAGE_ABBREVIATIONS.put("html/css", "HC");
        LANGUAGE_ABBREVIATIONS.put("css", "CSS");
        LANGUAGE_ABBREVIATIONS.put("sql", "SQL");
        LANGUAGE_ABBREVIATIONS.put("shell", "Sh");
        LANGUAGE_ABBREVIATIONS.put("bash", "Sh");
        LANGUAGE_ABBREVIATIONS.put("dart", "Dart");
        LANGUAGE_ABBREVIATIONS.put("react", "Rct");
        LANGUAGE_ABBREVIATIONS.put("vue", "Vue");

        // File extensions
        LANGUAGE_EXTENSIONS.put("javascript", ".js");
        LANGUAGE_EXTENSIONS.put("typescript", ".ts");
        LANGUAGE_EXTENSIONS.put("python", ".py");
        LANGUAGE_EXTENSIONS.put("java", ".java");
        LANGUAGE_EXTENSIONS.put("kotlin", ".kt");
        LANGUAGE_EXTENSIONS.put("swift", ".swift");
        LANGUAGE_EXTENSIONS.put("go", ".go");
        LANGUAGE_EXTENSIONS.put("rust", ".rs");
        LANGUAGE_EXTENSIONS.put("ruby", ".rb");
        LANGUAGE_EXTENSIONS.put("php", ".php");
        LANGUAGE_EXTENSIONS.put("c#", ".cs");
        LANGUAGE_EXTENSIONS.put("csharp", ".cs");
        LANGUAGE_EXTENSIONS.put("c++", ".cpp");
        LANGUAGE_EXTENSIONS.put("cpp", ".cpp");
        LANGUAGE_EXTENSIONS.put("c", ".c");
        LANGUAGE_EXTENSIONS.put("html", ".html");
        LANGUAGE_EXTENSIONS.put("html/css", ".html");
        LANGUAGE_EXTENSIONS.put("css", ".css");
        LANGUAGE_EXTENSIONS.put("sql", ".sql");
        LANGUAGE_EXTENSIONS.put("shell", ".sh");
        LANGUAGE_EXTENSIONS.put("bash", ".sh");
        LANGUAGE_EXTENSIONS.put("dart", ".dart");
        LANGUAGE_EXTENSIONS.put("react", ".jsx");
        LANGUAGE_EXTENSIONS.put("vue", ".vue");
    }

    public interface OnSnippetClickListener {
        void onSnippetClick(SnippetCard snippet);
    }

    public SnippetCardAdapter(List<SnippetCard> snippets) {
        this.snippets = snippets;
    }

    public void filterList(List<SnippetCard> filteredList) {
        this.snippets = filteredList;
        notifyDataSetChanged();
    }

    public void setOnSnippetClickListener(OnSnippetClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_snippet_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SnippetCard snippet = snippets.get(position);
        holder.bind(snippet, listener);
    }

    @Override
    public int getItemCount() {
        return snippets.size();
    }

    /**
     * Get abbreviated language name for badge display
     */
    private static String getLanguageAbbreviation(String language) {
        if (language == null || language.isEmpty()) {
            return "?";
        }
        String lowerLang = language.toLowerCase().trim();
        String abbrev = LANGUAGE_ABBREVIATIONS.get(lowerLang);
        if (abbrev != null) {
            return abbrev;
        }
        // Return first 2-4 characters if not found in map
        if (language.length() <= 4) {
            return language;
        }
        return language.substring(0, Math.min(4, language.length()));
    }

    /**
     * Get file extension for language
     */
    private static String getFileExtension(String language) {
        if (language == null || language.isEmpty()) {
            return ".txt";
        }
        String lowerLang = language.toLowerCase().trim();
        String ext = LANGUAGE_EXTENSIONS.get(lowerLang);
        return ext != null ? ext : ".txt";
    }

    /**
     * Generate a filename from snippet title and language
     */
    private static String generateFilename(String title, String language) {
        if (title == null || title.isEmpty()) {
            return "snippet" + getFileExtension(language);
        }
        // Convert title to filename-safe format
        String filename = title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
        if (filename.length() > 20) {
            filename = filename.substring(0, 20);
        }
        if (filename.isEmpty()) {
            filename = "snippet";
        }
        return filename + getFileExtension(language);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLanguageBadge;
        private final TextView tvSnippetTitle;
        private final TextView tvSnippetTime;
        private final TextView tvCode;
        private final TextView tvCodeFilename;
        private final TextView tvTag1;
        private final TextView tvTag2;
        private final CardView cardView;
        private final ImageButton btnShare;
        private final SyntaxHighlighter syntaxHighlighter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvLanguageBadge = itemView.findViewById(R.id.tvLanguageBadge);
            tvSnippetTitle = itemView.findViewById(R.id.tvSnippetTitle);
            tvSnippetTime = itemView.findViewById(R.id.tvSnippetTime);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvCodeFilename = itemView.findViewById(R.id.tvCodeFilename);
            tvTag1 = itemView.findViewById(R.id.tvTag1);
            tvTag2 = itemView.findViewById(R.id.tvTag2);
            btnShare = itemView.findViewById(R.id.btnShare);
            syntaxHighlighter = new SyntaxHighlighter(itemView.getContext());
        }

        public void bind(SnippetCard snippet, OnSnippetClickListener listener) {
            // Show abbreviated language name in badge
            tvLanguageBadge.setText(getLanguageAbbreviation(snippet.getLanguageBadge()));
            tvSnippetTitle.setText(snippet.getTitle());
            tvSnippetTime.setText(snippet.getUpdatedTime());

            // Set filename in code header
            if (tvCodeFilename != null) {
                tvCodeFilename.setText(generateFilename(snippet.getTitle(), snippet.getLanguageBadge()));
            }

            // Display code preview with syntax highlighting
            if (tvCode != null) {
                String code = snippet.getCodePreview();
                if (code != null && !code.isEmpty()) {
                    SpannableString highlightedCode = syntaxHighlighter.highlightForLanguage(
                            code, snippet.getLanguageBadge());
                    tvCode.setText(highlightedCode);
                } else {
                    tvCode.setText("// No code preview");
                }
            }

            // Set tags
            String[] tags = snippet.getTags();
            if (tags != null && tags.length > 0) {
                if (tvTag1 != null) {
                    tvTag1.setText(tags[0]);
                    tvTag1.setVisibility(View.VISIBLE);
                }
                if (tvTag2 != null && tags.length > 1) {
                    tvTag2.setText(tags[1]);
                    tvTag2.setVisibility(View.VISIBLE);
                } else if (tvTag2 != null) {
                    tvTag2.setVisibility(View.GONE);
                }
            } else {
                if (tvTag1 != null) tvTag1.setVisibility(View.GONE);
                if (tvTag2 != null) tvTag2.setVisibility(View.GONE);
            }

            if (btnShare != null) {
                btnShare.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, snippet.getTitle());
                    String shareText = "Check out this " + snippet.getLanguageBadge() + " code snippet: "
                            + snippet.getTitle() + "\n\n" + snippet.getCodePreview();
                    intent.putExtra(Intent.EXTRA_TEXT, shareText);
                    v.getContext().startActivity(Intent.createChooser(intent, "Share Snippet via"));
                });
            }

            if (listener != null) {
                cardView.setOnClickListener(v -> listener.onSnippetClick(snippet));
            }
        }
    }
}
