package co.umbc.cmsc.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    String user, u_email;
    EditText name, number, street, city, state, zip, model, specs, email, dri_ride, phone;
    Button back;
    private final static String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = new SessionManager(this).getUserDetailsAsObject().getUsername();
        u_email = new SessionManager(this).getUserDetailsAsObject().getEmail();
        Log.d("ProfileActivity", "email is: "+u_email);
        //user = getIntent().getStringExtra("Username");
        //u_email = getIntent().getStringExtra("Email");

        name = (EditText)findViewById(R.id.edit_name);
        number = (EditText)findViewById(R.id.edit_phone);
        street = (EditText)findViewById(R.id.edit_street);
        city = (EditText)findViewById(R.id.edit_city);
        state = (EditText)findViewById(R.id.edit_state);
        zip = (EditText)findViewById(R.id.edit_zip);
        phone = (EditText)findViewById(R.id.edit_phone);
        specs = (EditText)findViewById(R.id.edit_specs);
        model = (EditText)findViewById(R.id.edit_vehicle);
        email = (EditText)findViewById(R.id.edit_email);
        dri_ride = (EditText)findViewById(R.id.edit_driver_rider);

        back = (Button)findViewById(R.id.back_button);
        back.setOnClickListener(this);

        String[] input = new String[2];
        input[0] = user;
        input[1] = u_email;
        ProfileActivity.InvokeWebservice mywebservice = new ProfileActivity.InvokeWebservice();
        mywebservice.execute(input);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back_button:
                //Intent AddressIntent = new Intent(AddressActivity.this, AddressActivity.class);
                //startActivity(AddressIntent);
                break;
        }
    }

    private class InvokeWebservice extends AsyncTask<String,Integer,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                //Log.d(TAG, "S value: "+s);
                try {
                    JSONObject myjson = new JSONObject(s);
                    JSONObject myjsonUser = myjson.getJSONObject("users");
                    //JSONArray the_json_array = myjson.getJSONArray("users");
                    //Log.d(TAG, "json obj : "+myjsonUser);
                    email.setText(u_email);
                    name.setText(myjsonUser.getString("fullname"));
                    street.setText(myjsonUser.getString("street"));
                    city.setText(myjsonUser.getString("city"));
                    state.setText(myjsonUser.getString("state"));
                    zip.setText(myjsonUser.getString("zipcode"));
                    phone.setText(myjsonUser.getString("phone"));
                    model.setText(myjsonUser.getString("vehicle_name"));
                    specs.setText(myjsonUser.getString("vehicle_specs"));
                    dri_ride.setText(myjsonUser.getString("driver_status"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                /*
                 * Change the code below to start the next Intent
                 */
                //Intent AddressIntent = new Intent(AddressActivity.this, AddressActivity.class);
                //startActivity(AddressIntent);
            }
            else {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {

            URL url;
            String response = "";

            /*
             * This part needs changing. Change the URL to where the php file is located.
             */
            //String requestURL = "http://10.200.11.250/hopin/ProfileActivity.php";
            String baseURL = getString(R.string.domain_url);
            String requestURL = baseURL+"profileactivity.php";
            try
            {
                url = new URL(requestURL);
                HttpURLConnection myconnection =  (HttpURLConnection) url.openConnection();
                myconnection.setReadTimeout(15000);
                myconnection.setConnectTimeout(15000);
                myconnection.setRequestMethod("GET");
                myconnection.setDoInput(true);
                myconnection.setDoOutput(true);

                OutputStream os =  myconnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                StringBuilder str = new StringBuilder();

                str.append("username="+params[0]+"&").append("email="+params[1]+"&");
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
                }
                myconnection.disconnect();

            }catch(Exception e)
            {
                e.printStackTrace();
            }
            return response;
        }
    }
}
