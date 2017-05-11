package co.umbc.cmsc.hopin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressActivity extends AppCompatActivity implements View.OnClickListener{

    String phone, username, email;
    EditText street, city, state, zip;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        phone = getIntent().getStringExtra("Phone");
        username = getIntent().getStringExtra("Username");
        email = getIntent().getStringExtra("Email");
        street = (EditText)findViewById(R.id.edittext_street);
        city = (EditText)findViewById(R.id.edittext_city);
        state = (EditText)findViewById(R.id.edittext_state);
        zip = (EditText)findViewById(R.id.edittext_zipcode);
        confirm = (Button)findViewById(R.id.button_address_confirm);
        confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_address_confirm:
                String[] input = new String[7];
                input[0] = username;
                input[1] = email;
                input[2] = phone;
                input[3] = street.getText().toString();
                input[4] = city.getText().toString();
                input[5] = state.getText().toString();
                input[6] = zip.getText().toString();
                AddressActivity.InvokeWebservice mywebservice = new AddressActivity.InvokeWebservice();
                mywebservice.execute(input);
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
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                /*
                 * Change the code below to start the next Intent
                 */
                //Intent AddressIntent = new Intent(AddressActivity.this, AddressActivity.class);
                //startActivity(AddressIntent);
            }
            else {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
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

            //String requestURL = "http://10.200.54.39/hopinservice/api/v0/phoneaddress.php";
            String baseURL = getString(R.string.domain_url);
            String requestURL = baseURL+"phoneaddress.php";

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
                str.append("phone="+params[2]+"&").append("street="+params[3]+"&");
                str.append("city="+params[4]+"&").append("state="+params[5]+"&");
                str.append("zip="+params[6]+"&");

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