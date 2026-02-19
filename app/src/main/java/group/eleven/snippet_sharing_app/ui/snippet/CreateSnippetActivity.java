package group.eleven.snippet_sharing_app.ui.snippet;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

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

    private void initializeLanguages() {
        allLanguages = new ArrayList<>();
        allLanguages.add(new Language("JavaScript", "application/javascript", "JS", "#F7DF1E"));
        allLanguages.add(new Language("Python", "text/x-python", "Py", "#3776AB"));
        allLanguages.add(new Language("Java", "text/x-java-source", "Java", "#007396"));
        allLanguages.add(new Language("HTML/CSS", "text/html", "HC", "#E34F26"));
        allLanguages.add(new Language("Kotlin", "text/x-kotlin", "Kt", "#7F52FF"));
        allLanguages.add(new Language("Swift", "text/x-swift", "Sw", "#F05138"));

        // Add more default if needed or just these

        // Default selection
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
        // Try to set header title if possible, usually snippet_create_header or similar
        // binding.tvHeaderTitle.setText("Edit Snippet"); // Commented out to prevent
        // crash if ID wrong
        try {
            // Basic reflection or just check binding
            // Assuming generic "tvHeader" or hardcoded text
        } catch (Exception e) {
        }

        binding.btnPublish.setText("Update");
        binding.etTitle.setText(snippet.getTitle());
        binding.etCode.setText(snippet.getCode());

        // Language
        for (Language l : allLanguages) {
            if (l.getName().equalsIgnoreCase(snippet.getLanguage())) {
                updateLanguageSelection(l);
                break;
            }
        }

        // Privacy
        currentPrivacy = snippet.getPrivacy();
        updatePrivacyUI(currentPrivacy);
    }

    private void updatePrivacyUI(String privacy) {
        binding.tabPublic.setAlpha(0.5f);
        binding.tabTeam.setAlpha(0.5f);
        binding.tabPrivate.setAlpha(0.5f);

        if (privacy.equalsIgnoreCase("Public"))
            binding.tabPublic.setAlpha(1.0f);
        else if (privacy.equalsIgnoreCase("Team"))
            binding.tabTeam.setAlpha(1.0f);
        else if (privacy.equalsIgnoreCase("Private"))
            binding.tabPrivate.setAlpha(1.0f);
    }

    private void setupHeaderAndInputs() {
        binding.tvCancel.setOnClickListener(v -> finish());
        binding.btnPublish.setOnClickListener(v -> {
            String title = binding.etTitle.getText().toString();
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Snippet
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

        View.OnClickListener visibilityListener = v -> {
            binding.tabPublic.setAlpha(0.5f);
            binding.tabTeam.setAlpha(0.5f);
            binding.tabPrivate.setAlpha(0.5f);
            v.setAlpha(1.0f);

            if (v == binding.tabPublic)
                currentPrivacy = "Public";
            else if (v == binding.tabTeam)
                currentPrivacy = "Team";
            else if (v == binding.tabPrivate)
                currentPrivacy = "Private";
        };
        binding.tabPublic.setOnClickListener(visibilityListener);
        binding.tabTeam.setOnClickListener(visibilityListener);
        binding.tabPrivate.setOnClickListener(visibilityListener);
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
        binding.chipLanguage.setOnClickListener(v -> showLanguageBottomSheet());
        binding.btnManageTags.setOnClickListener(v -> showTagsBottomSheet());
        binding.btnSelectCategory.setOnClickListener(v -> showCategoryBottomSheet());
        binding.btnSelectTeam.setOnClickListener(v -> showTeamBottomSheet());

        // Listen for Category Selection
        getSupportFragmentManager().setFragmentResultListener(SelectCategoryBottomSheet.REQUEST_KEY, this,
                (requestKey, result) -> {
                    String id = result.getString("categoryId");
                    String name = result.getString("categoryName");
                    if (id != null && name != null) {
                        selectedCategoryId = id;
                        binding.btnSelectCategory.setText(name);
                        binding.btnSelectCategory.setTextColor(Color.parseColor("#3DD68C")); // Green
                    }
                });

        // Listen for Team Selection
        getSupportFragmentManager().setFragmentResultListener(SelectTeamDialogFragment.REQUEST_KEY, this,
                (requestKey, result) -> {
                    int id = result.getInt("teamId", -1);
                    String name = result.getString("teamName");
                    if (id != -1 && name != null) {
                        selectedTeamId = id;
                        binding.btnSelectTeam.setText(name);
                        binding.btnSelectTeam.setTextColor(Color.parseColor("#3DD68C")); // Green
                    }
                });
    }

    // ... (keep intermediate methods if any, but replacing showTeamBottomSheet
    // below)

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

        // Setup adapters
        LanguageAdapter adapter = new LanguageAdapter(this, allLanguages);
        lvLanguages.setAdapter(adapter);

        // Recent Adapter
        LanguageAdapter recentAdapter = new LanguageAdapter(this, recentLanguages);
        lvRecent.setAdapter(recentAdapter);

        if (recentLanguages.isEmpty()) {
            tvRecentLabel.setVisibility(View.GONE);
            lvRecent.setVisibility(View.GONE);
        } else {
            tvRecentLabel.setVisibility(View.VISIBLE);
            lvRecent.setVisibility(View.VISIBLE);
        }

        // Search Logic
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

        // Click Logic
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

        // Add to recent
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
            String buttonText = tags.isEmpty() ? "Manage Tags" : tags.size() + " Tags Selected";
            binding.btnManageTags.setText(buttonText);

            // Optional: Change button style/color if tags are selected
            if (!tags.isEmpty()) {
                binding.btnManageTags.setTextColor(Color.parseColor("#00e676"));
                // You could also update the icon tint or make it bold
            } else {
                binding.btnManageTags.setTextColor(Color.parseColor("#ffffff"));
            }
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
                ((View) sheetView.getParent()).setBackgroundColor(Color.TRANSPARENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}