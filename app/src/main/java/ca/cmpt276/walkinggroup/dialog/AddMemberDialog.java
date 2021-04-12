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
 * Dialog prompted when a user a hold-clicks on the list of Groups
 *
 * Dialogue Box takes in no system inputs
 * Shows to the screen an EditText and 2 buttons
 * takes user input into the EditText
 * one button communicates the addition of a new member to the group
 * the other button ends this dialogue popup
 */

public class AddMemberDialog extends android.support.v4.app.DialogFragment{
    private static final String TAG = "AddMemberDialog";

    public AddMemberDialog.OnInputSelected mOnInputSelected;

    private EditText mInputEmail;
    private TextView mActionOk, mActionCancel;

    public interface OnInputSelected{
        void sendInput(String input);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_addmember_to_group, container, false);

        mInputEmail = view.findViewById(R.id.input_addmember_description);
        mActionOk = view.findViewById(R.id.action_addmember_ok);
        mActionCancel = view.findViewById(R.id.action_addmember_cancel);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = mInputEmail.getText().toString();

                if(!input.equals("")) {
                    mOnInputSelected.sendInput(input);

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
            mOnInputSelected = (AddMemberDialog.OnInputSelected) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }
    }

    public static String getDialogTag() {
        return TAG;
    }
}
