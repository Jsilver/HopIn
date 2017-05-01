package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class UsageStatusActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "UsageStatusActivity: " ;
    Spinner spinnerUsageStatus;
    LinearLayout linearLayoutUsageStatus;

    public static final int STATUS_UNSELECTED = 0;
    public static final int STATUS_DRIVER = 1;
    public static final int STATUS_RIDER  = 2;

    private boolean mButtonConfCreated = false;

    SessionManager mSessionManager;
    SessionManager.UserDetails mUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_status);

        spinnerUsageStatus      = (Spinner) findViewById(R.id.spinner_usage_status_choose);
        linearLayoutUsageStatus = (LinearLayout) findViewById(R.id.activity_usage_status);

        spinnerUsageStatus.setOnItemSelectedListener(this);

        mSessionManager = new SessionManager(getApplicationContext());
    }

    /**
     *  this will fetch information about the currently logged in user and update the class field for inclusion in the data sent to webservice.
     */
    private String getLoggedInId() {
        mUserDetails = mSessionManager.getUserDetailsAsObject();

        if (mUserDetails != null) {
            return mUserDetails.getEmail();
            //debug
        }
        return "blank";
    }

    @Override
    public void onClick(View clickedView) {

        switch (clickedView.getId()) {

            case R.id.button_usage_status_confirm:  //selection confirmed.;

                String[] input = new String[4]; // container for values to be passed to webservice
                input[1] = this.getLoggedInId(); // set second param of input[] as userid

                int selectedValue = (int) spinnerUsageStatus.getSelectedItemId();
                spinnerUsageStatus.getSelectedItem();
                //Log.d("selected Item: ", String.valueOf(selectedValue));
                if (selectedValue == this.STATUS_RIDER) {
                    input[0] = "setrider";
                    invokeWebService(input);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else
                if (selectedValue == this.STATUS_DRIVER) {
                    input[0] = "setdriver";
                    Intent intent = new Intent(getApplicationContext(), Seats.class);
                    startActivity(intent);
                }
                break;

        } // end switch stmt

    } // end onClick()

    /**
     * <p> Callback method to be invoked when an item in this view has been selected.
     * This callback is invoked only when the newly selected position is different from the previously selected position or if there was no selected item.</p>
     * Implementers can call getItemAtPosition(position) if they need to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch ((int) parent.getItemIdAtPosition(position)) {
            case STATUS_UNSELECTED:
                //do nothing
                break;
            case STATUS_DRIVER:
                Toast.makeText(parent.getContext(), "You chose to be a : " + parent.getItemAtPosition(position).toString() + ", Pls confirm!", Toast.LENGTH_SHORT).show();
                addButtonOnSelection();
                break;
            case STATUS_RIDER:
                Toast.makeText(parent.getContext(), "You chose to be a : " + parent.getItemAtPosition(position).toString() + ", Pls confirm!", Toast.LENGTH_SHORT).show();
                addButtonOnSelection();
                break;
        } // end switch
    }

    /**
     * Callback method to be invoked when the selection disappears from this view.
     * The selection can disappear for instance when touch is activated or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Dynamically creates a confirmation button.
     */
    public void addButtonOnSelection() {

        if (!mButtonConfCreated) {
            ContextThemeWrapper btnContext = new ContextThemeWrapper(getBaseContext(), R.style.AppTheme_Button);
            Button buttonConfirmation = new Button(btnContext);

            buttonConfirmation.setId(R.id.button_usage_status_confirm);
            buttonConfirmation.setText("CONFIRM!");
            buttonConfirmation.setLayoutParams(new LinearLayout.LayoutParams(350, 90));
            buttonConfirmation.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.White));
            buttonConfirmation.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent2));
            buttonConfirmation.setGravity(Gravity.CENTER);
            //buttonConfirmation.setTextAppearance(getApplicationContext(), android.R.style.Widget_Material_Button_Colored);
            buttonConfirmation.setOnClickListener(this);

            linearLayoutUsageStatus.addView(buttonConfirmation);
            mButtonConfCreated = true;
        }

    }

    /**
     * This method will invoke the web service
     * @param input : a string array containing parameters for the webservice.  The first param should be the endpoint of the webservice, we want to call.
     */
    private void invokeWebService(String[] input) {

        //input[0] = String.valueOf();
        //input[1] = this.userEmailId;

        InvokeWebserviceTask myWebService = new InvokeWebserviceTask();
        myWebService.execute(input);

    }

    private class InvokeWebserviceTask extends AsyncTask<String, Integer, String> {

        String requestURL;

        /**
         * Override this method to perform a computation on a background thread. The specified parameters are the parameters passed to {@link #execute} by the caller of this task.
         * This method can call {@link #publishProgress} to publish updates on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... params) {
            switch (params[0]) {
                case "getdrivers":
                    requestURL = "http://10.200.54.39/hopinservice/api/v0/getdrivers.php";
                    Log.d(TAG, requestURL);
                    break;
                case "getriders":
                    requestURL = "http://10.200.54.39/hopinservice/api/v0/getdrivers.php";
                    Log.d(TAG, requestURL);
                    break;
            }
            return null;
        }

    } // end class

} // end Activity class