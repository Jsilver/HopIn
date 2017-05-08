package co.umbc.cmsc.hopin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by arjun on 4/12/17.
 */

public class CarDetails extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    EditText license;
    EditText make;
    EditText model;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardetails);

        license = (EditText) findViewById(R.id.editText);
        make = (EditText) findViewById(R.id.editText2);
        model = (EditText) findViewById(R.id.editText3);
        confirm = (Button) findViewById(R.id.phone_button);
        confirm.setOnClickListener(this);
        Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
        String[] color = new String[]{"","Red", "Black", "Green","Yellow"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, color);
        dropdown.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
            String license_ = license.getText().toString();
            if (TextUtils.isEmpty(license_)) {
                license.setError("License Field is required.");
                return;
                }


            String make_ = make.getText().toString();
            if (TextUtils.isEmpty(make_)) {
                make.setError("Make Field is required.");
                return;
            }

            String model_ = model.getText().toString();
            if (TextUtils.isEmpty(model_)) {
                model.setError("Model Field is required.");
                return;
            }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
