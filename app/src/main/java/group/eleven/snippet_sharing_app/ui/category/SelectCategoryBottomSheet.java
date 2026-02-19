package group.eleven.snippet_sharing_app.ui.category;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import group.eleven.snippet_sharing_app.model.CategoryModel;

public class SelectCategoryBottomSheet extends BottomSheetDialogFragment {

    public static final String REQUEST_KEY = "request_category_selection";
    public static final String ARG_SELECTED_ID = "arg_selected_id";

    private RecyclerView rvRecent, rvAll;
    private CategoryAdapter recentAdapter, allAdapter;
    private EditText etSearch;
    private String currentSelectedId;

    // Data - Static for simple in-memory persistence across fragment recreation
    private static List<CategoryModel> allRecentList = new ArrayList<>();
    private static List<CategoryModel> allCategoryList = new ArrayList<>();

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

                // Height 85%
                android.util.DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int height = (int) (displayMetrics.heightPixels * 0.85);
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
        loadData();
        setupListeners(view);
    }

    private void initViews(View view) {
        rvRecent = view.findViewById(R.id.rvRecentCategories);
        rvAll = view.findViewById(R.id.rvAllCategories);
        etSearch = view.findViewById(R.id.etSearchCategories);

        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAll.setLayoutManager(new LinearLayoutManager(getContext()));

        recentAdapter = new CategoryAdapter(this::onCategorySelected);
        allAdapter = new CategoryAdapter(this::onCategorySelected);

        rvRecent.setAdapter(recentAdapter);
        rvAll.setAdapter(allAdapter);
    }

    private void loadData() {
        // Only load if empty (mock persistence)
        if (!allRecentList.isEmpty() || !allCategoryList.isEmpty()) {
            updateAdaptersSelection();
            // Ensure adapters have data
            recentAdapter.setCategories(new ArrayList<>(allRecentList));
            allAdapter.setCategories(new ArrayList<>(allCategoryList));
            return;
        }

        // Mock Data - Representing a fetch from a Repository
        allRecentList.clear();
        allCategoryList.clear();

        // Recent
        allRecentList.add(new CategoryModel("1", "Utilities", 12, R.drawable.ic_folder, true));
        allRecentList.add(new CategoryModel("2", "UI Components", 45, R.drawable.ic_folder, true));
        allRecentList.add(new CategoryModel("3", "API Hooks", 8, R.drawable.ic_folder, true));

        // All Categories
        allCategoryList.add(new CategoryModel("4", "JavaScript Basics", 156, R.drawable.ic_code, false));
        allCategoryList.add(new CategoryModel("5", "CSS Animations", 32, R.drawable.ic_code, false));
        allCategoryList.add(new CategoryModel("6", "Database Queries", 18, R.drawable.ic_database, false));
        allCategoryList.add(new CategoryModel("7", "DevOps Scripts", 24, R.drawable.ic_terminal, false));
        allCategoryList.add(new CategoryModel("8", "Testing", 40, R.drawable.ic_shield_check, false));
        allCategoryList.add(new CategoryModel("9", "React Hooks", 12, R.drawable.ic_code, false));
        allCategoryList.add(new CategoryModel("10", "Python Utils", 5, R.drawable.ic_folder, false));

        // Initial set
        recentAdapter.setCategories(new ArrayList<>(allRecentList));
        allAdapter.setCategories(new ArrayList<>(allCategoryList));

        updateAdaptersSelection();
    }

    private void updateAdaptersSelection() {
        recentAdapter.setSelectedCategoryId(currentSelectedId);
        allAdapter.setSelectedCategoryId(currentSelectedId);
    }

    // Logic: Selection -> Update State (No Close)
    private void onCategorySelected(CategoryModel category) {
        currentSelectedId = category.getId();
        updateAdaptersSelection();
    }

    private void setupListeners(View view) {
        // Done button: Return Result -> Close
        view.findViewById(R.id.tvDone).setOnClickListener(v -> {
            Bundle result = new Bundle();
            String name = "";

            // Find name
            for (CategoryModel c : allRecentList)
                if (c.getId().equals(currentSelectedId))
                    name = c.getName();
            if (name.isEmpty()) {
                for (CategoryModel c : allCategoryList)
                    if (c.getId().equals(currentSelectedId))
                        name = c.getName();
            }

            result.putString("categoryId", currentSelectedId);
            result.putString("categoryName", name);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            dismiss();
        });

        // Search Logic
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Create New Category Action
        view.findViewById(R.id.clCreateCategory).setOnClickListener(v -> showCreateCategoryDialog());
    }

    private void showCreateCategoryDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_category, null);
        builder.setView(dialogView);

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        EditText etName = dialogView.findViewById(R.id.etNewCategoryName);
        View btnCancel = dialogView.findViewById(R.id.btnCancel);
        View btnCreate = dialogView.findViewById(R.id.btnCreate);

        // Auto focus
        etName.requestFocus();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            saveCategory(name);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveCategory(String name) {
        // Create new category object
        String id = String.valueOf(System.currentTimeMillis());
        CategoryModel newCategory = new CategoryModel(id, name, 0, R.drawable.ic_folder, true);

        // Add to BEGINNING of recent list (Static list)
        allRecentList.add(0, newCategory);

        // UpdateUI if visible
        if (etSearch != null)
            etSearch.setText("");

        // Refresh adapters with new list copy
        recentAdapter.setCategories(new ArrayList<>(allRecentList));

        // Select the new category automatically
        currentSelectedId = id;
        updateAdaptersSelection();

        // Scroll to top
        if (rvRecent != null)
            rvRecent.scrollToPosition(0);

        Toast.makeText(getContext(), "Category created: " + name, Toast.LENGTH_SHORT).show();
    }

    private void filter(String query) {
        if (query.isEmpty()) {
            // Restore full lists
            recentAdapter.setCategories(new ArrayList<>(allRecentList));
            allAdapter.setCategories(new ArrayList<>(allCategoryList));
            return;
        }

        List<CategoryModel> filteredRecent = new ArrayList<>();
        List<CategoryModel> filteredAll = new ArrayList<>();

        String lowerQuery = query.toLowerCase().trim();

        for (CategoryModel item : allRecentList) {
            if (item.getName().toLowerCase().contains(lowerQuery)) {
                filteredRecent.add(item);
            }
        }

        for (CategoryModel item : allCategoryList) {
            if (item.getName().toLowerCase().contains(lowerQuery)) {
                filteredAll.add(item);
            }
        }

        recentAdapter.setCategories(filteredRecent);
        allAdapter.setCategories(filteredAll);
    }
}
