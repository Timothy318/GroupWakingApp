package ca.cmpt276.walkinggroup.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276walkinggroupproject.walkinggroup.R;

/**
 * Dialog that pops up when the user tries to add a user to MonitoredBy or
 * Monitoring lists
 *
 * Dialogue Box takes in no system inputs
 * Shows to the screen an EditText and 2 buttons
 * takes user input into the EditText
 * one button communicates the creation of a monitor of the current user to the server
 * the other button ends this dialogue popup
 */

public class AddMonitorDialog extends android.support.v4.app.DialogFragment {
    private static final String TAG = "AddMonitorDialog";

    public AddMonitorDialog.OnInputSelected mOnInputSelected;

    //widgets
    private EditText mInputemail;
    private TextView mActionOk, mActionCancel;

    public interface OnInputSelected{
        void sendMonitorInput(String input);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_monitor, container, false);

        mActionOk = view.findViewById(R.id.action_ok);
        mActionCancel = view.findViewById(R.id.action_cancel);
        mInputemail = view.findViewById(R.id.input_email);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });


        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input.");

                String input = mInputemail.getText().toString();

                if(!input.equals("")) {
                    mOnInputSelected.sendMonitorInput(input);

                }else{
                    Toast.makeText(getContext(),"Name or Email cannot be blank",Toast.LENGTH_LONG).show();
                }

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputSelected = (AddMonitorDialog.OnInputSelected) getTargetFragment();

        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }
    }

    public static String getDialogTag() {
        return TAG;
    }
}
