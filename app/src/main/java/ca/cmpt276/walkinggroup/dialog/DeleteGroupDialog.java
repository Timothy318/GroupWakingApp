package ca.cmpt276.walkinggroup.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmpt276walkinggroupproject.walkinggroup.R;

/**
 * Dialog prompted when a user a hold-clicks on the list of Groups
 *
 * Dialogue Box takes in no system inputs
 * Shows to the screen 2 buttons
 * one button communicates the deletion of a group to the server
 * the other button ends this dialogue popup
 */

public class DeleteGroupDialog extends android.support.v4.app.DialogFragment{
    private static final String TAG = "DeleteGroupDialog";

    public DeleteGroupDialog.OnDeleteSelected mOnInputSelected;

    private TextView mActionOk, mActionCancel;

    public interface OnDeleteSelected{
        void sendDeleteInput();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.delete_group, container, false);

        mActionOk = view.findViewById(R.id.action_group_delete_yes);
        mActionCancel = view.findViewById(R.id.action_group_delete_cancel);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnInputSelected.sendDeleteInput();

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputSelected = (DeleteGroupDialog.OnDeleteSelected) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }
    }

    public static String getDialogTag() {
        return TAG;
    }
}
