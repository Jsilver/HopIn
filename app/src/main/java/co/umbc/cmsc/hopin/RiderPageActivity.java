package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

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

public class RiderPageActivity extends AppCompatActivity {

    private ProgressBar progress;

    int status=0;

    Timer timer;
    timeout timertask;

    String loggedInUsername = "NoOne";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_page);

        String[] input=new String[2];
        Intent intent = getIntent();
        loggedInUsername= new SessionManager(getBaseContext()).getUserDetailsAsObject().getUsername();
        input[0]=loggedInUsername;
       // Toast.makeText(getApplicationContext(),input[0], Toast.LENGTH_LONG).show();

        InvokeWebservice mywebservice = new InvokeWebservice();
        mywebservice.execute(input);
    }



    private class InvokeWebservice extends AsyncTask<String,Integer,String> {
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

            //String requestURL = "http://10.200.61.136/hopinservice/api/v0/riderinfo.php";
            String baseURL = getString(R.string.domain_url);
            String requestURL = baseURL+"riderinfo.php";

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
            try {

                JSONObject jObject=new JSONObject(response);
                //String error=jObject.getString("username");

                String code2=jObject.getString("selected");
               // Toast.makeText(getApplicationContext(),code2, Toast.LENGTH_LONG).show();

                // if selected, then grab driver details...
                if(code2.equals("1"))
                {
                    String drivername=jObject.getString("driverfullname");
                    String phone=jObject.getString("phone");
                    String vehicle=jObject.getString("vehicle_name");
                    Intent intent2=new Intent(RiderPageActivity.this,RiderPageActivity2.class);
                    intent2.putExtra("drivername",drivername);
                    intent2.putExtra("phone",phone);
                    intent2.putExtra("vehicle",vehicle);
                    status=1;
                    startActivity(intent2);
                }
                else
                {
                    if(timer!=null)
                    {
                        timer.cancel();
                    }
                    timer=new Timer();
                    timertask=new timeout();
                    timer.schedule(timertask,1000,9000);

                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    class timeout extends TimerTask
    {
        @Override
        public void run() {
            String[] input=new String[2];

            //loggedInUsername= new SessionManager(getBaseContext()).getUserDetailsAsObject().getUsername();
            input[0] = RiderPageActivity.this.loggedInUsername;

            InvokeWebservice mywebservice = new InvokeWebservice();
            mywebservice.execute(input);
        }
    }  // end inner class

}