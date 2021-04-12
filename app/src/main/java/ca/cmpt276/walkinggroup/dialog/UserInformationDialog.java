package ca.cmpt276.walkinggroup.dialog;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.User;

/**
 * Shows user information of a specific user
 *
 * Takes in a UserId as system input
 * loads a user from the server and displays to the screen
 *      all the information associated with the user save the id
 */

public class UserInformationDialog extends android.support.v4.app.DialogFragment {
    private static final String TAG = "UserInformationDialog";
    private static final String USER_ID_TAG = "USER_ID_TAG_431";

    public static UserInformationDialog newInstance(Long userId) {
        UserInformationDialog instance = new UserInformationDialog();

        Bundle args = new Bundle();
        args.putLong(USER_ID_TAG, userId);
        instance.setArguments(args);

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.user_information_dialog, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        displayUserInformation();
        setupOkButton();
    }

    private void displayUserInformation() {
        ServerHandler.getUserWithId(
                getActivity(),
                this::responseSetTargetUserInformation,
                getArguments().getLong(USER_ID_TAG)
        );
    }

    private void setupOkButton() {
        Button mActionCancel = getView().findViewById(R.id.userInformationLayout_ok);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void responseSetTargetUserInformation(User user) {
        TextView name = getView().findViewById(R.id.userInformationLayout_userName);
        TextView email = getView().findViewById(R.id.userInformationLayout_userEmail);
        TextView contactInfo = getView().findViewById(R.id.userInformationLayout_emergencyContact);
        TextView phone = getView().findViewById(R.id.userInformationLayout_userPhone);

        name.setText(user.getName());
        email.setText(user.getEmail());
        contactInfo.setText((user.getEmergencyContactInfo()));
        phone.setText(user.getHomePhone());

        if (user.getCellPhone() != null)
            if (!user.getCellPhone().equals("")) {
            phone.setText(user.getCellPhone());
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public static String getDialogTag() {
        return TAG;
    }

}
