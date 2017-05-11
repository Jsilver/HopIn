package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
        seats = intent.getIntExtra("seats",0);
        ArrayList<Riders> riders = intent.getParcelableArrayListExtra("ridersList");

        List<String> ridersNames = new ArrayList<>();
        for(Riders rider : riders){
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
        int count = 0;
        switch (view.getId()){
            case R.id.btn_confirmriders:
                SparseBooleanArray checkedpositions =  lvriders.getCheckedItemPositions();
                if(checkedpositions!=null)
                {
                    for(int i=0;i<checkedpositions.size();i++) {
                        if(checkedpositions.get(i)) {
                            if (count < seats)
                                count++;
                            else {
                                count++;
                                //Toast.makeText(this, "Select " + seats + " riders only!", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                    if(count<seats || count > seats) {
                        Toast.makeText(this, "You can only select only " + seats + "riders!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    else if(count==seats) {
                        Toast.makeText(this, "Yaay! Riders Selected", Toast.LENGTH_LONG).show();
                        break;
                    }
                    else break;
                }
                else
                    Toast.makeText(this,"Select"+seats+"riders only!",Toast.LENGTH_LONG).show();

                break;
        }
    }
}