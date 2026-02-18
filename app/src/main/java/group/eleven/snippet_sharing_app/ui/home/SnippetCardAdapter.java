package group.eleven.snippet_sharing_app.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.SnippetCard;

/**
 * Adapter for snippet cards RecyclerView
 */
public class SnippetCardAdapter extends RecyclerView.Adapter<SnippetCardAdapter.ViewHolder> {

    private List<SnippetCard> snippets;
    private OnSnippetClickListener listener;

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

    private static CharSequence highlightCode(String code) {
        // 1. Escape HTML special characters
        String escaped = code.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

        // 2. VS Code Dark Theme Colors
        String colorKeyword = "#C586C0"; // Purple (import, public, void, return)
        String colorControl = "#C586C0"; // Purple (if, else, for, while)
        String colorType = "#4EC9B0"; // Teal (String, int, boolean, ClassName)
        String colorFunction = "#DCDCAA"; // Light Yellow (methodName)
        String colorString = "#CE9178"; // Orange/Brown ("text")
        String colorNumber = "#B5CEA8"; // Light Green (123)
        String colorComment = "#6A9955"; // Green (// comment)
        String colorNormal = "#D4D4D4"; // Grey/White (default text)

        // 3. Apply Coloring (Order is crucial!)
        // Comments (Handle first to avoid coloring keywords inside comments)
        // Note: This simple regex handles single line comments //...
        String colored = escaped.replaceAll("(//.*)", "<font color='" + colorComment + "'>$1</font>");

        // Keywords & Control Flow (Purple)
        // We use a negative lookahead (?![^<]*>) to avoid replacing inside HTML tags we
        // just added
        colored = colored.replaceAll(
                "\\b(public|private|protected|void|return|import|package|class|new|this|super|extends|implements)\\b(?![^<]*>)",
                "<font color='" + colorKeyword + "'>$1</font>");
        colored = colored.replaceAll("\\b(if|else|for|while|switch|case|break|continue|try|catch|finally)\\b(?![^<]*>)",
                "<font color='" + colorControl + "'>$1</font>");

        // Primitive Types (Teal)
        colored = colored.replaceAll("\\b(int|long|float|double|boolean|char|String|void)\\b(?![^<]*>)",
                "<font color='" + colorType + "'>$1</font>");

        // Strings (Orange) - Handle both " and '
        colored = colored.replaceAll("(\".*?\")(?![^<]*>)", "<font color='" + colorString + "'>$1</font>");
        colored = colored.replaceAll("('.*?')(?![^<]*>)", "<font color='" + colorString + "'>$1</font>");

        // Numbers (Light Green)
        colored = colored.replaceAll("\\b(\\d+)\\b(?![^<]*>)", "<font color='" + colorNumber + "'>$1</font>");

        // 4. Return HTML
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return android.text.Html.fromHtml(colored, android.text.Html.FROM_HTML_MODE_LEGACY);
        } else {
            return android.text.Html.fromHtml(colored);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLanguageBadge;
        private final TextView tvSnippetTitle;
        private final TextView tvSnippetTime;
        private final TextView tvCode;
        private final CardView cardView;
        private final ImageButton btnShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvLanguageBadge = itemView.findViewById(R.id.tvLanguageBadge);
            tvSnippetTitle = itemView.findViewById(R.id.tvSnippetTitle);
            tvSnippetTime = itemView.findViewById(R.id.tvSnippetTime);
            tvCode = itemView.findViewById(R.id.tvCode);
            btnShare = itemView.findViewById(R.id.btnShare);
        }

        public void bind(SnippetCard snippet, OnSnippetClickListener listener) {
            tvLanguageBadge.setText(snippet.getLanguageBadge());
            tvSnippetTitle.setText(snippet.getTitle());
            tvSnippetTime.setText(snippet.getUpdatedTime());

            // Configure Code Highlight
            if (tvCode != null) {
                tvCode.setText(highlightCode(snippet.getCodePreview()));
                // Set background color for the TextView if needed to match the theme
                tvCode.setBackgroundColor(android.graphics.Color.parseColor("#080e0b"));
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
