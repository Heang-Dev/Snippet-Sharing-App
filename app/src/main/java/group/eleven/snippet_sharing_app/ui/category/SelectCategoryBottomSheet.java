package group.eleven.snippet_sharing_app.ui.category;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.Category;
import group.eleven.snippet_sharing_app.data.repository.SnippetCreationRepository;
import group.eleven.snippet_sharing_app.model.CategoryModel;
import group.eleven.snippet_sharing_app.utils.Resource;

public class SelectCategoryBottomSheet extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "request_category_selection";
    public static final String ARG_SELECTED_ID = "arg_selected_id";

    private RecyclerView rvAll;
    private CategoryAdapter allAdapter;
    private EditText etSearch;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private View layoutContent;
    private String currentSelectedId;

    private SnippetCreationRepository repository;
    private List<CategoryModel> allCategoryList = new ArrayList<>();

    public static SelectCategoryBottomSheet newInstance(String selectedId) {
        SelectCategoryBottomSheet fragment = new SelectCategoryBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_ID, selectedId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentSelectedId = getArguments().getString(ARG_SELECTED_ID);
        }
        repository = new SnippetCreationRepository(requireContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackgroundResource(android.R.color.transparent);

                // Height 70%
                android.util.DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int height = (int) (displayMetrics.heightPixels * 0.70);
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = height;
                bottomSheet.setLayoutParams(layoutParams);

                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
        return inflater.inflate(R.layout.bottom_sheet_select_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadCategoriesFromApi();
        setupListeners(view);
    }

    private void initViews(View view) {
        rvAll = view.findViewById(R.id.rvAllCategories);
        etSearch = view.findViewById(R.id.etSearchCategories);

        // Hide recent section - we'll just show all categories
        View rvRecent = view.findViewById(R.id.rvRecentCategories);
        if (rvRecent != null) rvRecent.setVisibility(View.GONE);

        // Hide recent label
        View recentLabel = view.findViewById(R.id.tvRecentLabel);
        if (recentLabel != null) recentLabel.setVisibility(View.GONE);

        rvAll.setLayoutManager(new LinearLayoutManager(getContext()));
        allAdapter = new CategoryAdapter(this::onCategorySelected);
        rvAll.setAdapter(allAdapter);
    }

    private void loadCategoriesFromApi() {
        repository.getCategories().observe(this, resource -> {
            if (resource.status == Resource.Status.LOADING) {
                // Show loading state
            } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allCategoryList.clear();
                flattenCategories(resource.data, allCategoryList, 0);
                allAdapter.setCategories(new ArrayList<>(allCategoryList));
                updateAdaptersSelection();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Flatten the category tree into a list with indentation level
     */
    private void flattenCategories(List<Category> categories, List<CategoryModel> result, int level) {
        if (categories == null) return;

        for (Category cat : categories) {
            // Create a prefix for indentation
            String prefix = "";
            for (int i = 0; i < level; i++) {
                prefix += "  ";
            }

            String displayName = prefix + (level > 0 ? "└ " : "") + cat.getName();

            CategoryModel model = new CategoryModel(
                    cat.getId(),                    // Use actual UUID
                    displayName,
                    cat.getSnippetsCount(),
                    getCategoryIcon(cat.getIcon()),
                    false
            );
            result.add(model);

            // Recursively add children
            if (cat.getChildren() != null && !cat.getChildren().isEmpty()) {
                flattenCategories(cat.getChildren(), result, level + 1);
            }
        }
    }

    private int getCategoryIcon(String iconName) {
        if (iconName == null) return R.drawable.ic_folder;
        switch (iconName.toLowerCase()) {
            case "code":
                return R.drawable.ic_code;
            case "database":
                return R.drawable.ic_database;
            case "terminal":
                return R.drawable.ic_terminal;
            case "shield":
            case "shield_check":
                return R.drawable.ic_shield_check;
            default:
                return R.drawable.ic_folder;
        }
    }

    private void updateAdaptersSelection() {
        allAdapter.setSelectedCategoryId(currentSelectedId);
    }

    private void onCategorySelected(CategoryModel category) {
        currentSelectedId = category.getId();
        updateAdaptersSelection();
    }

    private void setupListeners(View view) {
        // Done button
        view.findViewById(R.id.tvDone).setOnClickListener(v -> {
            Bundle result = new Bundle();
            String name = "";

            // Find name (strip indentation prefix)
            for (CategoryModel c : allCategoryList) {
                if (c.getId().equals(currentSelectedId)) {
                    name = c.getName().replaceAll("^\\s*└\\s*", "").trim();
                    break;
                }
            }

            result.putString("categoryId", currentSelectedId);
            result.putString("categoryName", name);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Hide create category for now (would need API call)
        View createCategory = view.findViewById(R.id.clCreateCategory);
        if (createCategory != null) {
            createCategory.setVisibility(View.GONE);
        }
    }

    private void filter(String query) {
        if (query.isEmpty()) {
            allAdapter.setCategories(new ArrayList<>(allCategoryList));
            return;
        }

        List<CategoryModel> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        for (CategoryModel item : allCategoryList) {
            if (item.getName().toLowerCase().contains(lowerQuery)) {
                filtered.add(item);
            }
        }

        allAdapter.setCategories(filtered);
    }
}
