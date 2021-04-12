package ca.cmpt276.walkinggroup.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;


/**
 * This class is required to allow the users to sign up
 * and have a specific account that they can later use.
 *
 * Takes no system inputs
 * takes user input to create a user
 * creates a user to send to the server
 * sends you to mainActivity upon creation
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String EMAIL_TAG = "emailisgucci";;

    private static final String TAG = "RegisterActivity";

    private WGServerProxy proxy;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

        setupTexts();
        setupRegisterButton();
        setupLoginButton();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }


    // register the user if in the register activity
    private void setupRegisterButton(){
        Button registerBtn = (Button) findViewById(R.id.registerButtonID);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        try {
            User user = setupUserFromFields();
            ServerHandler.register(RegisterActivity.this,
                    RegisterActivity.this::response,
                    user);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private User setupUserFromFields() throws IllegalArgumentException {
        User user = new User();
        user.setName(getName());
        user.setEmail(getEmail());
        user.setPassword(getPassword());

        return user;
    }

    private String getName() throws IllegalArgumentException{
        EditText text_firstName = findViewById(R.id.firstNameInputID);
        EditText text_lastName = findViewById(R.id.lastNameInputID);

        String firstName = text_firstName.getText().toString();
        String lastName = text_lastName.getText().toString();

        return firstName + " " + lastName;
    }

    private String getEmail() throws IllegalArgumentException{
        EditText email = findViewById(R.id.emailRegisterInputID);

        return email.getText().toString();
    }

    private String getPassword() throws IllegalArgumentException{
        EditText pass = findViewById(R.id.passInputRegisterTextID);

        return pass.getText().toString();
    }


    private void response(User user) {
        Intent intent = new Intent();
        intent.putExtra(EMAIL_TAG, user.getEmail());

        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    // go to the login activity
    private void setupLoginButton(){
        Button loginBtn = (Button) findViewById(R.id.backToLoginBtnID);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        setResult(Activity.RESULT_CANCELED, new Intent());

        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupTexts(){
        setupFirstNameText();
        setupLastNameText();
        setupEmailText();
        setupPasswordText();
    }

    private void setupFirstNameText() {
        TextView textview = findViewById(R.id.firstNameTextID);
        textview.setText(R.string.firstName);
    }

    private void setupLastNameText() {
        TextView textview = findViewById(R.id.lastNameTextID);
        textview.setText(R.string.lastName);
    }

    private void setupEmailText() {
        TextView textview = findViewById(R.id.emailTextID);
        textview.setText(R.string.email);
    }

    private void setupPasswordText() {
        TextView textview = findViewById(R.id.passTextID);
        textview.setText(R.string.password);
    }

    public static String getEmailTag() {
        return EMAIL_TAG;
    }
}

