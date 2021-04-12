package ca.cmpt276.walkinggroup.dialog;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.Group;
import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.User;

/**
 * Dialog prompted when a user clicks on the list of Groups
 * https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
 *
 * Dialogue Box takes in no system inputs
 * Shows to the screen 2 buttons
 * one button communicates the deletion of a group to the server
 * the other button ends this dialogue popup
 */

public class GroupEditDialog extends android.support.v4.app.DialogFragment
        implements AddMemberDialog.OnInputSelected,
                    DeleteUserDialog.OnDeleteSelected,
                    SendGroupMessageDialog.OnInputSelected{
    private static final String TAG = "GroupEditDialog";
    private static final String GROUP_ID_TAG = "id";
    private static final int ADD_MEMBER_TO_GROUP_REQUEST_CODE = 21321421;
    private static final int DELETE_MEMBER_CODE = 21312312;
    private static final int USER_INFORMATION_CODE = 31213;
    private static final int MESSAGE_GROUP_CODE = 213921041;

    private List<String> names;
    private HashMap<String, List<String>> map;

    private List<User> monitoringUsers = new ArrayList<>();
    private List<String> monitoredByUserId = new ArrayList<>();

    private List<User> membersList = new ArrayList<User>();
    private HashMap<String, String> descriptions;

    private User selectedUser;

    private Group group;
    private Long groupId;

    public static GroupEditDialog newInstance(Long groupId) {
        GroupEditDialog instance = new GroupEditDialog();

        Bundle args = new Bundle();
        args.putLong(GROUP_ID_TAG, groupId);
        instance.setArguments(args);

        return instance;
    }

    private void initLoggedUser() {
        ServerHandler.getMonitoringList(
                getActivity(),
                this::setMonitoringList);
    }

    private void setMonitoringList(List<User> users) {
        monitoringUsers = users;
    }

    @Override
    public void sendDeleteInput() {
        ServerHandler.deleteMemberFromGroup(
                getActivity(),
                this::responseDeleteMember,
                groupId,
                selectedUser.getId()
        );
    }

    private void responseDeleteMember(Void nothing) {
        updateList(groupId);
    }

    @Override
    public void sendDeleteCancel() {
        // Do nothing
    }

    @Override
    public void sendInput(String input) {
        ServerHandler.getUserWithEmail(getActivity(), this::responseGetUserToAdd ,input);
    }

    @Override
    public void setMessageInput(String message) {
        Message msg = new Message();
        msg.setText(message);

        ServerHandler.sendMessage(
                getActivity(),
                this::responseSendGroupMessage,
                msg,
                groupId
        );
    }

    private void responseSendGroupMessage(Message message) {
        // Do nothing
    }

    private void responseGetUserToAdd(User user) {
        if (checkIfMemberIsMonitored(user) ||
                checkIfMemberIsUser(user))
            ServerHandler.addMemberToGroup(
                    getActivity(),
                    this::response,
                    groupId,
                    user);
        else
            Toast.makeText(
                    getActivity(),
                    "You are not monitoring that user",
                    Toast.LENGTH_SHORT).show();
    }

    private void response(List<User> users) {
        updateList(groupId);
    }

    private boolean checkIfMemberIsUser(User user) {
        boolean retVal = false;

        if (Objects.equals(user.getId(), PreferenceHandler.getInstance().getLoggedInUserId(getActivity()))) {
            retVal = true;
        }

        return retVal;
    }

    private boolean checkIfMemberIsMonitored(User user) {
        boolean retVal = false;

        for (User monitoringUser : monitoringUsers) {
            if (Objects.equals(
                    monitoringUser.getId(),
                    user.getId())) {

                retVal = true;
                break;
            }
        }

        return retVal;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.edit_group_dialog, container, false);

        initLoggedUser();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();

        groupId = getArguments().getLong(GROUP_ID_TAG);

        setupGroupInfo(groupId);
        updateList(groupId);

        initSendGroupMessage();
        initAddMemberBtn();
        initCancelBtn();
    }

    private void initSendGroupMessage() {
        TextView view = getView().findViewById(R.id.sendGroupMessage);
        view.setVisibility(View.VISIBLE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendGroupMessageDialog dialog = new SendGroupMessageDialog();
                dialog.setTargetFragment(GroupEditDialog.this, MESSAGE_GROUP_CODE);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), SendGroupMessageDialog.getDialogTag());
            }
        });
    }

    private void hideSendGroupMessage() {
        TextView view = getView().findViewById(R.id.sendGroupMessage);
        view.setVisibility(View.INVISIBLE);
    }

    private void updateList(Long groupId) {
        storeMembers(groupId);
    }

    private void storeMembers(Long groupId) {
        ServerHandler.getMembersFromGroup(
                getActivity(),
                this::responseStoreMembers,
                groupId
        );
    }

    private void responseStoreMembers(List<User> users) {
        populateView(users);
    }

    private void populateView(List<User> users) {
        membersList = users;

        ServerHandler.getUserWithId(
                getActivity(),
                this::responseGetLoggedUser,
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity())
        );
    }

    private void populateListView(List<User> users) {
        ExpandableListView listView = getView().findViewById(R.id.editgroup_expandablelist);
        TextView header = getView().findViewById(R.id.editgroup_heading);

        header.setText("Members");

        ExpandableListAdapter adapter = new ca.cmpt276.walkinggroup.adapter.ExpandableListAdapter(
                getActivity(),
                getUserNames(users),
                getMonitoringUserDescriptionsForMember(users)
        );

        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);

        initDeleteUser(listView);
        initViewUserInformation(listView);
    }

    private void hideListView() {
        ExpandableListView listView = getView().findViewById(R.id.editgroup_expandablelist);

        listView.setVisibility(View.GONE);
    }

    private void responseGetLoggedUser(User user) {
        if (isUserPartOfMembers(user) ||
                isUserLeaderOfGroup(user) ||
                isMonitoringUserPartOfGroup(user)) {

            initSendGroupMessage();
            populateListView(membersList);
        }
        else {
            hideListView();
            hideSendGroupMessage();

            TextView header = getView().findViewById(R.id.editgroup_heading);
            header.setText(String.format("You are not part of this group\n\n" +
                    "Please follow one of the steps:\n" +
                    "- Add a member you monitor to this group\n" +
                    "- Add yourself to this group"));
        }
    }

    private boolean isUserPartOfMembers(User user) {
        for (User member : membersList) {
            if (Objects.equals(member.getId(), user.getId())) {
                return true;
            }
        }

        return false;
    }

    private boolean isUserLeaderOfGroup(User user) {
        if (group.getLeader() != null)
            if (Objects.equals(group.getLeader().getId(), user.getId())) {
                return true;
            }

        return false;
    }

    private boolean isMonitoringUserPartOfGroup(User user) {
        for (User monitored : user.getMonitorsUsers()) {
            for (User member : membersList) {
                if (Objects.equals(member.getId(), monitored.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void setupGroupInfo(Long groupId) {
        ServerHandler.getGroupDetails(
                getActivity(),
                this::responseGetGroupInfo,
                groupId);
    }

    private void responseGetGroupInfo(Group group) {
        this.group = group;
    }

    private void initDeleteUser(ExpandableListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = membersList.get(position);

                DeleteUserDialog dialog = new DeleteUserDialog();
                dialog.setTargetFragment(GroupEditDialog.this, DELETE_MEMBER_CODE);

                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), DeleteUserDialog.getDialogTag());

                return true;
            }
        });
    }

    private void initViewUserInformation(ExpandableListView listView) {
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String value = map.get(names.get(groupPosition)).get(childPosition);

                String[] selectedId = value.split("\\s+");

                UserInformationDialog dialog = UserInformationDialog.newInstance(Long.parseLong(selectedId[2]));
                dialog.setTargetFragment(GroupEditDialog.this, USER_INFORMATION_CODE);

                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), UserInformationDialog.getDialogTag());

                return true;
            }
        });
    }

    private void initAddMemberBtn() {
        Button mActionAddMember = getView().findViewById(R.id.editGroup_addMemberBtn);

        mActionAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddMemberDialog();
            }
        });
    }

    private void initCancelBtn(){
        Button mActionCancel = getView().findViewById(R.id.editGroup_cancel);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

    }

    private void startAddMemberDialog() {
        AddMemberDialog dialog = new AddMemberDialog();
        dialog.setTargetFragment(GroupEditDialog.this, ADD_MEMBER_TO_GROUP_REQUEST_CODE);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), AddMemberDialog.getDialogTag());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static String getDialogTag() {
        return TAG;
    }

    private List<String> getUserNames(List<User> users) {
        names = new ArrayList<String>();

        for (User user : users) {
            names.add(user.getName() + " - " + user.getEmail());
        }

        return names;
    }

    private HashMap<String, List<String>> getMonitoringUserDescriptionsForMember(List<User> users){
        map = new HashMap<>();
        descriptions = new HashMap<>();

        int i = 0;
        for (User member : users) {
            monitoredByUserId = new ArrayList<>();
            for (User user : member.getMonitoredByUsers()) {
                monitoredByUserId.add("Guardian" + " - "  +  Long.toString(user.getId()));
            }

            map.put(names.get(i), monitoredByUserId);

            i++;
        }

        return map;
    }
}
