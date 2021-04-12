package ca.cmpt276.walkinggroup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.cmpt276.walkinggroup.dialog.AddMonitorDialog;
import ca.cmpt276.walkinggroup.dialog.DeleteUserDialog;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.User;
import ca.cmpt276.walkinggroup.model.UserCollection;

//import android.app.Fragment;


/**
 * Can view and interact with the list of people who you monitor, and the people that monitor
 * you.
 *
 * Can hold-click on a user to prompt a delete from your list
 *
 * Takes in list of those the user is monitoring and those who are monitoring the user from the server
 * Displays the above lists to the user
 * allows users to add more guardians to themselves
 * allows users to begin monitoring children
 */

public class MonitorFragment extends Fragment implements AddMonitorDialog.OnInputSelected,
        DeleteUserDialog.OnDeleteSelected{
    private static final String TAG = "MonitorFragment";
    private static final int MONITORED_BY_REQUEST_CODE = 1;
    private static final int MONITORING_REQUEST_CODE = 2;

    private static ListView monitoredByList;
    private static ListView monitoringList;
    private static ArrayAdapter<String> monitoredAdapter;
    private static ArrayAdapter<String> monitoringAdapter;

    private static Button monitoredByDialog;
    private static Button monitoringDialog;

    private static User userToDelete;

    private static boolean requestingMonitoredUser = false;
    private static boolean requestingMonitoringUser = false;

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

        setupMonitorLists();
        setupAdapters();

        setupDialog();

        initListLongClickCallBacks();

        updateLists();
    }

    @Override
    public void sendMonitorInput(String input) {
        ServerHandler.getUserWithEmail(getActivity(),
                this::responseGetUserWithEmail,
                input);
    }

    @Override
    public void sendDeleteInput() {
        if (requestingMonitoringUser) {
            ServerHandler.deleteUserFromMonitoring(
                    getActivity(),
                    this::onUserDeleteFromList,
                    userToDelete);

            requestingMonitoringUser = false;
        }
        else if (requestingMonitoredUser) {
            ServerHandler.deleteUserFromMonitoredBy(
                    getActivity(),
                    this::onUserDeleteFromList,
                    userToDelete);

            requestingMonitoredUser = false;
        }
    }

    @Override
    public void sendDeleteCancel() {
        requestingMonitoredUser = false;
        requestingMonitoringUser = false;
    }

    private void initListLongClickCallBacks() {
        initListLongClickCallbackMonitoredBy();
        initListLongClickCallbackMonitoring();
    }

    private void initListLongClickCallbackMonitoredBy() {
        ListView list = getView().findViewById(R.id.listViewTopPanel);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                userToDelete = UserCollection.getInstance().getMonitoredByList().get(pos);

                requestingMonitoredUser = true;

                DeleteUserDialog dialog = new DeleteUserDialog();
                dialog.setTargetFragment(MonitorFragment.this, MONITORED_BY_REQUEST_CODE);

                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), DeleteUserDialog.getDialogTag());

                return true;
            }
        });
    }

    private void initListLongClickCallbackMonitoring() {
        ListView list = getView().findViewById(R.id.listViewBottomPanel);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                userToDelete = UserCollection.getInstance().getMonitoringList().get(pos);

                requestingMonitoringUser = true;

                DeleteUserDialog dialog = new DeleteUserDialog();
                dialog.setTargetFragment(MonitorFragment.this, MONITORED_BY_REQUEST_CODE);

                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), DeleteUserDialog.getDialogTag());

                return true;
            }
        });
    }

    private void onUserDeleteFromList(Void nothing) {
        updateLists();
    }

    private void responseGetUserWithEmail(User user) {
        if (requestingMonitoredUser) {
            ServerHandler.addUserToMonitoredByList(getActivity(),
                    this::responseUpdateUI,
                    user);

            requestingMonitoredUser = false;
        }
        else if (requestingMonitoringUser) {
            ServerHandler.addUserToMonitoringList(getActivity(),
                    this::responseUpdateUI,
                    user);

            requestingMonitoringUser = false;
        }
    }

    private void responseUpdateUI(List<User> users) {
        updateLists();
    }

    private List<String> getUserDescriptions(List<User> users) {
        List<String> descriptions = new ArrayList<String>();

        for (User user : users) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);

            String date;
            if (user.getLastGpsLocation().getTimestamp() != null)
                date = formatter.format(user.getLastGpsLocation().getTimestamp());
            else {
                date = "N/A";
            }

            descriptions.add(user.getName() + " - " +
                    user.getEmail() +
                    " : Seen: " +
                    date);
        }

        return descriptions;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.monitor_fragment, container, false);
    }

    private void setupAdapters() {
        setupAdapterForMonitoredByList();
        setupAdapterForMonitoringList();
    }

     private void setupAdapterForMonitoredByList() {
        monitoredAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_of_monitors,
                getUserDescriptions(UserCollection
                        .getInstance()
                        .getMonitoredByList()));

        monitoredByList.setAdapter(monitoredAdapter);
    }

    private void setupAdapterForMonitoringList() {
        monitoringAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_of_monitors,
                getUserDescriptions(UserCollection
                        .getInstance()
                        .getMonitoringList()));

        monitoringList.setAdapter(monitoringAdapter);
    }

    private void setupDialog() {
        setupMonitoredByDialog();
        setupMonitoringDialog();
    }

    private void setupMonitoredByDialog() {
        monitoredByDialog = getView().findViewById(R.id.addWatchedBy);

        monitoredByDialog.setOnClickListener((View v) -> {
            Log.d(TAG, "Opening monitoredBy dialog");
            requestingMonitoredUser = true;

            AddMonitorDialog dialog = new AddMonitorDialog();
            dialog.setTargetFragment(MonitorFragment.this, MONITORED_BY_REQUEST_CODE);

            dialog.show(getFragmentManager(), AddMonitorDialog.getDialogTag());
        });
    }

    private void setupMonitoringDialog() {
        monitoringDialog = getView().findViewById(R.id.addWatching);

        monitoringDialog.setOnClickListener((View v) -> {
            Log.d(TAG, "Opening monitoring dialog");
            requestingMonitoringUser = true;

            AddMonitorDialog dialog = new AddMonitorDialog();
            dialog.setTargetFragment(MonitorFragment.this, MONITORING_REQUEST_CODE);

            dialog.show(getFragmentManager(), AddMonitorDialog.getDialogTag());
        });
    }

    private void setupMonitorLists() {
        setupMonitoredByList();
        setupMonitoringList();
    }

    private void setupMonitoredByList() {
        monitoredByList = (ListView) getView().findViewById(R.id.listViewTopPanel);
    }

    private void setupMonitoringList() {
        monitoringList = (ListView) getView().findViewById(R.id.listViewBottomPanel);
    }

    private void updateLists() {
        updateMonitoredByList();
        updateMonitoringList();
    }

    private void updateMonitoringList() {
        ServerHandler.getMonitoringList(getActivity(), this::responseMonitoringList);
    }

    private void updateMonitoredByList() {
        ServerHandler.getMonitoredByList(getActivity(), this::responseMonitoredByList);
    }

    private void responseMonitoringList(List<User> users) {
        UserCollection.getInstance().setUsersToMonitoringList(users);

        setupAdapterForMonitoringList();

        monitoringAdapter.notifyDataSetChanged();
    }

    private void responseMonitoredByList(List<User> users) {
        UserCollection.getInstance().setUsersToMonitoredByList(users);

        setupAdapterForMonitoredByList();

        monitoredAdapter.notifyDataSetChanged();
    }
}
