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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private String getLoggedInEmailId() {
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
                input[1] = this.getLoggedInEmailId(); // set second param of input[] as email address

                int selectedValue = (int) spinnerUsageStatus.getSelectedItemId();
                spinnerUsageStatus.getSelectedItem();
                //Log.d("selected Item: ", String.valueOf(selectedValue));
                if (selectedValue == this.STATUS_RIDER) {
                    input[0] = "setrider";
                    invokeWebService(input);
                    Intent intent = new Intent(getApplicationContext(), RiderPageActivity.class);
                    startActivity(intent);
                } else
                if (selectedValue == this.STATUS_DRIVER) {
                    input[0] = "setdriver";
                    invokeWebService(input);
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

        Log.d(TAG, "StatusSetterWebServiceCalled: ");
        StatusSetterWebserviceTask myWebService = new StatusSetterWebserviceTask();
        myWebService.execute(input);

    }

    private class StatusSetterWebserviceTask extends AsyncTask<String, Integer, String> {

        URL url;
        String responseStr = "";

        String baseURL = getString(R.string.domain_url);
        String requestURL = baseURL+"setusagestatus.php";

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

            String userStatus = params[0];      // usage status chosen
            String loggedInEmailId = params[1]; // The user's email address

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
                str.append("emailid="+loggedInEmailId+"&").append("status="+userStatus);

                Log.d(TAG, "Status String: "+String.valueOf(str));

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

                    return null;
                    //return Boolean.valueOf(parseJsonResponse(responseStr)); // Converts boolean to Boolean.
                }
                connection.disconnect();

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

    } // end class

} // end Activity class