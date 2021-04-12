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

import java.util.List;
import java.util.Objects;

import ca.cmpt276.walkinggroup.dialog.AddMemberDialog;
import ca.cmpt276.walkinggroup.dialog.GroupEditDialog;
import ca.cmpt276.walkinggroup.dialog.DeleteGroupDialog;
import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.GroupCollection;
import ca.cmpt276.walkinggroup.model.User;

/**
 * Can see and interact with all the groups that the user is current a part of
 *
 * Takes in list of groups from the server
 * Displays a list of groups to the screen
 * Lets Users decide whether or not to join groups
 * Lets the leader of any group to disband it
 */

public class GroupsFragment extends Fragment implements
        DeleteGroupDialog.OnDeleteSelected{
    private static final String TAG = "GroupsFragment";

    private static final int DELETE_GROUP_REQUEST_CODE = 39419;
    private static final int ADD_MEMBER_TO_GROUP_REQUEST_CODE = 2194;

    private static final String ERROR_DESCRIPTION = "ERROR RETRIEVING DESCRIPTION";

    private static Group selectedGroup = new Group();

    private static User loggedUser = new User();

    private static ListView groupView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // TODO: Retrieve data from saved instance
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.groups_fragment, container, false);
    }

    @Override
    public void sendDeleteInput() {
        ServerHandler.deleteGroup(getActivity(), this::response, selectedGroup);
    }

    @Override
    public void onStart() {
        super.onStart();

        setupGroupView();

        updateUi();
        initListClickCallback();
        initListLongClickCallback();
    }

    private void initLoggedUser() {
        ServerHandler.getUserWithEmail(
                getActivity(),
                this::setLoggedUser,
                PreferenceHandler.getInstance().getLoggedInUserEmail(getActivity()));
    }

    private void setLoggedUser(User user) {
        loggedUser = user;
        ServerHandler.getMonitoringList(
                getActivity(),
                this::responseSetLoggedUserMonitoringList);
    }

    private void responseSetLoggedUserMonitoringList(List<User> users) {
        loggedUser.setMonitorsUsers(users);
    }

    private void updateUi() {
        initLoggedUser();
        updateList();
    }

    private void updateList() {
        ServerHandler.getGroupsList(getActivity(), this::responseGetUpdatedList);
    }

    private void responseGetUpdatedList(List<Group> groups) {
        GroupCollection.getInstance().setGroups(groups);
        populateListView();
    }

    private void response(Void nothing) {
        updateUi();
    }

    private void setupGroupView() {
        groupView = (ListView) getView().findViewById(R.id.groupList);
    }

    private void initListClickCallback() {
        GroupsFragment context = GroupsFragment.this;

        groupView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGroup = GroupCollection
                        .getInstance()
                        .getGroup(position);

                startEditGroupDialog(selectedGroup.getId());
            }
        });
    }

    private void initListLongClickCallback() {
        GroupsFragment context = GroupsFragment.this;

        groupView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                selectedGroup = GroupCollection
                        .getInstance()
                        .getGroup(pos);

                if (doesLeaderExist(selectedGroup) && isLeaderOfGroup(selectedGroup)) {
                    startDeleteGroupDialog();
                }

                return true;
            }
        });
    }

    private boolean doesLeaderExist(Group group) {
        return group.getLeader() != null;
    }

    private boolean isLeaderOfGroup(Group group) {
        return Objects.equals(
                selectedGroup.getLeader().getId(),
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity()));
    }

    private void startEditGroupDialog(Long groupId) {
        GroupEditDialog dialog = GroupEditDialog.newInstance(groupId);
        dialog.setTargetFragment(GroupsFragment.this, ADD_MEMBER_TO_GROUP_REQUEST_CODE);

        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), GroupEditDialog.getDialogTag());
    }

    private void startDeleteGroupDialog() {
        DeleteGroupDialog dialog = new DeleteGroupDialog();
        dialog.setTargetFragment(GroupsFragment.this, DELETE_GROUP_REQUEST_CODE);

        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), DeleteGroupDialog.getDialogTag());
    }

    private void populateListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_of_groups,
                getGroupDescriptions(GroupCollection
                        .getInstance()
                        .getGroups())
        );

        groupView.setAdapter(adapter);
    }

    public String[] getGroupDescriptions(List<Group> groups) {
        String[] descriptions = new String[groups.size()];
        int i = 0;

        for (Group group : groups) {
            if (group.getGroupDescription() == null)
                descriptions[i] = ERROR_DESCRIPTION;
            else
                descriptions[i] = group.getGroupDescription();

            i++;
        }

        return descriptions;
    }
}
