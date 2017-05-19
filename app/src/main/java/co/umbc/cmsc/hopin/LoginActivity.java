package co.umbc.cmsc.hopin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * A login screen that offers login via UserId/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    Intent mNavIntent;

    // UI references.
    private EditText mEdittextUserId;
    private EditText mEdittextPassword;
    private View mProgressView;
    private View mLoginFormView;

    private static final String TAG = "LoginActivity";

    /**
     * To Store User details for User Login Session purposes
     */
    HashMap<String, String> userDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEdittextUserId = (EditText) findViewById(R.id.edittext_login_userid);
        mEdittextPassword = (EditText) findViewById(R.id.edittext_login_password);

        Button mEmailSignInButton = (Button) findViewById(R.id.button_login_email_signin);
        if (mEmailSignInButton != null) {
            mEmailSignInButton.setOnClickListener(this);
        }

        Button mButtonGoToSignUp = (Button) findViewById(R.id.button_login_to_signup);
        if (mButtonGoToSignUp != null) {
            mButtonGoToSignUp.setOnClickListener(this);
        }

        mLoginFormView = findViewById(R.id.scrollview_login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param clickedView The view that was clicked.
     */
    @Override
    public void onClick(View clickedView) {

        switch (clickedView.getId()) {
            case R.id.button_login_email_signin:
                attemptLogin();
                break;
            case R.id.button_login_to_signup:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                finish();
                break;
        }

    } // end onClick()

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEdittextUserId.setError(null);
        mEdittextPassword.setError(null);

        // Store values at the time of the login attempt.
        String username = mEdittextUserId.getText().toString();
        String email    = UtilHelper.appendEmailSuffix(username);
        String password = mEdittextPassword.getText().toString();

        boolean invalidCredentialsFlag = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mEdittextUserId.setError(getString(R.string.error_field_required));
            focusView = mEdittextUserId;
            invalidCredentialsFlag = true;
        }
        if (!isEmailValid(email)) {
            mEdittextUserId.setError(getString(R.string.error_invalid_email));
            focusView = mEdittextUserId;
            invalidCredentialsFlag = true;
        }

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mEdittextPassword.setError(getString(R.string.error_password_too_short));
            focusView = mEdittextPassword;
            invalidCredentialsFlag = true;
        }

        if (invalidCredentialsFlag) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            userDetails = new HashMap<String, String>();
            userDetails.put(SessionManager.KEY_USERNAME, username);
            userDetails.put(SessionManager.KEY_EMAIL, email);
            userDetails.put(SessionManager.KEY_PASSWORD, UtilHelper.sha1Hash(password));
            mAuthTask = new UserLoginTask(userDetails); // login credentials are passed in here.
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Now uses email ID instead of full email string, so should check with internal DB or webservice to verify authenticity
        //return email.contains("@umbc.edu");
        return true;
    }

    public boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow for very easy animations.
        // If available, use these APIs to fade-in the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        URL url;
        String responseStr = "";
        String baseURL = getString(R.string.domain_url);
        String requestURL = baseURL+"signin.php";


        private UserLoginTask(HashMap<String, String> userDetails) {
            mEmail = userDetails.get(SessionManager.KEY_EMAIL);
            mPassword = userDetails.get(SessionManager.KEY_PASSWORD);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try{

                // Simulate network access.
                Thread.sleep(10);

                url = new URL(requestURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                StringBuilder str = new StringBuilder();

                str.append("email="+mEmail+"&").append("password="+mPassword);
                Log.d(TAG, "Login String: "+String.valueOf(str));

                String stringParams = str.toString();
                writer.write(stringParams);
                writer.flush();
                writer.close();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line = " | ";
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    line = br.readLine();
                    while (line != null) {
                        responseStr += line;
                        Log.d(TAG, "Response from Server:" + line);
                        line = br.readLine();
                    }
                    br.close();

                    return parseJsonResponse(responseStr);
                    //return Boolean.valueOf(parseJsonResponse(responseStr)); // Converts boolean to Boolean.
                }
                connection.disconnect();

            }catch (Exception e){
                e.printStackTrace();
            }

            return null; //this should only occur if there was an error in signing the user in!

        }

        private boolean parseJsonResponse(String responseStr) {
            try {
                JSONObject jsonObj = new JSONObject(responseStr);

                boolean result = jsonObj.getBoolean("result");

                if (result) { // This checks the result of the web-service!
                    Log.d(TAG, "Call to Web Service : Successful, User Exists!");

                    userDetails.put(SessionManager.KEY_USERID, jsonObj.getString("id"));
                    userDetails.put(SessionManager.KEY_FULLNAME, jsonObj.getString("fullname"));
                    userDetails.put(SessionManager.KEY_EMAIL, jsonObj.getString("email"));

                    SessionManager mSession = new SessionManager(getApplicationContext());
                    mSession.createNewSession(userDetails);

                    // Still pass this to Intent. Session info can be retrieved here as well as from SessionManager.
                    mNavIntent = new Intent(getBaseContext(), UsageStatusActivity.class);
                    mNavIntent.putExtra("EXTRA_SESSION_ID", jsonObj.getString("id"));
                    mNavIntent.putExtra("EXTRA_SESSION_NAME", jsonObj.getString("fullname"));
                    mNavIntent.putExtra("EXTRA_SESSION_EMAIL", jsonObj.getString("email"));
                    return true;
                }
                else {
                    Log.d(TAG, "Web service reported INVALID credentials");
                    return false;
                }

            } catch (final JSONException e) {
                //e.printStackTrace();
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            } // end catch

            return false; // this line should technically never execute, unless the web-service failed!
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                mNavIntent = new Intent(getApplicationContext(), UsageStatusActivity.class);
                startActivity(mNavIntent); // Navigate to
                finish(); // Terminate Activity
            } else {
                mEdittextPassword.setError(getString(R.string.error_incorrect_password));
                mEdittextPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }  // end class UserLoginTask

} // end class LoginActivity