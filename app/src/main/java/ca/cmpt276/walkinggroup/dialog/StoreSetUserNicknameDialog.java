package ca.cmpt276.walkinggroup.dialog;

import android.annotation.SuppressLint;
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
 * Asks the user which nickname out of the unlocked list
 * view would they like to select.
 *
 * Takes in no system inputs
 * displays 2 buttons to the screen
 * takes user input and relays it to the server
 *
 */

public class StoreSetUserNicknameDialog extends android.support.v4.app.DialogFragment{
    private static final String TAG = "StoreFragmentDialog";
    private int positionOfDescription;
    public StoreSetUserNicknameDialog.onInputSelected onInputSelected;

    public interface onInputSelected {
        void setNicknamePosition(int positionOfDescription);
    }

    public void setDescriptionPosition(int positionOfDescription) {
        this.positionOfDescription = positionOfDescription;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.dialog_store_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        setupTextViewQuestion();
        setupCancelDialogButton();
        setupNickNameApproveButton();
    }

    @SuppressLint("SetTextI18n")
    private void setupTextViewQuestion(){
        TextView textViewQuestion = getView().findViewById(R.id.request_heading);
        textViewQuestion.setText("Do you want to set this title as your nickname?");
    }

    private void setupCancelDialogButton(){
        TextView actionDeny = getView().findViewById(R.id.request_deny);
        actionDeny.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getDialog().dismiss();
            }
        });
    }

    private void setupNickNameApproveButton(){
        TextView actionAccept = getView().findViewById(R.id.request_accept);
        actionAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onInputSelected.setNicknamePosition(positionOfDescription);
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            onInputSelected = (StoreSetUserNicknameDialog.onInputSelected) getTargetFragment();
        }
        catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage());
        }
    }

    public static String getDialogTag(){
        return TAG;
    }
}
