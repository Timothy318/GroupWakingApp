package ca.cmpt276.walkinggroup.dialog;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276walkinggroupproject.walkinggroup.R;

/**
 *
 *
 * Dialogue Box takes in no system inputs
 * Shows to the screen an EditText and 2 buttons
 * the TextEditor takes user input in the form of a string to be sent to other users
 * one button communicates the sending of a Group Message to the server
 * the other button ends this dialogue popup
 */
public class SendGroupMessageDialog extends android.support.v4.app.DialogFragment{
    private static final String TAG = "SendGroupMsgDialogTag";
    private static final String USER_ID_TAG = "USER_ID_TAG_220";
    public SendGroupMessageDialog.OnInputSelected inputSelected;

    private EditText messageInputEditText;

    public interface OnInputSelected{
        void setMessageInput(String message);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_send_message_to_group, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();

        setupCancelDialogButton();
        setupSendDialogButton();
    }

    private void setupCancelDialogButton() {
        TextView actionCancel = getView().findViewById(R.id.cancelGroupMsgDialogId);
        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void setupSendDialogButton() {
        TextView actionSend = getView().findViewById(R.id.sendGroupMsgDialogId);
        actionSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input.");

                messageInputEditText = getView().findViewById(R.id.sm_groupmessage);

                String messageInputVal = messageInputEditText.getText().toString();

                if(!messageInputVal.equals("")) {
                    inputSelected.setMessageInput(messageInputVal);
                }
                else{
                    Toast.makeText(getContext(),"Email or Message cannot be blank", Toast.LENGTH_LONG).show();
                }

                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            inputSelected = (SendGroupMessageDialog.OnInputSelected) getTargetFragment();
        }
        catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }
    }


    public static String getDialogTag() {
        return TAG;
    }
}