package ca.cmpt276.walkinggroup.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.User;

/**
 * User can edit their accounts from this Fragment
 *
 * Takes in a UserId as system input
 * loads a user from the server and displays to the screen
 *      all the information associated with the user save the id
 * it allows the User to change data on the account
 * it will communicate to the sever any changes if the user presses the save button
 */

public class AccountFragment extends Fragment {
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText birthYearInput;
    private EditText birthMonthInput;
    private EditText addressInput;
    private EditText homePhoneInput;
    private EditText cellPhoneInput;
    private EditText emailInput;
    private EditText gradeInput;
    private EditText teacherInput;
    private EditText emergencyContactInput;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // TODO: Retrieve data from saved instance
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.account_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        setupFieldsWithLoggedeUserInfo();
        setupOnClickListenerForUpdateBtn();
    }

    private void setupFieldsWithLoggedeUserInfo() {
        ServerHandler.getUserWithId(getActivity(), this::responseSetupLoggedUser,
                PreferenceHandler
                .getInstance()
                .getLoggedInUserId(getActivity()));
    }

    private void responseSetupLoggedUser(User user){
        setupFieldsWithUser(user);
    }

    private void setupFieldsWithUser(User user) {
        setupNameInputField(user);
        setupBirthdayInputField(user);
        setupAddressInputField(user);
        setupPhoneNumberInputField(user);
        setupEmailInputField(user);
        setupGradeInputField(user);
        setupTeacherInputField(user);
        setupEmergencyContactInputField(user);
    }

    private void setupNameInputField(User user) {
        firstNameInput = getView().findViewById(R.id.editAccount_firstNameInputId);
        lastNameInput = getView().findViewById(R.id.editAccount_lastNameInputId);

        if (user.getName() != null) {
            String[] name = user.getName().split("\\s+");

            firstNameInput.setText(name[0], TextView.BufferType.EDITABLE);
            lastNameInput.setText(name[1], TextView.BufferType.EDITABLE);
        }
    }

    private void setupBirthdayInputField(User user) {
        birthYearInput = getView().findViewById(R.id.editAccount_birthYearInput);
        birthMonthInput = getView().findViewById(R.id.editAccount_birthMonthInput);

        if (user.getBirthYear() != null)
            birthYearInput.setText(String.valueOf(user.getBirthYear()), TextView.BufferType.EDITABLE);

        if (user.getBirthMonth() != null)
            birthMonthInput.setText(String.valueOf(user.getBirthMonth()), TextView.BufferType.EDITABLE);

    }

    private void setupAddressInputField(User user) {
        addressInput = getView().findViewById(R.id.editAccount_addressInput);

        if (user.getAddress() != null)
            addressInput.setText(user.getAddress(), TextView.BufferType.EDITABLE);
    }

    private void setupPhoneNumberInputField(User user) {
        homePhoneInput = getView().findViewById(R.id.editAccount_homePhoneInput);
        cellPhoneInput = getView().findViewById(R.id.editAccount_cellPhoneInput);

        if (user.getHomePhone() != null)
            homePhoneInput.setText(user.getHomePhone(), TextView.BufferType.EDITABLE);

        if (user.getCellPhone() != null)
            cellPhoneInput.setText(user.getCellPhone(), TextView.BufferType.EDITABLE);
    }

    private void setupEmailInputField(User user) {
        emailInput = getView().findViewById(R.id.editAccount_emailInput);

        if (user.getEmail() != null)
            emailInput.setText(user.getEmail(), TextView.BufferType.EDITABLE);
    }

    private void setupGradeInputField(User user) {
        gradeInput = getView().findViewById(R.id.editAccount_gradeInput);

        if (user.getGrade() != null)
            gradeInput.setText(user.getGrade(), TextView.BufferType.EDITABLE);
    }

    private void setupTeacherInputField(User user) {
        teacherInput = getView().findViewById(R.id.editAccount_teacherInput);

        if (user.getTeacherName() != null)
            teacherInput.setText(user.getTeacherName(), TextView.BufferType.EDITABLE);
    }

    private void setupEmergencyContactInputField(User user) {
        emergencyContactInput = getView().findViewById(R.id.editAccount_emergencyContactInfoInput);

        if (user.getEmergencyContactInfo() != null)
            emergencyContactInput.setText(user.getEmergencyContactInfo(), TextView.BufferType.EDITABLE);
    }

    private String getStringFromField(EditText edit) {
        String retVal = null;

        if (edit.getText() != null) {
            retVal = edit.getText().toString();
        }

        return retVal;
    }

    private Integer getIntegerFromField(EditText edit) {
        Integer retVal = null;

        if (edit.getText() != null) {
            retVal = Integer.parseInt(edit.getText().toString());
        }

        return retVal;
    }

    private void setupOnClickListenerForUpdateBtn() {
        Button button = getActivity().findViewById(R.id.btn_updateAccountInfo);


        AccountFragment context = AccountFragment.this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerHandler.editUserWIthId(
                        getActivity(),
                        context::responseUpdateUser,
                        PreferenceHandler.getInstance().getLoggedInUserId(getActivity()),
                        getUserFromFields());
            }
        });
    }

    private void responseUpdateUser(User user) {
        // Not necessary, but making sure x2
        setupFieldsWithUser(user);
        Toast.makeText(getActivity(), "Account Updated", Toast.LENGTH_SHORT).show();
    }

    private User getUserFromFields() {
        User user = new User();

        user.setName(getStringFromField(firstNameInput) + " " + getStringFromField(lastNameInput));

        user.setBirthYear(getIntegerFromField(birthYearInput));
        user.setBirthMonth(getIntegerFromField(birthMonthInput));

        user.setAddress(getStringFromField(addressInput));

        user.setHomePhone(getStringFromField(homePhoneInput));
        user.setCellPhone(getStringFromField(cellPhoneInput));

        user.setEmail(getStringFromField(emailInput));

        user.setGrade(getStringFromField(gradeInput));

        user.setTeacherName(getStringFromField(teacherInput));

        user.setEmergencyContactInfo(getStringFromField(emergencyContactInput));

        return user;
    }
}
