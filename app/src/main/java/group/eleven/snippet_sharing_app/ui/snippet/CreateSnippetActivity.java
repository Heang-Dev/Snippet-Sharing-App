package group.eleven.snippet_sharing_app.ui.snippet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.databinding.ActivityCreateSnippetBinding;
import group.eleven.snippet_sharing_app.model.Language;
import group.eleven.snippet_sharing_app.ui.category.SelectCategoryBottomSheet;
import group.eleven.snippet_sharing_app.ui.tags.ManageTagsBottomSheet;
import group.eleven.snippet_sharing_app.ui.team.SelectTeamDialogFragment;

public class CreateSnippetActivity extends AppCompatActivity {

    private ActivityCreateSnippetBinding binding;
    private List<Language> allLanguages;
    private List<Language> recentLanguages;
    private Language selectedLanguage;
    private ArrayList<String> currentSelectedTags = new ArrayList<>();
    private String selectedCategoryId;
    private int selectedTeamId = -1;

    private String snippetIdToEdit = null;
    private boolean originalFavoriteStatus = false;
    private String currentPrivacy = "Public";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateSnippetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupStatusBar();
        initializeLanguages();
        setupHeaderAndInputs();
        setupQuickAccess();
        setupBottomSheets();

        if (getIntent().hasExtra("SNIPPET_ID")) {
            String id = getIntent().getStringExtra("SNIPPET_ID");
            for (group.eleven.snippet_sharing_app.model.SnippetModel s : group.eleven.snippet_sharing_app.data.SnippetRepository
                    .getInstance().getAllSnippets()) {
                if (s.getId().equals(id)) {
                    snippetIdToEdit = id;
                    originalFavoriteStatus = s.isFavorite();
                    startEditMode(s);
                    break;
                }
            }
        }
    }

    private void setupStatusBar() {
        Window window = getWindow();
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.surfaceColor, typedValue, true);
        int surfaceColor;
        if (typedValue.resourceId != 0) {
            surfaceColor = ContextCompat.getColor(this, typedValue.resourceId);
        } else {
            surfaceColor = typedValue.data;
        }
        window.setStatusBarColor(surfaceColor);
        window.setNavigationBarColor(surfaceColor);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            boolean isLightBackground = isColorLight(surfaceColor);
            controller.setAppearanceLightStatusBars(isLightBackground);
            controller.setAppearanceLightNavigationBars(isLightBackground);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color)
                + 0.587 * android.graphics.Color.green(color)
                + 0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void initializeLanguages() {
        allLanguages = new ArrayList<>();
        allLanguages.add(new Language("JavaScript", "application/javascript", "JS", "#F7DF1E"));
        allLanguages.add(new Language("Python", "text/x-python", "Py", "#3776AB"));
        allLanguages.add(new Language("Java", "text/x-java-source", "Java", "#007396"));
        allLanguages.add(new Language("HTML/CSS", "text/html", "HC", "#E34F26"));
        allLanguages.add(new Language("Kotlin", "text/x-kotlin", "Kt", "#7F52FF"));
        allLanguages.add(new Language("Swift", "text/x-swift", "Sw", "#F05138"));

        if (!allLanguages.isEmpty()) {
            selectedLanguage = allLanguages.get(0);
            selectedLanguage.setSelected(true);
            if (binding != null) {
                binding.chipLanguage.setText(selectedLanguage.getName());
            }
        }

        recentLanguages = new ArrayList<>();
    }

    private void startEditMode(group.eleven.snippet_sharing_app.model.SnippetModel snippet) {
        binding.tvNewSnippet.setText("Edit Snippet");
        binding.btnPublish.setText("Update");
        binding.etTitle.setText(snippet.getTitle());
        binding.etCode.setText(snippet.getCode());

        for (Language l : allLanguages) {
            if (l.getName().equalsIgnoreCase(snippet.getLanguage())) {
                updateLanguageSelection(l);
                break;
            }
        }

        currentPrivacy = snippet.getPrivacy();
        updatePrivacyUI(currentPrivacy);
    }

    private void updatePrivacyUI(String privacy) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.accentColor, typedValue, true);
        int accentColor = typedValue.resourceId != 0
            ? ContextCompat.getColor(this, typedValue.resourceId)
            : typedValue.data;

        getTheme().resolveAttribute(R.attr.textSecondaryColor, typedValue, true);
        int textSecondaryColor = typedValue.resourceId != 0
            ? ContextCompat.getColor(this, typedValue.resourceId)
            : typedValue.data;

        int whiteColor = ContextCompat.getColor(this, R.color.white);
        int transparentColor = android.graphics.Color.TRANSPARENT;

        // Reset all tabs to unselected state
        binding.tabPublic.setCardBackgroundColor(transparentColor);
        binding.tabTeam.setCardBackgroundColor(transparentColor);
        binding.tabPrivate.setCardBackgroundColor(transparentColor);
        binding.tvPublicLabel.setTextColor(textSecondaryColor);
        binding.tvTeamLabel.setTextColor(textSecondaryColor);
        binding.tvPrivateLabel.setTextColor(textSecondaryColor);

        // Highlight selected tab
        if (privacy.equalsIgnoreCase("Public")) {
            binding.tabPublic.setCardBackgroundColor(accentColor);
            binding.tvPublicLabel.setTextColor(whiteColor);
        } else if (privacy.equalsIgnoreCase("Team")) {
            binding.tabTeam.setCardBackgroundColor(accentColor);
            binding.tvTeamLabel.setTextColor(whiteColor);
        } else if (privacy.equalsIgnoreCase("Private")) {
            binding.tabPrivate.setCardBackgroundColor(accentColor);
            binding.tvPrivateLabel.setTextColor(whiteColor);
        }
    }

    private void setupHeaderAndInputs() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnPublish.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString();
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            String langName = selectedLanguage != null ? selectedLanguage.getName() : "Text";
            String langColor = selectedLanguage != null ? selectedLanguage.getColorHex() : "#FFFFFF";

            group.eleven.snippet_sharing_app.model.SnippetModel newSnippet = new group.eleven.snippet_sharing_app.model.SnippetModel(
                    snippetIdToEdit != null ? snippetIdToEdit : String.valueOf(System.currentTimeMillis()),
                    title,
                    langName,
                    langColor,
                    currentPrivacy,
                    snippetIdToEdit != null ? "v1.0.1" : "v1.0.0",
                    "Just now",
                    snippetIdToEdit != null ? originalFavoriteStatus : false,
                    binding.etCode.getText().toString());

            if (snippetIdToEdit != null) {
                group.eleven.snippet_sharing_app.data.SnippetRepository.getInstance().updateSnippet(newSnippet);
                Toast.makeText(this, "Snippet Updated successfully!", Toast.LENGTH_LONG).show();
            } else {
                group.eleven.snippet_sharing_app.data.SnippetRepository.getInstance().addSnippet(newSnippet);
                Toast.makeText(this, "Snippet Published successfully!", Toast.LENGTH_LONG).show();
            }
            finish();
        });

        // Visibility tab listeners
        binding.tabPublic.setOnClickListener(v -> {
            currentPrivacy = "Public";
            updatePrivacyUI(currentPrivacy);
        });
        binding.tabTeam.setOnClickListener(v -> {
            currentPrivacy = "Team";
            updatePrivacyUI(currentPrivacy);
        });
        binding.tabPrivate.setOnClickListener(v -> {
            currentPrivacy = "Private";
            updatePrivacyUI(currentPrivacy);
        });
    }

    private void setupQuickAccess() {
        View.OnClickListener qaListener = v -> {
            if (v instanceof TextView) {
                String textToInsert = ((TextView) v).getText().toString();
                int start = Math.max(binding.etCode.getSelectionStart(), 0);
                int end = Math.max(binding.etCode.getSelectionEnd(), 0);
                binding.etCode.getText().replace(Math.min(start, end), Math.max(start, end), textToInsert, 0,
                        textToInsert.length());
            }
        };
        binding.qaBraceOpen.setOnClickListener(qaListener);
        binding.qaBraceClose.setOnClickListener(qaListener);
        binding.qaParenOpen.setOnClickListener(qaListener);
        binding.qaParenClose.setOnClickListener(qaListener);
        binding.qaEquals.setOnClickListener(qaListener);
        binding.qaArrow.setOnClickListener(qaListener);
        binding.qaSemicolon.setOnClickListener(qaListener);
        binding.qaConst.setOnClickListener(qaListener);
        binding.qaReturn.setOnClickListener(qaListener);
    }

    private void setupBottomSheets() {
        binding.cardLanguage.setOnClickListener(v -> showLanguageBottomSheet());
        binding.btnManageTags.setOnClickListener(v -> showTagsBottomSheet());
        binding.btnSelectCategory.setOnClickListener(v -> showCategoryBottomSheet());
        binding.btnSelectTeam.setOnClickListener(v -> showTeamBottomSheet());

        getSupportFragmentManager().setFragmentResultListener(SelectCategoryBottomSheet.REQUEST_KEY, this,
                (requestKey, result) -> {
                    String id = result.getString("categoryId");
                    String name = result.getString("categoryName");
                    if (id != null && name != null) {
                        selectedCategoryId = id;
                        // Update the text in the card
                        TextView tv = binding.btnSelectCategory.findViewById(android.R.id.text1);
                        if (tv == null) {
                            // Find TextView inside the card
                            View child = binding.btnSelectCategory.getChildAt(0);
                            if (child instanceof android.widget.LinearLayout) {
                                tv = (TextView) ((android.widget.LinearLayout) child).getChildAt(0);
                            }
                        }
                    }
                });

        getSupportFragmentManager().setFragmentResultListener(SelectTeamDialogFragment.REQUEST_KEY, this,
                (requestKey, result) -> {
                    int id = result.getInt("teamId", -1);
                    String name = result.getString("teamName");
                    if (id != -1 && name != null) {
                        selectedTeamId = id;
                    }
                });
    }

    private void showTeamBottomSheet() {
        SelectTeamDialogFragment dialog = SelectTeamDialogFragment.newInstance(selectedTeamId);
        dialog.show(getSupportFragmentManager(), "SelectTeamDialogFragment");
    }

    private void showLanguageBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_language, null);
        bottomSheetDialog.setContentView(sheetView);
        makeBackgroundTransparent(sheetView);

        EditText etSearch = sheetView.findViewById(R.id.etSearch);
        ListView lvLanguages = sheetView.findViewById(R.id.lvLanguages);
        ListView lvRecent = sheetView.findViewById(R.id.lvRecent);
        TextView tvRecentLabel = sheetView.findViewById(R.id.tvRecentLabel);

        LanguageAdapter adapter = new LanguageAdapter(this, allLanguages);
        lvLanguages.setAdapter(adapter);

        LanguageAdapter recentAdapter = new LanguageAdapter(this, recentLanguages);
        lvRecent.setAdapter(recentAdapter);

        if (recentLanguages.isEmpty()) {
            tvRecentLabel.setVisibility(View.GONE);
            lvRecent.setVisibility(View.GONE);
        } else {
            tvRecentLabel.setVisibility(View.VISIBLE);
            lvRecent.setVisibility(View.VISIBLE);
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        lvLanguages.setOnItemClickListener((parent, view, position, id) -> {
            Language clickedLang = adapter.getItem(position);
            updateLanguageSelection(clickedLang);
            bottomSheetDialog.dismiss();
        });

        lvRecent.setOnItemClickListener((parent, view, position, id) -> {
            Language clickedLang = recentAdapter.getItem(position);
            updateLanguageSelection(clickedLang);
            bottomSheetDialog.dismiss();
        });

        View closeBtn = sheetView.findViewById(R.id.btnClose);
        if (closeBtn != null)
            closeBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void updateLanguageSelection(Language newLang) {
        if (selectedLanguage != null)
            selectedLanguage.setSelected(false);

        selectedLanguage = newLang;
        selectedLanguage.setSelected(true);

        binding.chipLanguage.setText(selectedLanguage.getName());

        if (recentLanguages.contains(newLang)) {
            recentLanguages.remove(newLang);
        }
        recentLanguages.add(0, newLang);
        if (recentLanguages.size() > 3) {
            recentLanguages.remove(recentLanguages.size() - 1);
        }
    }

    private void showTagsBottomSheet() {
        ManageTagsBottomSheet bottomSheet = ManageTagsBottomSheet.newInstance(currentSelectedTags);
        bottomSheet.setOnTagsSelectedListener(tags -> {
            currentSelectedTags = new ArrayList<>(tags);
            // Update can be done if needed
        });
        bottomSheet.show(getSupportFragmentManager(), "ManageTagsBottomSheet");
    }

    private void showCategoryBottomSheet() {
        SelectCategoryBottomSheet bottomSheet = SelectCategoryBottomSheet.newInstance(selectedCategoryId);
        bottomSheet.show(getSupportFragmentManager(), "SelectCategoryBottomSheet");
    }

    private void makeBackgroundTransparent(View sheetView) {
        try {
            if (sheetView.getParent() != null) {
                ((View) sheetView.getParent()).setBackgroundColor(android.graphics.Color.TRANSPARENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
