package co.umbc.cmsc.hopin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private static final String TAG = "MainActivity";
    SessionManager mSessionManager;
    SessionManager.UserDetails mUserDetails;

    public String latitude = "49.5";
    public String longitude = "-77.5";
    public String id = "00";
    public String userEmailId = "dummy@example.com";
    public String userFullName = "LoggedInUserPlaceholder";

    public static final int MIN_TIME = 10000;  // measured in milliseconds, should be set to 60 seconds
    public static final int MIN_DISTANCE = 1;  // measured in meters

    private LocationManager mLocationManager;
    private Location         mCurrentLocation;

    public String[] input = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSessionManager = new SessionManager(getApplicationContext());
        mSessionManager.checkLoginStatus();

        mSessionManager.getUserDetailsAsObject().getEmail();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        updateLoggedInId();

        input[0] = this.latitude;
        input[1] = this.longitude;
        input[3] = this.userEmailId;

        /*TextView textViewFullName = (TextView) findViewById(R.id.textview_main_user);
        textViewFullName.setText(this.userFullName.toString());*/

        /* Set the view for navigation and toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUser = (TextView) headerView.findViewById(R.id.textview_main_user);
        navUser.setText(this.userFullName);
        TextView navEmail = (TextView) headerView.findViewById(R.id.textview_main_email);
        navEmail.setText(this.userEmailId);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     *  This will set the email id of the Authentication session to the email id of the currently logged in user ONLY IF it is null.
     *  The action performed here the 'emailid' class field for this Activity hence making it easy to obtain info about the user for inclusion in the data sent to the webservice.
     *
     */
    private void updateLoggedInId() {
        mUserDetails = mSessionManager.getUserDetailsAsObject();

        if (mUserDetails != null) {
            this.userEmailId = mUserDetails.getEmail();
            this.userFullName = mUserDetails.getDisplayName();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //mSessionManager.debugGetUserDetails();
        mSessionManager.checkLoginStatus();
        updateLoggedInId();  // will set the email id of the session to the email id of the currently logged in user ONLY IF it is null.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.removeUpdates(this);
    }

    private void invokeWebService() {

        input[0] = String.valueOf(mCurrentLocation.getLatitude());
        input[1] = String.valueOf(mCurrentLocation.getLongitude());
        input[3] = this.userEmailId;

        InvokeWebserviceTask myWebService = new InvokeWebserviceTask();
        myWebService.execute(input);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_address) {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_phone) {
            Intent intent = new Intent(this, PhoneActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_usage_status) {
            Intent intent = new Intent(this, UsageStatusActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_cardetails) {
            Intent intent = new Intent(this, CarDetails.class);
            startActivity(intent);
        } else if (id == R.id.nav_seats) {
            Intent intent = new Intent(this, Seats.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            mSessionManager.logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        //Toast.makeText(this, "Updating Location with: "+mCurrentLocation.getProvider(), Toast.LENGTH_LONG).show();

        //execute web service after updating inputs with current lat & lon
        invokeWebService();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    protected class InvokeWebserviceTask extends AsyncTask<String, Integer, String> {
        /**
         * Override this method to perform a computation on a background thread. The specified parameters are the parameters passed to {@link #execute} by the caller of this task.
         *
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

            URL url;
            String response = "";
            String baseURL = getString(R.string.domain_url);
            String requestURL = baseURL+"postlocation.php";
            //String requestURL = "http://10.200.54.39/hopinservice/api/v0/postlocation.php";

            try{
                url = new URL(requestURL);
                HttpURLConnection myconnection = (HttpURLConnection) url.openConnection();
                myconnection.setReadTimeout(15000);
                myconnection.setConnectTimeout(15000);
                myconnection.setRequestMethod("POST");
                myconnection.setDoInput(true);
                myconnection.setDoOutput(true);

                OutputStream outputStream = myconnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                StringBuilder str = new StringBuilder();

                str.append("latitude="+params[0]+"&").append("longitude="+params[1]+"&").append("emailid="+params[3]);

                String stringParams = str.toString();
                writer.write(stringParams);
                writer.flush();
                writer.close();

                int responseCode = myconnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line = " | ";
                    BufferedReader br = new BufferedReader(new InputStreamReader(myconnection.getInputStream()));

                    line = br.readLine();
                    while (line != null) {
                        response += line;
                        Log.d(TAG, "Response from Server: "+line);
                        line = br.readLine();
                    }
                    br.close();
                }
                myconnection.disconnect();

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

    } // end Inner class

} // end class