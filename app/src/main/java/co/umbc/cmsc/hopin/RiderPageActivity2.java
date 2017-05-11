package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RiderPageActivity2 extends AppCompatActivity implements View.OnClickListener {

    EditText driver2,phone2,vehicle2;
    Button locate;

    String driver,phone,vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_page2);

        Intent intent=getIntent();
        driver=intent.getStringExtra("drivername");
        //Toast.makeText(getApplicationContext(),driver, Toast.LENGTH_LONG).show();
        phone=intent.getStringExtra("phone");
        vehicle=intent.getStringExtra("vehicle");

        driver2=(EditText) findViewById(R.id.editText4);
        phone2=(EditText) findViewById(R.id.editText5);
        vehicle2=(EditText) findViewById(R.id.editText6);
        locate=(Button)findViewById(R.id.button3);

        vehicle2.setText(vehicle.toString());
        driver2.setText(driver.toString());
        phone2.setText(phone.toString());
        locate.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button3:
                Intent myintent=new Intent(this,RiderPageActivity3.class);
                myintent.putExtra("drivername",driver);
                startActivity(myintent);
        }

    }

}