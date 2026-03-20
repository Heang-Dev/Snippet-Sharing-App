package group.eleven.snippet_sharing_app.ui.team.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import group.eleven.snippet_sharing_app.R;
import group.eleven.snippet_sharing_app.data.model.TeamJoinRequest;
import group.eleven.snippet_sharing_app.data.repository.AuthRepository;
import group.eleven.snippet_sharing_app.ui.team.TeamSettingsActivity;
import group.eleven.snippet_sharing_app.ui.team.viewmodel.TeamViewModel;

public class TeamSettingsRequestsFragment extends Fragment implements JoinRequestAdapter.OnActionListener {

    private String teamId;
    private TeamViewModel teamViewModel;
    private JoinRequestAdapter adapter;

    private RecyclerView rvJoinRequests;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof TeamSettingsActivity) {
            teamId = ((TeamSettingsActivity) getActivity()).getTeamId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_settings_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvJoinRequests = view.findViewById(R.id.rvJoinRequests);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        adapter = new JoinRequestAdapter(this);
        rvJoinRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJoinRequests.setAdapter(adapter);

        teamViewModel = new ViewModelProvider(requireActivity()).get(TeamViewModel.class);

        teamViewModel.getJoinRequestsResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                rvJoinRequests.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.GONE);
            } else if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                progressBar.setVisibility(View.GONE);
                List<TeamJoinRequest> data = resource.getData();
                if (data != null && !data.isEmpty()) {
                    adapter.setRequests(data);
                    rvJoinRequests.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                } else {
                    rvJoinRequests.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                progressBar.setVisibility(View.GONE);
                rvJoinRequests.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        teamViewModel.getHandleJoinRequestResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == AuthRepository.Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Done!", Toast.LENGTH_SHORT).show();
            } else if (resource.getStatus() == AuthRepository.Resource.Status.ERROR) {
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (teamId != null) {
            teamViewModel.loadJoinRequests(teamId);
        }
    }

    @Override
    public void onApprove(TeamJoinRequest request, int position) {
        if (teamId == null) return;
        teamViewModel.handleJoinRequest(teamId, request.getId(), "approve");
        adapter.removeAt(position);
        if (adapter.getItemCount() == 0) {
            rvJoinRequests.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        }
        Toast.makeText(getContext(), "Request approved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReject(TeamJoinRequest request, int position) {
        if (teamId == null) return;
        teamViewModel.handleJoinRequest(teamId, request.getId(), "reject");
        adapter.removeAt(position);
        if (adapter.getItemCount() == 0) {
            rvJoinRequests.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        }
        Toast.makeText(getContext(), "Request rejected.", Toast.LENGTH_SHORT).show();
    }
}
