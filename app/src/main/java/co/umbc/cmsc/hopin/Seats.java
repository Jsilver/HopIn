package co.umbc.cmsc.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by arjun on 4/13/17.
 */

public class Seats extends AppCompatActivity implements View.OnClickListener {

    Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats);


        confirm = (Button) findViewById(R.id.button2);
        confirm.setOnClickListener(this);

        Spinner dropdown = (Spinner) findViewById(R.id.spinner);
        String[] seats = new String[]{"","1", "2", "3","4","5","6"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, seats);
        dropdown.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {

    }
}
