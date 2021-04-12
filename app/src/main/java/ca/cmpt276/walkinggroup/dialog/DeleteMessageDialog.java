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
import android.widget.TextView;

import com.cmpt276walkinggroupproject.walkinggroup.R;

/**
 * Dialogue Box for deleting messages
 *
 * Dialogue Box takes in no system inputs
 * Shows to the screen 2 buttons
 * one button communicates the deletion of a message to the server
 * the other button ends this dialogue popup
 */
public class DeleteMessageDialog extends  android.support.v4.app .DialogFragment{
    private static final String TAG = "DeleteMessageDialog";
    private static final String MESSAGE_ID_TAG = "id";

    public DeleteMessageDialog.onInputSelected onInputSelected;

    public interface onInputSelected {
        void sendDeleteInput();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.dialog_delete_user_message, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();

        setupCancelDialogButton();
        setupDeleteDialogButton();
    }

    private void setupCancelDialogButton(){
        TextView actionCancel = getView().findViewById(R.id.action_cancel_btn_id);
        actionCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getDialog().dismiss();
            }
        });
    }

    private void setupDeleteDialogButton(){
        TextView actionOk = getView().findViewById(R.id.action_delete_btn_id);
        actionOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onInputSelected.sendDeleteInput();
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            onInputSelected = (DeleteMessageDialog.onInputSelected) getTargetFragment();
        }
        catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage());
        }
    }

    public static String getDialogTag(){
        return TAG;
    }

    public static DeleteMessageDialog newInstance(Long groupId) {
        DeleteMessageDialog instance = new DeleteMessageDialog();

        Bundle args = new Bundle();
        args.putLong(MESSAGE_ID_TAG, groupId);
        instance.setArguments(args);

        return instance;
    }
}

