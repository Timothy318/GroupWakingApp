package ca.cmpt276.walkinggroup.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cmpt276walkinggroupproject.walkinggroup.R;

import ca.cmpt276.walkinggroup.handler.PreferenceHandler;
import ca.cmpt276.walkinggroup.handler.ServerHandler;
import ca.cmpt276.walkinggroup.model.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

/**
 * This class is required to allow the user to sign in
 * to their account and be able to use the application.
 *
 * Is the starting activity takes savedInstance as input
 * displays login Screen
 * takes userInput to make a serverCall for a UserClass
 * Will Open either Register or Main activity
 */
public class LoginActivity extends AppCompatActivity{
    private static final String TAG = "LoginActivity";

    private final int REGISTER_USER_REQUEST_CODE = 420;

    private static WGServerProxy proxy;

    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceHandler.getInstance()
                .getLoggedInUserToken(LoginActivity.this) != null) {
            startActivity(MainActivity.makeIntent(LoginActivity.this));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);
        ServerHandler.setProxy(proxy);

        setupTexts();
        setupLoginButton();
        setupRegisterButton();
    }

    // goes to the sign up page.
    private void setupRegisterButton(){
        Button registerBtn = (Button) findViewById(R.id.backToSignupBtnID);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        Intent intent = RegisterActivity.makeIntent(LoginActivity.this);
        startActivityForResult(intent, REGISTER_USER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REGISTER_USER_REQUEST_CODE:
                if (resultCode == RESULT_OK)
                    fillInputFields(data);
                break;
            default:
                break;
        }
    }

    private void fillInputFields(Intent intent) {
        String email = intent.getStringExtra(RegisterActivity.getEmailTag());

        fillEmailField(email);
    }

    private void fillEmailField(String email) {
        EditText text = findViewById(R.id.emailInputID);

        text.setText(email);
    }

    private void fillPasswordField(String password) {
        EditText text = findViewById(R.id.passInputTextID);

        text.setText(password);
    }

    // the user can sign in if the user is in the login activity page
    private void setupLoginButton(){
        Button loginBtn = (Button) findViewById(R.id.loginButtonID);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = setupUserFromEmailAndPassword();

                ServerHandler.setOnReceiveToken(LoginActivity.this::onReceiveToken);
                ServerHandler.login(LoginActivity.this, LoginActivity.this::response, user);
            }
        });
    }



    private User setupUserFromEmailAndPassword() {
        User user = null;

        try {
            String email = getEmailFromInputField();
            String password = getPasswordFromInputField();

            user = new User();
            user.setEmail(email);
            user.setPassword(password);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return user;
    }

    private String getEmailFromInputField() throws IllegalArgumentException{
        EditText emailInput = findViewById(R.id.emailInputID);
        String email = emailInput.getText().toString();
        saveEmailPreference(email);

        return email;
    }

    private String getPasswordFromInputField() throws IllegalArgumentException{
        EditText passwordInput = findViewById(R.id.passInputTextID);

        return passwordInput.getText().toString();
    }

    // input some strings for the texts from string.xml file
    private void setupTexts(){
        setupEmailText();
        setupPasswordText();
    }

    private void setupEmailText() {
        TextView textview = findViewById(R.id.emailTextID);
        textview.setText(R.string.email);
    }

    private void setupPasswordText() {
        TextView textview = findViewById(R.id.passTextID);
        textview.setText(R.string.password);
    }

    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);

        saveTokenPreference(token);
    }

    private void response(Void startActivity) {
        startActivity(MainActivity.makeIntent(LoginActivity.this));

        finish();
    }

    private void saveEmailPreference(String email) {
        PreferenceHandler.getInstance()
                .saveUserEmail(LoginActivity.this, email);
    }

    private void saveTokenPreference(String token) {
        PreferenceHandler.getInstance()
                .saveUserToken(LoginActivity.this, token);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

}
