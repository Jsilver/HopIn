package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PhoneActivity extends AppCompatActivity implements View.OnClickListener{

    String username, email;
    Button submit;
    EditText number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        /*
         * The next 2 lines assume that a username and an email is passed when changing intents
         */
        username = getIntent().getStringExtra("Username");
        email = getIntent().getStringExtra("Email");
        submit = (Button)findViewById(R.id.button);
        submit.setOnClickListener(this);
        number = (EditText)findViewById(R.id.edittext_phone_number);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button:
                Intent AddressIntent = new Intent(this, AddressActivity.class);
                AddressIntent.putExtra("Phone", number.getText().toString());
                AddressIntent.putExtra("Username", username);
                AddressIntent.putExtra("Email", email);
                startActivity(AddressIntent);
                break;
        }
    }
}
