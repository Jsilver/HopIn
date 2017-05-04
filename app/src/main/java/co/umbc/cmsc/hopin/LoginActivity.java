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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers login via UserId/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEdittextUserId;
    private EditText mEdittextPassword;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEdittextUserId = (EditText) findViewById(R.id.edittext_login_userid);
        mEdittextPassword = (EditText) findViewById(R.id.edittext_login_password);

        Button mEmailSignInButton = (Button) findViewById(R.id.button_login_email_signin);
        mEmailSignInButton.setOnClickListener(this);

        Button mButtonGoToSignUp = (Button) findViewById(R.id.button_login_to_signup);
        mButtonGoToSignUp.setOnClickListener(this);

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
        // TODO : append suffix @umbc.edu to email string obtained.
        String email = username;
        String password = mEdittextPassword.getText().toString();

        boolean invalidCredentialsFlag = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mEdittextPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEdittextPassword;
            invalidCredentialsFlag = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEdittextUserId.setError(getString(R.string.error_field_required));
            focusView = mEdittextUserId;
            invalidCredentialsFlag = true;
        } else if (!isEmailValid(email)) {
            mEdittextUserId.setError(getString(R.string.error_invalid_email));
            focusView = mEdittextUserId;
            invalidCredentialsFlag = true;
        }

        if (invalidCredentialsFlag) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, UtilHelper.sha1Hash(password));
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Now uses email ID instead of full email string, so should check with internal DB or webservice to verify authenticity
        //return email.contains("@umbc.edu");
        return true;
    }

    public boolean isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return password.length() > 8;
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

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return false;
            }

            /* One last check, query the shared pref file.  */
            SessionManager mSession = new SessionManager(getApplicationContext());
            return mSession.authenticate(mEmail, mPassword);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent authOkIntent = new Intent(getApplicationContext(), UsageStatusActivity.class);
                startActivity(authOkIntent); // Navigate to
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