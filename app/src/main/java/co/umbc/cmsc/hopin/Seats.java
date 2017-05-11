package co.umbc.cmsc.hopin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by arjun on 4/13/17.
 */

public class Seats extends AppCompatActivity implements View.OnClickListener {

    private static final String PREF_NAME = String.valueOf(R.string.auth_preference_file_key);
    Button buttonConfirm;
    SessionManager msessionmanager;
    private SharedPreferences sharedPreferences;
    private String email;
    private Spinner dropdown;
    //int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats);

        buttonConfirm = (Button) findViewById(R.id.button_seats_confirm);
        buttonConfirm.setOnClickListener(this);

        dropdown = (Spinner) findViewById(R.id.spinner_seats_dropdown);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        email = sharedPreferences.getString(SessionManager.KEY_EMAIL,"");
    }

    private void createSeatsPref(int value){
        sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("seats", value).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_seats_confirm:
                createSeatsPref(Integer.valueOf(String.valueOf(dropdown.getSelectedItem())));

                Intent intent = new Intent(this, MapsActivity.class);

                intent.putExtra("userEmail",email);

                startActivity(intent);
                break;
        }
    }
}
