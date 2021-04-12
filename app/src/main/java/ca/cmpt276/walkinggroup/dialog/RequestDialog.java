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
 * Dialogue Box takes in no system inputs
 * Shows to the screen 2 buttons
 * one button communicates the addition of a member to a group
 * the other button ends this dialogue popup
 */

public class RequestDialog extends android.support.v4.app.DialogFragment  {
    private static final String TAG = "RequestDialog";

    public RequestDialog.OnInputSelected mOnInputSelected;

    //widgets
    private EditText mInputDescription;
    private TextView mActionAccept, mActionDeny;

    public interface OnInputSelected{
        void sendRequestAccept();
        void sendRequestDeny();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_request, container, false);

        mActionAccept = view.findViewById(R.id.request_accept);
        mActionDeny = view.findViewById(R.id.request_deny);

        mActionAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");

                mOnInputSelected.sendRequestAccept();

                getDialog().dismiss();
            }
        });


        mActionDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input.");

                mOnInputSelected.sendRequestDeny();

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputSelected = (RequestDialog.OnInputSelected) getTargetFragment();

        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }
    }

    public static String getDialogTag() {
        return TAG;
    }
}
