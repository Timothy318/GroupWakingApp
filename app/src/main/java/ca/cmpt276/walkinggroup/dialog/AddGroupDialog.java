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
 * Prompt when the user chooses to add a group
 *
 * Dialogue Box takes in no inputs
 * Shows to the screen an EditText and 2 buttons
 * takes user input into the EditText
 * one button communicates the creation of a group to the server
 * the other button ends this dialogue popup
 */

public class AddGroupDialog extends android.support.v4.app.DialogFragment  {
    private static final String TAG = "AddGroupDialog";

    public AddGroupDialog.OnInputSelected mOnInputSelected;

    //widgets
    private EditText mInputDescription;
    private TextView mActionOk, mActionCancel;

    public interface OnInputSelected{
        void sendGroupInput(String input);
        void sendGroupCancel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_addgroup, container, false);

        mActionOk = view.findViewById(R.id.action_addgroup_ok);
        mActionCancel = view.findViewById(R.id.action_addgroup_cancel);
        mInputDescription = view.findViewById(R.id.input_addgroup_description);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");

                mOnInputSelected.sendGroupCancel();

                getDialog().dismiss();
            }
        });


        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input.");

                String input = mInputDescription.getText().toString();

                if(!input.equals("")) {
                    mOnInputSelected.sendGroupInput(input);

                }else{
                    Toast.makeText(
                            getContext(),
                            "Group description cannot be blank",
                            Toast.LENGTH_LONG).show();
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
            mOnInputSelected = (AddGroupDialog.OnInputSelected) getTargetFragment();

        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }
    }

    public static String getDialogTag() {
        return TAG;
    }
}
