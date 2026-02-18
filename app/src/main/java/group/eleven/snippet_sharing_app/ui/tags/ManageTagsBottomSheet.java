package group.eleven.snippet_sharing_app.ui.tags;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.model.Tag;

public class ManageTagsBottomSheet extends BottomSheetDialogFragment {

    private SuggestionTagsAdapter adapter;
    private List<Tag> allTags; // All possible tags (from API/DB)
    private List<String> selectedTags; // Currently selected tags (Strings)
    private ChipGroup chipGroupSelected;
    private ChipGroup chipGroupPopular;
    private EditText etSearchTags;
    private OnTagsSelectedListener listener;

    // Interface callback to return data
    public interface OnTagsSelectedListener {
        void onTagsSelected(List<String> tags);
    }

    // Factory method
    public static ManageTagsBottomSheet newInstance(ArrayList<String> selectedTags) {
        ManageTagsBottomSheet fragment = new ManageTagsBottomSheet();
        Bundle args = new Bundle();
        args.putStringArrayList("selected_tags", selectedTags);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnTagsSelectedListener(OnTagsSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        android.app.Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = (com.google.android.material.bottomsheet.BottomSheetDialog) dialogInterface;
            android.widget.FrameLayout bottomSheet = bottomSheetDialog
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                // Background transparent to show rounded corners from XML
                bottomSheet.setBackgroundResource(android.R.color.transparent);

                // Set height to 75% of screen height
                android.util.DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int height = (int) (displayMetrics.heightPixels * 0.75);

                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = height;
                bottomSheet.setLayoutParams(layoutParams);

                // Behavior setup
                com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior = com.google.android.material.bottomsheet.BottomSheetBehavior
                        .from(bottomSheet);
                behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setDraggable(true);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_manage_tags, container, false);
    }

    private ViewGroup rootContainer;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rootContainer = (ViewGroup) view;
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initData();
        setupListeners(view);
    }

    private void initViews(View view) {
        RecyclerView rvSuggestions = view.findViewById(R.id.rvSuggestions);
        // ...

        // Setup RecyclerView
        rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SuggestionTagsAdapter(this::addTag);
        rvSuggestions.setAdapter(adapter);

        // ... (rest of initViews)
        chipGroupSelected = view.findViewById(R.id.chipGroupSelected);
        chipGroupPopular = view.findViewById(R.id.chipGroupPopular);
        etSearchTags = view.findViewById(R.id.etSearchTags);

        // Auto-focus search and show keyboard
        etSearchTags.requestFocus();
        new Handler().postDelayed(() -> {
            if (getContext() != null && etSearchTags != null) {
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etSearchTags, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 200);
    }

    // ...

    private void addChipToSelected(String tagName) {
        Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.chip_item_selected, chipGroupSelected,
                false);
        chip.setText("#" + tagName);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupSelected.removeView(chip);
            selectedTags.remove(tagName);
            filterSuggestions(etSearchTags.getText().toString()); // Refresh to allow re-adding
        });

        chipGroupSelected.addView(chip);
    }

    private void initData() {
        if (getArguments() != null) {
            selectedTags = getArguments().getStringArrayList("selected_tags");
        }
        if (selectedTags == null)
            selectedTags = new ArrayList<>();

        // Mock Data for All Tags (In real app, fetch from VM/Repo)
        allTags = new ArrayList<>();
        allTags.add(new Tag("utility", 12));
        allTags.add(new Tag("frontend", 8));
        allTags.add(new Tag("react", 24));
        allTags.add(new Tag("backend", 15));
        allTags.add(new Tag("java", 30));
        allTags.add(new Tag("android", 42));
        allTags.add(new Tag("datetime", 5));
        allTags.add(new Tag("formatting", 3));
        allTags.add(new Tag("javascript", 100));

        // Mock Data for Popular Tags
        List<String> popularTags = new ArrayList<>();
        popularTags.add("typescript");
        popularTags.add("api");
        popularTags.add("database");
        popularTags.add("optimization");
        popularTags.add("css-tricks");
        popularTags.add("testing");

        // Populate Selected Chips from state
        for (String tagName : selectedTags) {
            addChipToSelected(tagName);
        }

        // Populate Popular Chips
        for (String tagName : popularTags) {
            addChipToPopular(tagName);
        }

        // Initial Suggestion List
        filterSuggestions("");
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.ivClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.tvCancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnDone).setOnClickListener(v -> {
            if (listener != null) {
                listener.onTagsSelected(selectedTags);
            }
            dismiss();
        });

        etSearchTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSuggestions(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Core logic to add a tag:
     * 1. Normalize name (trim, lowercase for comparison/storage, or keep original
     * casing)
     * 2. Check duplicates
     * 3. Add to list
     * 4. Add Chip UI
     * 5. Refresh Suggestions
     */
    private void addTag(Tag tag) {
        String tagName = tag.getName().trim().toLowerCase(); // Normalize to lowercase

        // Max limit check (Bonus)
        if (selectedTags.size() >= 10) {
            Toast.makeText(getContext(), "Max 10 tags allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if already selected
        if (selectedTags.contains(tagName)) {
            // Already added - normally filter hides duplicates, but just in case
            return;
        }

        selectedTags.add(tagName);
        addChipToSelected(tagName);

        // Clear search
        etSearchTags.setText("");

        // Refresh suggestions to remove the added tag from list
        filterSuggestions("");
    }

    private void addChipToPopular(String tagName) {
        Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.chip_item_popular, chipGroupPopular,
                false);
        chip.setText(tagName);
        chip.setOnClickListener(v -> {
            // Create a temporary Tag object to pass to addTag
            Tag tag = new Tag(tagName, 0);
            addTag(tag);
        });
        chipGroupPopular.addView(chip);
    }

    private void filterSuggestions(String query) {
        List<Tag> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        boolean exactMatchFound = false;

        for (Tag tag : allTags) {
            String tagName = tag.getName();

            // Should hide if already selected
            boolean isAlreadySelected = selectedTags.contains(tagName.toLowerCase());

            if (!isAlreadySelected) {
                if (tagName.toLowerCase().contains(lowerQuery)) {
                    filtered.add(tag);
                }
            }

            if (tagName.equalsIgnoreCase(lowerQuery)) {
                exactMatchFound = true; // Even if selected, we mark exact match found so we don't show "Create"
            }
        }

        // If no match found and query is not empty -> Show "Create ... " option
        // Also check if the query itself is already selected (e.g. user typed "java"
        // and "java" is already selected)
        if (!lowerQuery.isEmpty() && !exactMatchFound) {
            boolean isQuerySelected = selectedTags.contains(lowerQuery);
            if (!isQuerySelected) {
                // Use snippetCount = -1 to indicate "Create new" type
                filtered.add(0, new Tag(lowerQuery, -1));
            }
        }

        adapter.setTags(filtered);
    }
}
