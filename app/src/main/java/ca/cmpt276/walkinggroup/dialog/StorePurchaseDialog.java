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

import ca.cmpt276.walkinggroup.model.Titles;

/**
 * This class will provide the dialog for StoreFragment class.
 * It will pop up a dialog asking the user if they want to buy
 * the title they are interested in. The user can either accept
 * or deny the dialog.
 *
 * takes in no system input
 * displays an option to allow the user to buy a title and sends data to the server
 */

public class StorePurchaseDialog extends android.support.v4.app.DialogFragment{
    private static final String TAG = "StoreFragmentDialog";
    private int positionOfDescription;
    public StorePurchaseDialog.onInputSelected onInputSelected;


    public interface onInputSelected {
        void setDeleteInputPosition(int positionOfDescription);
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
        setupDeleteDialogButton();
    }

    @SuppressLint("SetTextI18n")
    private void setupTextViewQuestion(){
        if(positionOfDescription < Titles.getInstance().getLockedScoreList().size()){
            TextView textViewQuestion = getView().findViewById(R.id.request_heading);
            textViewQuestion.setText("Do you want to purchase the title? It costs "
                    + Titles.getInstance().getLockedScoreList().get(positionOfDescription) + " points.");
        }
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

    private void setupDeleteDialogButton(){
        TextView actionAccept = getView().findViewById(R.id.request_accept);
        actionAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onInputSelected.setDeleteInputPosition(positionOfDescription);
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            onInputSelected = (StorePurchaseDialog.onInputSelected) getTargetFragment();
        }
        catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage());
        }
    }

    public static String getDialogTag(){
        return TAG;
    }
}
