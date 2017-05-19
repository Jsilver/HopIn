package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class RiderPageActivity3 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Timer timer;
    timeout timertask;
    String driver;
    Boolean isMapReady = false;

    class timeout extends TimerTask
    {
        @Override
        public void run() {
            String[] input = new String[2];

            Intent intent = getIntent();
            driver = intent.getStringExtra("drivername");
            input[0] = driver;
            InvokeWebservice mywebservice = new InvokeWebservice();
            mywebservice.execute(input);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_page3);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install it inside the SupportMapFragment. This method will only be triggered once the user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        isMapReady = true;
        mMap = googleMap;

        int i = 0;
        LatLng umbc = new LatLng(39.253831, -76.714469);

        // Add a marker at UMBC and move the camera.
        mMap.addMarker(new MarkerOptions().position(umbc).title("Your current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(umbc));

        Timer timer=new Timer();
        timer.schedule(new timeout(),UtilHelper.DELAY_FIRST_REFRESH,UtilHelper.REFRESH_RATE);

    }

    private class InvokeWebservice extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {

            URL url;
            String line;
            String response = "";

            //String requestURL = "http://10.200.61.136/hopinservice/api/v0/getdriverloc.php";
            String baseURL = getString(R.string.domain_url);
            String requestURL = baseURL+"getdriverloc.php";

            try {
                url = new URL(requestURL);
                HttpURLConnection myconnection = (HttpURLConnection) url.openConnection();
                myconnection.setReadTimeout(15000);
                myconnection.setConnectTimeout(15000);
                myconnection.setRequestMethod("POST");
                myconnection.setDoInput(true);
                myconnection.setDoOutput(true);

                OutputStream os = myconnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                StringBuilder str = new StringBuilder();

                str.append("ridername=" + params[0]);


                String urstr = str.toString();

                writer.write(urstr);
                writer.flush();
                writer.close();

                int responseCode = myconnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            myconnection.getInputStream()));

                    line = br.readLine();
                    while (line != null) {
                        response += line;
                        line = br.readLine();
                    }

                    br.close();
                    return response;

                }
                myconnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onPostExecute(String response) {

            if (isMapReady) {
                try {
                    mMap.clear();
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    JSONObject jObject = new JSONObject(response);
                    String latitude = jObject.getString("latitude");
                    String longitude = jObject.getString("longitude");
                    double latitude2, longitude2;
                    latitude2 = Double.parseDouble(latitude);
                    longitude2 = Double.parseDouble(longitude);
                    //String error=jObject.getString("username");
                    LatLng driverloc = new LatLng(latitude2, longitude2);

                    mMap.addMarker(new MarkerOptions().position(driverloc).title(driver));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(driverloc));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }


}