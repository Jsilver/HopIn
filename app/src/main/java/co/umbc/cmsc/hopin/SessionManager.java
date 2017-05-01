package co.umbc.cmsc.hopin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

/**
 * A simple Authentication Manager, stores and extracts user credentials for purposes of logging in.
 * Can store only one user's details at a time, if a new user logs in, the details of the last user will be over-written.
 *
 * Created by crypton on 4/20/17.
 */

public class SessionManager {

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;
    private Context mContext;

    private static final String PREF_NAME = String.valueOf(R.string.auth_preference_file_key);

    public static final String KEY_FULLNAME   = String.valueOf(R.string.auth_key_full_name);

    // Move these to a String resource file
    public static final String KEY_USERNAME   = "Username";
    public static final String KEY_EMAIL      = "Email";
    public static final String KEY_PASSWORD   = "Password";
    public static final String KEY_ISLOGGEDIN = "IsLoggedIn";
    // perhaps include a key to store last login time.

    public SessionManager(Context context) {
        this.mContext = context;
        mSharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mSharedPrefEditor = mSharedPref.edit();
    }

    private void createLoginSession(String username, String password) {
    }

    /**
     * Over-writes whatever user details happens to be stored in the AuthUserFile, if any.
     * @param user
     */
    public boolean signUp(HashMap<String, String> user) {
        /* Store user details in shared pref file. */
        mSharedPrefEditor.putString(KEY_FULLNAME, user.get(KEY_FULLNAME));
        mSharedPrefEditor.putString(KEY_USERNAME, user.get(KEY_USERNAME));
        mSharedPrefEditor.putString(KEY_EMAIL,    user.get(KEY_EMAIL));
        mSharedPrefEditor.putString(KEY_PASSWORD, user.get(KEY_PASSWORD));
        mSharedPrefEditor.putBoolean(KEY_ISLOGGEDIN, true);  //set the user as logged in

        return mSharedPrefEditor.commit();
    }

    public boolean authenticate(String username, String password) {
        HashMap<String, String> user = getUserDetails();

        //return username.equals(user.get(KEY_USERNAME)) && password.equals(user.get(KEY_PASSWORD));

        //if ( username == user.get(KEY_USERNAME) && password == user.get(KEY_PASSWORD) ) {
        if ( username.equals(user.get(KEY_USERNAME)) && password.equals(user.get(KEY_PASSWORD)) ) {
            mSharedPrefEditor.putBoolean(KEY_ISLOGGEDIN, true);  //set the user as logged in
            return mSharedPrefEditor.commit(); // true; if user is set as logged in, then indicate that authentication was successful
        }
        return false;
    }

    /**
     * Get Stored session data.
     * @return HashMap<String, String>
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        //Get User Info from pref file.
        user.put(KEY_FULLNAME, mSharedPref.getString(KEY_FULLNAME, null));
        user.put(KEY_USERNAME, mSharedPref.getString(KEY_USERNAME, null));
        user.put(KEY_EMAIL, mSharedPref.getString(KEY_EMAIL, null));
        user.put(KEY_PASSWORD, mSharedPref.getString(KEY_PASSWORD, null));

        return user;
    }

    /**
     * Quick check for loggedIn status.
     * @return boolean
     */
    public boolean isLoggedIn() {
        return mSharedPref.getBoolean(KEY_ISLOGGEDIN, false);  // get login status, default to -VE
    }

    /**
     * Include this in onCreate() or OnResume(), where you'd like to enforce authentication
     * This bad guy who will reroute the user to the login page, if the user is not loggedIn.
     */
    public void checkLoginStatus() {
        if (!this.isLoggedIn()) {
            //user is not logged in, reroute user to Login Activity
            Intent intent = new Intent(mContext, LoginActivity.class);

            // Closing all Activities
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // start Login Activity
            mContext.startActivity(intent);
        }
    } // authenticate()

    public void logOut() {
        mSharedPrefEditor.putBoolean(KEY_ISLOGGEDIN, false); // set the user as logged out.
        mSharedPrefEditor.commit();

        Intent logOutIntent = new Intent(mContext, LoginActivity.class);
        logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Closing all Activities
        logOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add new flag to start new Activity.
        mContext.startActivity(logOutIntent);
    }

    public void debugGetUserDetails() {
        Log.d("Debug: Fullname: ", mSharedPref.getString(KEY_FULLNAME, null));
        Log.d("Debug: Username: ", mSharedPref.getString(KEY_USERNAME, null));
        Log.d("Debug: Email:    ", mSharedPref.getString(KEY_EMAIL, null));
        Log.d("Debug: Password: ", mSharedPref.getString(KEY_PASSWORD, null));
        Log.d("Debug: LogInStatus: ", String.valueOf(mSharedPref.getBoolean(KEY_ISLOGGEDIN, false)));
    }

    public UserDetails getUserDetailsAsObject() {
        UserDetails userDetails = new UserDetails( mSharedPref.getString(KEY_FULLNAME, null) , mSharedPref.getString(KEY_USERNAME, null), mSharedPref.getString(KEY_EMAIL, null) );
        return userDetails;
    }

    class UserDetails {

        String username = "empty";
        String fullname = "empty";
        String email = "empty";

        public UserDetails(String fullname, String username, String email) {
            this.fullname = fullname;
            this.username = username;
            this.email = email;
        }

        public String getDisplayName() {
            return fullname;
        }

        public String getEmail() { return this.email; }

        public String getUsername() { return username; }

    } // end InnerClass

} // end class