package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CurrentRiderActivity extends AppCompatActivity implements View.OnClickListener {

    ListView lvriders;
    Button btn_confirmriders;
    int seats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_rider);
        initialise();
    }

    private void initialise() {
        lvriders = (ListView) findViewById(R.id.lvriders);
        btn_confirmriders = (Button) findViewById(R.id.btn_confirmriders);
        btn_confirmriders.setOnClickListener(this);
        Intent intent = getIntent();
        seats = intent.getIntExtra("seats", 0);
        ArrayList<Riders> riders = intent.getParcelableArrayListExtra("ridersList");
        List<String> ridersNames = new ArrayList<>();
        for (Riders rider : riders) {
            ridersNames.add(rider.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                ridersNames
        );
        lvriders.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int count = 0;  // Initialize counter check for number of riders selected.
        switch (view.getId()) {
            case R.id.btn_confirmriders:
                SparseBooleanArray checkedpositions = lvriders.getCheckedItemPositions();
                if (checkedpositions != null) {
                    ArrayList names = new ArrayList();
                    StringBuilder str = new StringBuilder();  // Used to build up list of rider names to be sent to server.
                    for (int i = 0; i < checkedpositions.size(); i++) {
                        if ( checkedpositions.get( checkedpositions.keyAt(i) ) ) {
                            String tag = String.valueOf(lvriders.getItemAtPosition(checkedpositions.keyAt(i)));
                            names.add(tag);
                            count++;
                        }
                    }
                    //if (count < seats || count > seats) {
                    if (count > seats) {
                        Toast.makeText(this, "You selected " + count + " riders! You only have "+seats+" seats available ", Toast.LENGTH_LONG).show();
                        break;
                    } else if (count <= seats) {
                        for (int i = 0; i < names.size(); i++) { // build up list of names (comma separated list) to send to server
                            String name = names.get(i).toString();
                            int x = names.size();
                            if (i == (x - 1)) {  // last value in the list should not have a comma
                                str.append("'" + name + "'");
                            } else {
                                str.append("'"+name + "',");
                            }
                        }
                        String names2 = str.toString();
                        //Toast.makeText(this, names2 + " Selected", Toast.LENGTH_LONG).show();  // Display list of selected riders!
                        Toast.makeText(this, count + " riders selected", Toast.LENGTH_LONG).show();  // Display number of selected riders!
                        String[] input = new String[2];
                        String loggedInUsername = new SessionManager(getBaseContext()).getUserDetailsAsObject().getUsername();
                        input[0] = loggedInUsername;
                        input[1] = names2;
                        InvokeWebservice mywebservice = new InvokeWebservice();
                        mywebservice.execute(input);
                        //Toast.makeText(this, "Yaay! Riders Selected", Toast.LENGTH_LONG).show();
                        break;
                    } else break;
                } else
                    Toast.makeText(this, "Select" + seats + "riders only!", Toast.LENGTH_LONG).show();
                break;
        }
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
            String baseURL = getString(R.string.domain_url);
            String requestURL = baseURL + "updateridersdata.php";
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
                str.append("drivername=" + params[0] + "&");
                str.append("riders=" + params[1]);
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
            //Toast.makeText(getApplicationContext(), "response:"+response, Toast.LENGTH_LONG).show();
            Log.d("response: ", response);
        }
    }

}