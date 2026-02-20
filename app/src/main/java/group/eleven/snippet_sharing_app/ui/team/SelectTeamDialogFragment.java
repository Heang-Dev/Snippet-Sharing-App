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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.api.ApiClient;
import group.eleven.snippet_sharing_app.api.ApiService;
import group.eleven.snippet_sharing_app.data.model.ApiResponse;
import group.eleven.snippet_sharing_app.data.model.Team;
import group.eleven.snippet_sharing_app.data.model.TeamsResponse;
import group.eleven.snippet_sharing_app.model.TeamModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectTeamDialogFragment extends DialogFragment {

    public static final String REQUEST_KEY = "request_team_selection";
    public static final String ARG_SELECTED_ID = "arg_selected_team_id";

    private RecyclerView rvTeams;
    private TeamAdapter adapter;
    private EditText etSearch;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private String selectedTeamId = null;

    private List<TeamModel> allTeams = new ArrayList<>();
    private ApiService apiService;

    public static SelectTeamDialogFragment newInstance(String selectedId) {
        SelectTeamDialogFragment fragment = new SelectTeamDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_ID, selectedId);
        fragment.setArguments(args);
        return fragment;
    }

    // Backwards compatibility
    public static SelectTeamDialogFragment newInstance(int selectedId) {
        return newInstance(selectedId > 0 ? String.valueOf(selectedId) : null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTeamId = getArguments().getString(ARG_SELECTED_ID);
        }
        apiService = ApiClient.getApiService(requireContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        loadTeamsFromApi();
        setupListeners(view);
    }

    private void initViews(View view) {
        rvTeams = view.findViewById(R.id.rvTeams);
        etSearch = view.findViewById(R.id.etSearchTeams);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        rvTeams.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeamAdapter(this::onTeamClick);
        rvTeams.setAdapter(adapter);
    }

    private void loadTeamsFromApi() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
        rvTeams.setVisibility(View.GONE);

        apiService.getMyTeams().enqueue(new Callback<ApiResponse<TeamsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TeamsResponse>> call, Response<ApiResponse<TeamsResponse>> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    TeamsResponse teamsData = response.body().getData();
                    List<Team> teams = teamsData != null ? teamsData.getAllTeams() : new ArrayList<>();
                    if (!teams.isEmpty()) {
                        allTeams.clear();
                        for (Team team : teams) {
                            TeamModel model = new TeamModel(
                                    team.getId().hashCode(),
                                    team.getName(),
                                    team.getMemberCount(),
                                    getDefaultTeamColor(),
                                    getTeamAbbreviation(team.getName())
                            );
                            model.setActualId(team.getId());
                            if (team.getId().equals(selectedTeamId)) {
                                model.setSelected(true);
                            }
                            allTeams.add(model);
                        }
                        rvTeams.setVisibility(View.VISIBLE);
                        adapter.setTeams(new ArrayList<>(allTeams));
                    } else {
                        showEmptyState();
                    }
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TeamsResponse>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                showEmptyState();
            }
        });
    }

    private int getDefaultTeamColor() {
        return Color.parseColor("#6366F1");
    }

    private void showEmptyState() {
        rvTeams.setVisibility(View.GONE);
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("No teams found. Join a team first.");
        }
    }

    private int parseColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) {
            return Color.parseColor("#6366F1");
        }
        try {
            return Color.parseColor(colorStr);
        } catch (Exception e) {
            return Color.parseColor("#6366F1");
        }
    }

    private String getTeamAbbreviation(String name) {
        if (name == null || name.isEmpty()) return "??";
        String[] words = name.split("\\s+");
        if (words.length >= 2) {
            return (words[0].substring(0, 1) + words[1].substring(0, 1)).toUpperCase();
        }
        return name.length() >= 2 ? name.substring(0, 2).toUpperCase() : name.toUpperCase();
    }

    private void onTeamClick(TeamModel team) {
        selectedTeamId = team.getActualId();
        for (TeamModel t : allTeams) {
            t.setSelected(t.getActualId() != null && t.getActualId().equals(selectedTeamId));
        }
        adapter.notifyDataSetChanged();
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.ivClose).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            if (selectedTeamId != null) {
                Bundle result = new Bundle();
                result.putString("teamId", selectedTeamId);
                for (TeamModel t : allTeams) {
                    if (t.getActualId() != null && t.getActualId().equals(selectedTeamId)) {
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String query) {
        if (query.isEmpty()) {
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
        adapter.setTeams(filtered);
    }
}
