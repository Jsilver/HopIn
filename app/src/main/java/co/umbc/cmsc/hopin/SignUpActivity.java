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
import android.widget.Toast;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdittextFullname;
    private EditText mEdittextUsername;
    private EditText mEdittextPassword;
    private Button mButtonSignup;
    private View mScrollviewSignup;
    private View mProgressBarSignup;

    /**
     * To Store User details for registration purposes
     */
    HashMap<String, String> userDetails = null;

    /**
     * Keep track of the Signup task to ensure we can cancel it if requested.
    */
    private UserSignUpTask mSignupTask = null;
    private boolean invalidInputsFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEdittextFullname = (EditText) findViewById(R.id.edittext_signup_fullname);
        mEdittextUsername = (EditText) findViewById(R.id.edittext_signup_email);
        mEdittextPassword = (EditText) findViewById(R.id.edittext_signup_password);

        mButtonSignup     = (Button) findViewById(R.id.button_signup_signup);
        mButtonSignup.setOnClickListener(this);

        mScrollviewSignup  = findViewById(R.id.scrollview_signup_form);
        mProgressBarSignup = findViewById(R.id.progressbar_signup);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param clickedView The view that was clicked.
     */
    @Override
    public void onClick(View clickedView) {
        switch (clickedView.getId()) {
            case R.id.button_signup_signup:
                attemptSignUp();
                break;
        }
    }

    private void attemptSignUp() {

        if (mSignupTask != null) {
            return;
        }

        // Reset errors.
        mEdittextFullname.setError(null);
        mEdittextUsername.setError(null);
        mEdittextPassword.setError(null);

        String fullname = mEdittextFullname.getText().toString();
        String username = mEdittextUsername.getText().toString();
        String email    = UtilHelper.appendEmailSuffix(username);
        String password = mEdittextPassword.getText().toString();

        // Initialize flag for indicating errors.
        boolean invalidInputsFlag = false;
        View focusView = null;

        if (TextUtils.isEmpty(fullname)) {
            mEdittextFullname.setError("Your name is required");
            focusView = mEdittextFullname;
            invalidInputsFlag = true;
        }

        if (TextUtils.isEmpty(username)) {
            mEdittextUsername.setError("A username is required");
            focusView = mEdittextUsername;
            invalidInputsFlag = true;
        }

        if (!isPasswordValid(password)) {
            mEdittextPassword.setError(getString(R.string.error_field_required));
            focusView = mEdittextPassword;
            invalidInputsFlag = true;
        }

        if (invalidInputsFlag) {
            // There was an error; don't attempt sign up and focus on the specified form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner; and kick off a background task to perform the user signup attempt.
            showProgress(true);

            userDetails = new HashMap<String, String>();
            //userDetails.put(SessionManager.KEY_FULLNAME, String.valueOf(mEdittextFullname.getText().toString()) );
            userDetails.put(SessionManager.KEY_FULLNAME, fullname);
            userDetails.put(SessionManager.KEY_USERNAME, username);
            userDetails.put(SessionManager.KEY_EMAIL, email);
            userDetails.put(SessionManager.KEY_PASSWORD, UtilHelper.sha1Hash(password));

            mSignupTask = new UserSignUpTask(userDetails);
            mSignupTask.execute((Void) null);
        }

    }

    public boolean isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return password.length() > 8;
    }

    /**
     * Shows the progress UI and hides the sign up form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow for very easy animations.
        // If available, use these APIs to fade-in the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mScrollviewSignup.setVisibility(show ? View.GONE : View.VISIBLE);
            mScrollviewSignup.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mScrollviewSignup.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBarSignup.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBarSignup.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBarSignup.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show and hide the relevant UI components.
            mProgressBarSignup.setVisibility(show ? View.VISIBLE : View.GONE);
            mScrollviewSignup.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {

        private final String mFullname;
        private final String mUsername;
        private final String mEmail;
        private final String mPassword;

        public UserSignUpTask(HashMap<String, String> userDetails) {
            mFullname = userDetails.get(SessionManager.KEY_FULLNAME);
            mUsername = userDetails.get(SessionManager.KEY_USERNAME);
            mEmail = userDetails.get(SessionManager.KEY_EMAIL);
            mPassword = userDetails.get(SessionManager.KEY_FULLNAME);
        }

        /**
         * Override this method to perform a computation on a background thread. The specified parameters are the parameters passed to {@link #execute} by the caller of this task.
         * This method can call {@link #publishProgress} to publish updates on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute(), @see #onPostExecute, @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            //TODO: attempt to send to webservice.

            try {
                // Simulate network access.
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return false;
            }

            SessionManager mSession = new SessionManager(getApplicationContext());
            return mSession.signUp(userDetails);
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The specified result is the value returned by {@link #doInBackground}.</p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param result The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute, @see #doInBackground, @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(final Boolean result) {
            //super.onPostExecute(result);
            mSignupTask = null;
            showProgress(false);

            if (result) {
                Intent registerOkIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(registerOkIntent);  // Navigate to authenticated Activity.
                finish(); // Terminate Activity
            }
            else {
                //TODO Display a better error message in terms of UI/UX
                Toast.makeText(getApplicationContext(), "Couldn't register your details, sorry!", Toast.LENGTH_LONG).show();
                mEdittextUsername.setError(getString(R.string.error_signup_fail));
                mEdittextUsername.requestFocus();
            }
        }

        /**
         * <p>Applications should preferably override {@link #onCancelled(Object)}. This method is invoked by the default implementation of {@link #onCancelled(Object)}.</p>
         * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and {@link #doInBackground(Object[])} has finished.</p>
         *
         * @see #onCancelled(Object), @see #cancel(boolean), @see #isCancelled()
         */
        @Override
        protected void onCancelled() {
            //super.onCancelled();
            mSignupTask = null;
            showProgress(false);
        }
    } // end Inner class

} // end class