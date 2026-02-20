package group.eleven.snippet_sharing_app.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.eleven.snippet_sharing_app.R;

/**
 * Utility class for applying syntax highlighting to code snippets.
 * Uses theme attributes for proper light/dark mode support.
 */
public class SyntaxHighlighter {

    // Colors for syntax highlighting (resolved from theme)
    private final int keywordColor;
    private final int stringColor;
    private final int numberColor;
    private final int commentColor;
    private final int functionColor;
    private final int typeColor;
    private final int operatorColor;
    private final int variableColor;

    // Common keywords across languages
    private static final String KEYWORDS_PATTERN =
            "\\b(function|const|let|var|if|else|for|while|do|switch|case|break|continue|return|" +
            "try|catch|finally|throw|new|delete|typeof|instanceof|in|of|" +
            "class|extends|implements|interface|enum|public|private|protected|static|final|" +
            "abstract|async|await|yield|import|export|from|as|default|" +
            "def|elif|except|lambda|pass|raise|with|assert|" +
            "fn|pub|mut|impl|trait|struct|mod|use|crate|" +
            "func|package|defer|go|chan|select|range|" +
            "val|fun|object|companion|when|is|data|sealed|" +
            "void|int|float|double|boolean|string|char|byte|short|long|" +
            "true|false|null|nil|None|undefined|this|self|super)\\b";

    // String patterns (single and double quotes)
    private static final String STRING_PATTERN =
            "(\"(?:[^\"\\\\]|\\\\.)*\"|'(?:[^'\\\\]|\\\\.)*'|`(?:[^`\\\\]|\\\\.)*`)";

    // Number pattern
    private static final String NUMBER_PATTERN =
            "\\b(\\d+\\.?\\d*[fFdDlL]?|0x[0-9a-fA-F]+|0b[01]+)\\b";

    // Comment patterns
    private static final String COMMENT_PATTERN =
            "(//.*?$|/\\*.*?\\*/|#.*?$)";

    // Function call pattern
    private static final String FUNCTION_PATTERN =
            "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(";

    // Type pattern (capitalized words, often types/classes)
    private static final String TYPE_PATTERN =
            "\\b([A-Z][a-zA-Z0-9_]*)\\b";

    // Operator pattern
    private static final String OPERATOR_PATTERN =
            "([+\\-*/%=<>!&|^~?:]+|=>|->)";

    public SyntaxHighlighter(Context context) {
        // Get colors from theme attributes for proper light/dark mode support
        Resources.Theme theme = context.getTheme();

        keywordColor = resolveThemeColor(theme, R.attr.syntaxKeywordColor,
                ContextCompat.getColor(context, R.color.syntax_keyword));
        stringColor = resolveThemeColor(theme, R.attr.syntaxStringColor,
                ContextCompat.getColor(context, R.color.syntax_string));
        numberColor = resolveThemeColor(theme, R.attr.syntaxNumberColor,
                ContextCompat.getColor(context, R.color.syntax_number));
        commentColor = resolveThemeColor(theme, R.attr.syntaxCommentColor,
                ContextCompat.getColor(context, R.color.syntax_comment));
        functionColor = resolveThemeColor(theme, R.attr.syntaxFunctionColor,
                ContextCompat.getColor(context, R.color.syntax_function));
        typeColor = resolveThemeColor(theme, R.attr.syntaxTypeColor,
                ContextCompat.getColor(context, R.color.syntax_type));
        operatorColor = resolveThemeColor(theme, R.attr.syntaxOperatorColor,
                ContextCompat.getColor(context, R.color.syntax_operator));
        variableColor = resolveThemeColor(theme, R.attr.syntaxVariableColor,
                ContextCompat.getColor(context, R.color.syntax_variable));
    }

    /**
     * Resolve a color from theme attribute, with fallback to default color
     */
    private int resolveThemeColor(Resources.Theme theme, int attr, int defaultColor) {
        TypedValue typedValue = new TypedValue();
        if (theme.resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        }
        return defaultColor;
    }

    /**
     * Apply syntax highlighting to code
     */
    public SpannableString highlight(String code) {
        if (code == null || code.isEmpty()) {
            return new SpannableString("");
        }

        SpannableString spannable = new SpannableString(code);

        // Apply highlighting in order (later ones override earlier)
        // 1. Numbers first (lowest priority)
        applyPattern(spannable, NUMBER_PATTERN, numberColor, 0);

        // 2. Types (capitalized words)
        applyPattern(spannable, TYPE_PATTERN, typeColor, 0);

        // 3. Keywords (higher priority than types)
        applyPattern(spannable, KEYWORDS_PATTERN, keywordColor, Pattern.CASE_INSENSITIVE);

        // 4. Function calls
        applyFunctionPattern(spannable);

        // 5. Operators
        applyPattern(spannable, OPERATOR_PATTERN, operatorColor, 0);

        // 6. Strings (higher priority - don't highlight inside strings)
        applyPattern(spannable, STRING_PATTERN, stringColor, Pattern.MULTILINE);

        // 7. Comments (highest priority - override everything)
        applyPattern(spannable, COMMENT_PATTERN, commentColor, Pattern.MULTILINE);

        return spannable;
    }

    private void applyPattern(SpannableString spannable, String pattern, int color, int flags) {
        try {
            Pattern p = Pattern.compile(pattern, flags | Pattern.MULTILINE);
            Matcher m = p.matcher(spannable);
            while (m.find()) {
                spannable.setSpan(
                        new ForegroundColorSpan(color),
                        m.start(),
                        m.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        } catch (Exception e) {
            // Ignore pattern errors
        }
    }

    private void applyFunctionPattern(SpannableString spannable) {
        try {
            Pattern p = Pattern.compile(FUNCTION_PATTERN);
            Matcher m = p.matcher(spannable);
            while (m.find()) {
                // Only color the function name, not the parenthesis
                int start = m.start(1);
                int end = m.end(1);
                spannable.setSpan(
                        new ForegroundColorSpan(functionColor),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        } catch (Exception e) {
            // Ignore pattern errors
        }
    }

    /**
     * Get language-specific highlighter (for future enhancement)
     */
    public SpannableString highlightForLanguage(String code, String language) {
        // For now, use generic highlighting
        // Can be extended for language-specific rules
        return highlight(code);
    }
}
