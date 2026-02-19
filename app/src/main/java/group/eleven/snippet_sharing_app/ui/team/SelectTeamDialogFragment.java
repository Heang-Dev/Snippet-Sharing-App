package group.eleven.snippet_sharing_app.ui.team;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.model.TeamModel;

public class SelectTeamDialogFragment extends DialogFragment {

    public static final String REQUEST_KEY = "request_team_selection";
    public static final String ARG_SELECTED_ID = "arg_selected_team_id";

    private RecyclerView rvTeams;
    private TeamAdapter adapter;
    private EditText etSearch;
    private int selectedTeamId = -1;

    private List<TeamModel> allTeams = new ArrayList<>();

    public static SelectTeamDialogFragment newInstance(int selectedId) {
        SelectTeamDialogFragment fragment = new SelectTeamDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_ID, selectedId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTeamId = getArguments().getInt(ARG_SELECTED_ID, -1);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Optional: Request no title
            dialog.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_select_team, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadMockData();
        setupListeners(view);
    }

    private void initViews(View view) {
        rvTeams = view.findViewById(R.id.rvTeams);
        etSearch = view.findViewById(R.id.etSearchTeams);

        rvTeams.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeamAdapter(this::onTeamClick);
        rvTeams.setAdapter(adapter);
    }

    private void loadMockData() {
        allTeams.clear();
        // Frontend Wizards (8 members, Blue, ID: 1)
        allTeams.add(new TeamModel(1, "Frontend Wizards", 8, Color.parseColor("#2979FF"), "FE"));
        // Core Engineering (24 members, Orange, ID: 2)
        allTeams.add(new TeamModel(2, "Core Engineering", 24, Color.parseColor("#FF6D00"), "CE"));
        // Design System (12 members, Teal, ID: 3)
        allTeams.add(new TeamModel(3, "Design System", 12, Color.parseColor("#00BFA5"), "DS"));
        // QA & Testing (5 members, Purple, ID: 4)
        allTeams.add(new TeamModel(4, "QA & Testing", 5, Color.parseColor("#AA00FF"), "QA"));

        updateSelectionState();
        adapter.setTeams(new ArrayList<>(allTeams));
    }

    private void onTeamClick(TeamModel team) {
        selectedTeamId = team.getId();
        updateSelectionState();
        adapter.notifyDataSetChanged();
    }

    private void updateSelectionState() {
        for (TeamModel t : allTeams) {
            t.setSelected(t.getId() == selectedTeamId);
        }
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.ivClose).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            if (selectedTeamId != -1) {
                Bundle result = new Bundle();
                result.putInt("teamId", selectedTeamId);
                // Find selected name
                for (TeamModel t : allTeams) {
                    if (t.getId() == selectedTeamId) {
                        result.putString("teamName", t.getName());
                        break;
                    }
                }
                getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            }
            dismiss();
        });

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
    }

    private void filter(String query) {
        if (query.isEmpty()) {
            updateSelectionState(); // Ensure state is synced
            adapter.setTeams(new ArrayList<>(allTeams));
            return;
        }

        List<TeamModel> filtered = new ArrayList<>();
        String lower = query.toLowerCase();
        for (TeamModel t : allTeams) {
            if (t.getName().toLowerCase().contains(lower)) {
                filtered.add(t);
            }
        }
        // Ensure filtered items reflect selection
        for (TeamModel t : filtered) {
            t.setSelected(t.getId() == selectedTeamId);
        }
        adapter.setTeams(filtered);
    }
}
