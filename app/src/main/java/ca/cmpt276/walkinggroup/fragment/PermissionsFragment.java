package ca.cmpt276.walkinggroup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.walkinggroup.dialog.GroupEditDialog;
import ca.cmpt276.walkinggroup.dialog.RequestDialog;
import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.PermissionRequest;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

/**
 * Place to accept or deny pending permission requests
 *
 * Expected Conditions:
 * Pending: Show ONLY pending requests
 * Overview: Show accepted/pending/denied requests (ALL)
 *
 * takes in list of pending requests from the server
 * displays list of users who wish to monitor the current user and asks for permission to let them
 * displays list of users asking too monitor those the current user monitors
 * displays an overview of permissions available to the user
 */

public class PermissionsFragment extends Fragment implements RequestDialog.OnInputSelected{
    private static int REQUEST_CODDE = 50122;

    private static ListView pendingList;
    private static ListView overviewList;
    private static ArrayAdapter<String> pendingListAdapter;
    private static ArrayAdapter<String> overviewListAdapter;

    private static PermissionRequest requestSelected;

    List<PermissionRequest> pendingRequests = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // TODO: Add code here to ensure we don't override saved state
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        setupListsView();
        initOnClickListenerForPendingList();
        updateLists();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.permissions_fragment, container, false);
    }

    @Override
    public void sendRequestAccept() {
        ServerHandler.markPermissionRequest(
                getActivity(),
                this::responseRequest,
                requestSelected.getId(),
                WGServerProxy.PermissionStatus.APPROVED
        );
    }

    @Override
    public void sendRequestDeny() {
        ServerHandler.markPermissionRequest(
                getActivity(),
                this::responseRequest,
                requestSelected.getId(),
                WGServerProxy.PermissionStatus.DENIED
        );
    }

    public void responseRequest(PermissionRequest request) {
        updateLists();
    }

    private void initOnClickListenerForPendingList() {
        pendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                requestSelected = pendingRequests.get(position);

                if (Objects.equals(
                        requestSelected.getRequestingUser().getId(),
                        PreferenceHandler.getInstance().getLoggedInUserId(getActivity()))) {
                    Toast.makeText(
                            getActivity(),
                            "You can't approve your own request",
                            Toast.LENGTH_SHORT).show();
                } else {
                    startRequestDialog();
                }
            }
        });
    }

    private void startRequestDialog() {
        RequestDialog dialog = new RequestDialog();
        dialog.setTargetFragment(PermissionsFragment.this, REQUEST_CODDE);

        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), RequestDialog.getDialogTag());
    }

    private void setupListsView() {
        pendingList = getView().findViewById(R.id.listViewPendingRequests);
        overviewList = getView().findViewById(R.id.listViewOverviewRequests);
    }

    private void updateLists() {
        updatePendingList();
        updateOverviewList();
    }

    private void updatePendingList() {
        ServerHandler.getPermissionsForUserWithStatus(
                getActivity(),
                this::setupAdapterForPendingList,
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity()),
                WGServerProxy.PermissionStatus.PENDING
        );
    }

    private void updateOverviewList() {
        ServerHandler.getPermissions(
                getActivity(),
                this::setupAdapterForOverviewList
        );
    }

    private void setupAdapterForPendingList(List<PermissionRequest> requests) {
        pendingRequests = requests;

        pendingListAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_of_monitors,
                getMessageFromPermission((ArrayList<PermissionRequest>) requests));

        pendingList.setAdapter(pendingListAdapter);
    }

    private void setupAdapterForOverviewList(List<PermissionRequest> requests) {
        overviewListAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_of_monitors,
                getMessageFromPermission((ArrayList<PermissionRequest>) requests));

        overviewList.setAdapter(overviewListAdapter);
    }

    private ArrayList<String> getMessageFromPermission(ArrayList<PermissionRequest> requests) {
        ArrayList<String> temp = new ArrayList<String>();

        for (PermissionRequest request : requests) {
            temp.add(request.getMessage());
        }

        return temp;
    }
}
