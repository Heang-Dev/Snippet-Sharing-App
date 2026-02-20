package group.eleven.snippet_sharing_app.ui.snippet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Language;
import group.eleven.snippet_sharing_app.data.repository.SnippetCreationRepository;
import group.eleven.snippet_sharing_app.databinding.ActivityCreateSnippetBinding;
import group.eleven.snippet_sharing_app.ui.category.SelectCategoryBottomSheet;
import group.eleven.snippet_sharing_app.ui.tags.ManageTagsBottomSheet;
import group.eleven.snippet_sharing_app.ui.team.SelectTeamDialogFragment;
import group.eleven.snippet_sharing_app.utils.KeyboardUtils;
import group.eleven.snippet_sharing_app.utils.Resource;

public class CreateSnippetActivity extends AppCompatActivity {

    private ActivityCreateSnippetBinding binding;
    private SnippetCreationRepository repository;

    // Languages
    private List<Language> apiLanguages = new ArrayList<>();
    private List<group.eleven.snippet_sharing_app.model.Language> localLanguages = new ArrayList<>();
    private Language selectedApiLanguage;

    // Selection state
    private ArrayList<String> selectedTags = new ArrayList<>();
    private String selectedCategoryId;
    private String selectedCategoryName;
    private String selectedTeamId;
    private String selectedTeamName;
    private String currentVisibility = "public";

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateSnippetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new SnippetCreationRepository(this);

        setupStatusBar();
        setupHeaderAndInputs();
        setupQuickAccess();
        setupBottomSheets();
        setupVisibilityTabs();
        loadLanguages();
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

    private void loadLanguages() {
        repository.getLanguages().observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                apiLanguages = resource.data;
                convertToLocalLanguages();
                if (!apiLanguages.isEmpty()) {
                    selectedApiLanguage = apiLanguages.get(0);
                    binding.chipLanguage.setText(selectedApiLanguage.getDisplayName());
                    updateFilename();
                }
            } else if (resource.status == Resource.Status.ERROR) {
                initializeFallbackLanguages();
            }
        });
    }

    private void convertToLocalLanguages() {
        localLanguages.clear();
        for (Language lang : apiLanguages) {
            group.eleven.snippet_sharing_app.model.Language local =
                    new group.eleven.snippet_sharing_app.model.Language(
                            lang.getDisplayName(),
                            lang.getSlug(),
                            getLanguageAbbr(lang.getName()),
                            lang.getColor() != null ? lang.getColor() : "#666666"
                    );
            localLanguages.add(local);
        }
    }

    private void initializeFallbackLanguages() {
        apiLanguages = new ArrayList<>();
        localLanguages = new ArrayList<>();

        String[][] fallbackData = {
                {"1", "JavaScript", "javascript", "JS", "#F7DF1E", "js"},
                {"2", "Python", "python", "Py", "#3776AB", "py"},
                {"3", "Java", "java", "Ja", "#007396", "java"},
                {"4", "TypeScript", "typescript", "TS", "#3178C6", "ts"},
                {"5", "Kotlin", "kotlin", "Kt", "#7F52FF", "kt"},
                {"6", "Swift", "swift", "Sw", "#F05138", "swift"}
        };

        for (String[] data : fallbackData) {
            Language apiLang = new Language();
            apiLang.setId(data[0]);
            apiLang.setName(data[1]);
            apiLang.setSlug(data[2]);
            apiLang.setColor(data[4]);
            apiLang.setFileExtensions(new String[]{data[5]});
            apiLanguages.add(apiLang);

            group.eleven.snippet_sharing_app.model.Language local =
                    new group.eleven.snippet_sharing_app.model.Language(data[1], data[2], data[3], data[4]);
            localLanguages.add(local);
        }

        if (!apiLanguages.isEmpty()) {
            selectedApiLanguage = apiLanguages.get(0);
            binding.chipLanguage.setText(selectedApiLanguage.getDisplayName());
            updateFilename();
        }
    }

    private String getLanguageAbbr(String name) {
        if (name == null) return "??";
        switch (name.toLowerCase()) {
            case "javascript": return "JS";
            case "typescript": return "TS";
            case "python": return "Py";
            case "java": return "Ja";
            case "kotlin": return "Kt";
            case "swift": return "Sw";
            case "html": return "HT";
            case "css": return "CS";
            case "php": return "PH";
            case "ruby": return "Rb";
            case "go": return "Go";
            case "rust": return "Rs";
            case "c#":
            case "csharp": return "C#";
            case "c++":
            case "cpp": return "C+";
            case "c": return "C";
            default:
                return name.length() >= 2 ? name.substring(0, 2) : name;
        }
    }

    private void updateFilename() {
        if (selectedApiLanguage != null && selectedApiLanguage.getFileExtensions() != null
                && selectedApiLanguage.getFileExtensions().length > 0) {
            binding.tvFilename.setText("snippet." + selectedApiLanguage.getFileExtensions()[0]);
        } else {
            binding.tvFilename.setText("snippet.txt");
        }
    }

    private void setupHeaderAndInputs() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnPublish.setOnClickListener(v -> publishSnippet());
    }

    private void setupVisibilityTabs() {
        updateVisibilityUI("public");

        binding.tabPublic.setOnClickListener(v -> {
            currentVisibility = "public";
            updateVisibilityUI(currentVisibility);
            binding.btnSelectTeam.setVisibility(View.GONE);
        });

        binding.tabTeam.setOnClickListener(v -> {
            currentVisibility = "team";
            updateVisibilityUI(currentVisibility);
            binding.btnSelectTeam.setVisibility(View.VISIBLE);
        });

        binding.tabPrivate.setOnClickListener(v -> {
            currentVisibility = "private";
            updateVisibilityUI(currentVisibility);
            binding.btnSelectTeam.setVisibility(View.GONE);
        });
    }

    private void updateVisibilityUI(String visibility) {
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

        // Reset all tabs
        binding.tabPublic.setCardBackgroundColor(transparentColor);
        binding.tabTeam.setCardBackgroundColor(transparentColor);
        binding.tabPrivate.setCardBackgroundColor(transparentColor);
        binding.tvPublicLabel.setTextColor(textSecondaryColor);
        binding.tvTeamLabel.setTextColor(textSecondaryColor);
        binding.tvPrivateLabel.setTextColor(textSecondaryColor);

        // Highlight selected
        switch (visibility.toLowerCase()) {
            case "public":
                binding.tabPublic.setCardBackgroundColor(accentColor);
                binding.tvPublicLabel.setTextColor(whiteColor);
                break;
            case "team":
                binding.tabTeam.setCardBackgroundColor(accentColor);
                binding.tvTeamLabel.setTextColor(whiteColor);
                break;
            case "private":
                binding.tabPrivate.setCardBackgroundColor(accentColor);
                binding.tvPrivateLabel.setTextColor(whiteColor);
                break;
        }
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

        // Category selection result
        getSupportFragmentManager().setFragmentResultListener(SelectCategoryBottomSheet.REQUEST_KEY, this,
                (requestKey, result) -> {
                    String id = result.getString("categoryId");
                    String name = result.getString("categoryName");
                    if (id != null && name != null) {
                        selectedCategoryId = id;
                        selectedCategoryName = name;
                        binding.tvSelectedCategory.setText(name);
                        binding.tvSelectedCategory.setTextColor(
                                ContextCompat.getColor(this, R.color.primary));
                    }
                });

        // Team selection result
        getSupportFragmentManager().setFragmentResultListener(SelectTeamDialogFragment.REQUEST_KEY, this,
                (requestKey, result) -> {
                    String id = result.getString("teamId");
                    String name = result.getString("teamName");
                    if (id != null && name != null) {
                        selectedTeamId = id;
                        selectedTeamName = name;
                        binding.tvSelectedTeam.setText(name);
                        binding.tvSelectedTeam.setTextColor(
                                ContextCompat.getColor(this, R.color.primary));
                    }
                });
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

        // Mark selected language
        for (int i = 0; i < localLanguages.size(); i++) {
            group.eleven.snippet_sharing_app.model.Language local = localLanguages.get(i);
            if (selectedApiLanguage != null && i < apiLanguages.size()
                    && apiLanguages.get(i).getId().equals(selectedApiLanguage.getId())) {
                local.setSelected(true);
            } else {
                local.setSelected(false);
            }
        }

        LanguageAdapter adapter = new LanguageAdapter(this, localLanguages);
        lvLanguages.setAdapter(adapter);

        // Hide recent section
        tvRecentLabel.setVisibility(View.GONE);
        lvRecent.setVisibility(View.GONE);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvLanguages.setOnItemClickListener((parent, view, position, id) -> {
            group.eleven.snippet_sharing_app.model.Language clickedLocal = adapter.getItem(position);
            // Find corresponding API Language
            for (int i = 0; i < localLanguages.size(); i++) {
                if (localLanguages.get(i).getName().equals(clickedLocal.getName())) {
                    if (i < apiLanguages.size()) {
                        selectedApiLanguage = apiLanguages.get(i);
                        binding.chipLanguage.setText(selectedApiLanguage.getDisplayName());
                        updateFilename();
                    }
                    break;
                }
            }
            bottomSheetDialog.dismiss();
        });

        View closeBtn = sheetView.findViewById(R.id.btnClose);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }

        bottomSheetDialog.show();
    }

    private void showTagsBottomSheet() {
        ManageTagsBottomSheet bottomSheet = ManageTagsBottomSheet.newInstance(selectedTags);
        bottomSheet.setOnTagsSelectedListener(tags -> {
            selectedTags = new ArrayList<>(tags);
            updateSelectedTagsUI();
        });
        bottomSheet.show(getSupportFragmentManager(), "ManageTagsBottomSheet");
    }

    private void updateSelectedTagsUI() {
        binding.chipGroupSelectedTags.removeAllViews();
        if (selectedTags.isEmpty()) {
            binding.chipGroupSelectedTags.setVisibility(View.GONE);
        } else {
            binding.chipGroupSelectedTags.setVisibility(View.VISIBLE);
            for (String tag : selectedTags) {
                Chip chip = (Chip) LayoutInflater.from(this)
                        .inflate(R.layout.chip_item_display, binding.chipGroupSelectedTags, false);
                chip.setText("#" + tag);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(v -> {
                    selectedTags.remove(tag);
                    updateSelectedTagsUI();
                });
                binding.chipGroupSelectedTags.addView(chip);
            }
        }
    }

    private void showCategoryBottomSheet() {
        SelectCategoryBottomSheet bottomSheet = SelectCategoryBottomSheet.newInstance(selectedCategoryId);
        bottomSheet.show(getSupportFragmentManager(), "SelectCategoryBottomSheet");
    }

    private void showTeamBottomSheet() {
        SelectTeamDialogFragment dialog = SelectTeamDialogFragment.newInstance(selectedTeamId);
        dialog.show(getSupportFragmentManager(), "SelectTeamDialogFragment");
    }

    private void publishSnippet() {
        String title = binding.etTitle.getText().toString().trim();
        String code = binding.etCode.getText().toString();
        String description = binding.etDescription.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            binding.etTitle.setError("Title is required");
            binding.etTitle.requestFocus();
            return;
        }

        if (code.isEmpty()) {
            Toast.makeText(this, "Please enter some code", Toast.LENGTH_SHORT).show();
            binding.etCode.requestFocus();
            return;
        }

        if (selectedApiLanguage == null) {
            Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentVisibility.equals("team") && (selectedTeamId == null || selectedTeamId.isEmpty())) {
            Toast.makeText(this, "Please select a team for team visibility", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button and show loading
        if (isLoading) return;
        isLoading = true;
        binding.btnPublish.setEnabled(false);
        binding.btnPublish.setText("Publishing...");

        repository.createSnippet(
                title,
                code,
                selectedApiLanguage.getId(),
                currentVisibility,
                description,
                selectedTags,
                selectedCategoryId,
                selectedTeamId
        ).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(this, "Snippet published successfully!", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            } else if (resource.status == Resource.Status.ERROR) {
                isLoading = false;
                binding.btnPublish.setEnabled(true);
                binding.btnPublish.setText("Publish");
                Toast.makeText(this, "Failed: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
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
