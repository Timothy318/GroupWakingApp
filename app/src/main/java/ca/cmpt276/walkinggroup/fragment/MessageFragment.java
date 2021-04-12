package ca.cmpt276.walkinggroup.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import java.util.List;
 
import ca.cmpt276.walkinggroup.dialog.DeleteMessageDialog;
import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.Message;
import ca.cmpt276.walkinggroup.model.MessageCollection;
import ca.cmpt276.walkinggroup.model.User;

/**
 * This class is responsible for controlling the incoming
 * messages and list them. It also allows the user to read
 * and delete them whenever interested.
 *
 * Takes in list of messages from server
 * displays list of messages to the screen for the user
 * allows removal of messages from the fragment
 */
public class MessageFragment extends Fragment implements
        DeleteMessageDialog.onInputSelected{

    private static final int DELETE_MSG_CODE = 61874;
    private static final String ERROR_DESCRIPTION = "ERROR RETRIEVING DESCRIPTION";
    private static final String TAG = "MessageFragment";

    private ListView messageListView;
    private static User loggedUser = new User();
    private static Message selectedMessage = new Message();

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

        setupListView();
        updateUi();

        initListLongClickCallback();
        setOnClickListener();
    }

    private void updateUi(){
        initLoggedUser();
        updateList();
    }

    private void initLoggedUser() {
        ServerHandler.getUserWithEmail(
                getActivity(),
                this::receiveAllMessages,
                PreferenceHandler.getInstance()
                        .getLoggedInUserEmail(getActivity()));
    }

    private void receiveAllMessages(User user) {
        loggedUser = user;
        ServerHandler.getAllMessages(
                getActivity(),
                this::responseSetLoggedUserMonitoringList);
    }

    private void responseSetLoggedUserMonitoringList(List<Message> messageList) {
        loggedUser.setUnreadMessages(messageList);
    }

    private void updateList() {
        getMessages();
    }

    private void responseGetUpdatedList(List<Message> messageList) {
        MessageCollection.getInstance().setMessageList(messageList);
        populateListView();
    }

    private void getMessages() {
        ServerHandler.getAllMessagesForUser(
                getActivity(),
                this::responseAddUnreadMessages,
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity()),
                "unread"
        );
    }

    private void responseAddUnreadMessages(List<Message> messages) {
        for (int i = 0; i < messages.size(); i++) {
            messages.get(i).setText("U: " + messages.get(i).getText());
        }

        MessageCollection.getInstance().setMessageList(messages);

        ServerHandler.getAllMessagesForUser(
                getActivity(),
                this::responseGetReadMessages,
                PreferenceHandler.getInstance().getLoggedInUserId(getActivity()),
                "read"
        );
    }

    private void responseGetReadMessages(List<Message> messages) {
        for(Message message : messages) {
            MessageCollection.getInstance().addMessageToCollection(message);
        }

        populateListView();
    }

    private void populateListView(){
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_of_groups,
                getMessageDescriptions(MessageCollection.getInstance().getMessageList()));

        messageListView.setAdapter(adapter);
    }

    public String[] getMessageDescriptions(List<Message> messageList) {
        String[] descriptions = new String[messageList.size()];
        int i = 0;

        for (Message message : messageList) {
            if (message.getText() == null) {
                descriptions[i] = ERROR_DESCRIPTION;
            }
            else {
                descriptions[i] = message.getText();
            }

            i++;
        }

        return descriptions;
    }

    private void setupListView(){
        Log.i(TAG, "Inside setupListView function.");
            messageListView = (ListView) this.getView().findViewById(R.id.msgListViewId);
    }


    private void initListLongClickCallback() {
        MessageFragment context = MessageFragment.this;

        messageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                selectedMessage = MessageCollection
                        .getInstance()
                        .getMessage(pos);

                startDeleteMessageDialog();

                return true;
            }
        });
    }

    private void startDeleteMessageDialog() {
        DeleteMessageDialog dialog = new DeleteMessageDialog();
        dialog.setTargetFragment(MessageFragment.this, DELETE_MSG_CODE);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), DeleteMessageDialog.getDialogTag());
    }

    private void setOnClickListener() {
        MessageFragment context = MessageFragment.this;

        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMessage = MessageCollection
                        .getInstance()
                        .getMessage(position);

                String message = selectedMessage.getText();
                String[] splitMsg = message.split("\\s+");

                if (splitMsg[0].equals("U:")) {
                    String newMessage = "";

                    for (int i = 1; i < splitMsg.length; i++) {
                        newMessage += splitMsg[i] + " ";
                    }

                    selectedMessage.setText(newMessage);

                    MessageCollection.getInstance().getMessageList().set(position, selectedMessage);

                    ServerHandler.markMessage(
                            getActivity(),
                            context::responseMarkMessage,
                            selectedMessage.getId(),
                            PreferenceHandler.getInstance().getLoggedInUserId(getActivity()),
                            true
                    );

                    populateListView();
                }
            }
        });
    }

    private void responseMarkMessage(User user) {
        // do nothing
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    public static Intent makeIntentForMessaging(Context context) {
        return new Intent(context, MessageFragment.class);
    }

    @Override
    public void sendDeleteInput(){
        ServerHandler.deleteMessage(getActivity(), this::response, selectedMessage.getId());
    }

    private void response(Void nothing){
        updateUi();
    }
}
