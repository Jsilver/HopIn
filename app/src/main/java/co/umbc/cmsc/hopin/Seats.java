package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by arjun on 4/13/17.
 */

public class Seats extends AppCompatActivity implements View.OnClickListener {

    Button buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats);

        buttonConfirm = (Button) findViewById(R.id.button_seats_confirm);
        buttonConfirm.setOnClickListener(this);

        Spinner dropdown = (Spinner) findViewById(R.id.spinner_seats_dropdown);
        String[] seats = new String[]{"","1", "2", "3","4","5","6"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, seats);
        dropdown.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_seats_confirm:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("userEmail",new SessionManager(getApplicationContext()).getUserDetailsAsObject().getEmail());
                intent.putExtra("userFullName",new SessionManager(getApplicationContext()).getUserDetailsAsObject().getDisplayName());
                startActivity(intent);
                break;
        }
    }

}