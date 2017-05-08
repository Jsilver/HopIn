package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    double latitude, longitude;
    boolean bound = false, animate;
    List<Rider> ridersList;

    int success;
    String userEmail;

    private static final String TAG = "MapsActivity";
    private String baseURL;


    Timer timer;
    GetUpdatedFriendsTimer timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initialise();
    }

    private void initialise() {
        baseURL = getString(R.string.domain_url);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");

        if (timer != null) {
            timer.cancel();
        }

        //re-schedule timer here
        //otherwise, IllegalStateException of
        //"TimerTask is scheduled already"
        //will be thrown
        timer = new Timer();
        timerTask = new GetUpdatedFriendsTimer();
        timer.schedule(timerTask, 1000, 1000); // repeating every 5sec

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        bound = true;

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();


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

    @Override
    protected void onResume() {
        animate = false;
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (timer != null) {
            timer.cancel();
        }

        //re-schedule timer here
        //otherwise, IllegalStateException of
        //"TimerTask is scheduled already"
        //will be thrown
        timer = new Timer();
        timerTask = new GetUpdatedFriendsTimer();
        timer.schedule(timerTask, 1000, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonMap:
                Intent intent = new Intent(this, CurrentRiders.class);
                startActivity(intent);
        }
    }


    class GetUpdatedFriendsTimer extends TimerTask {

        @Override
        public void run() {
            if(latitude != 0 && longitude != 0) {
                getFriends();
            }
        }
    }

    public void getFriends(){
        String[] input = new String[3];

        input[0] = String.valueOf(userEmail);
        synchronized(this){
            //latitude=51.5033640;longitude=-0.1276250;
            input[1] = String.valueOf(latitude);
            input[2] = String.valueOf(longitude);
        }

        InvokeWebService webService = new InvokeWebService();
        webService.execute(input);
        Log.d(TAG,"sign up web service called");

    }

    private class InvokeWebService extends AsyncTask<String,Integer,List<Rider>> {

        @Override
        protected List<Rider> doInBackground(String... params) {
            URL url;
            String response = "";
            String requestURL = baseURL + "updaterider.php?";
            List<Rider> ridersList = new ArrayList<Rider>();
            try
            {
                url = new URL(requestURL);
                HttpURLConnection myconnection = (HttpURLConnection) url.openConnection();
                myconnection.setReadTimeout(15000);
                myconnection.setConnectTimeout(15000);
                myconnection.setRequestMethod("POST");
                myconnection.setDoInput(true);
                myconnection.setDoOutput(true);

                OutputStream os =  myconnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                StringBuilder str = new StringBuilder();

                str.append("useremail="+params[0]+"&").append("latitude="+params[1]+"&");
                str.append("longitude="+params[2]);

                String urstr = str.toString();

                writer.write(urstr);
                writer.flush();
                writer.close();

                int responseCode = myconnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            myconnection.getInputStream()));

                    line = br.readLine();
                    while(line != null)
                    {
                        response += line;
                        line = br.readLine();
                    }
                    br.close();

                    //reading the response
                    JSONObject json = new JSONObject(response);
                    String message = json.getString("message");
                    success = json.getInt("success");
                    JSONArray friends = json.getJSONArray("friends");

                    if(friends.length() > 0 ){

                        for(int i=0;i<friends.length();i++){

                            JSONObject friend = friends.getJSONObject(i);
                            Rider person = new Rider(friend.getString("name"),friend.getDouble("latitude"),friend.getDouble("longitude"));
                            ridersList.add(person);
                        }

                        //displayAllFriends(ridersList);
                    }

                    Toast.makeText(MapsActivity.this,message,Toast.LENGTH_LONG).show();


                }
                myconnection.disconnect();

            }catch(Exception e)
            {
                e.printStackTrace();
            }

            return ridersList;
        }

        @Override
        protected void onPostExecute(List<Rider> riders) {
            super.onPostExecute(riders);
            displayAllRiders(riders);
        }
    }

    public void displayAllRiders(List<Rider> ridersList) {
        if (bound == true) {
            mMap.clear();
            LatLng currentuser ;
            synchronized (this) {
                currentuser = new LatLng(latitude, longitude);
            }
            mMap.addMarker(new MarkerOptions().position(currentuser).title("I'm here"));

            if(!animate) {
                CameraPosition userposition = CameraPosition.builder().target(currentuser).zoom(14).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(userposition));

                animate = true;
            }

            for(Rider riders : ridersList) {

                LatLng userLocation = new LatLng(riders.getLatitude(), riders.getLongitude());

                mMap.addMarker(new MarkerOptions().position(userLocation).title(riders.name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            }
        }
    }

}  // end class