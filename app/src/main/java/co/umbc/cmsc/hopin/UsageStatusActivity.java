package co.umbc.cmsc.hopin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class UsageStatusActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Spinner spinnerUsageStatus;
    //Button buttonUsageStatus;
    LinearLayout linearLayoutUsageStatus;

    public static final int STATUS_UNSELECTED = 0;
    public static final int STATUS_DRIVER = 1;
    public static final int STATUS_RIDER  = 2;

    private boolean mButtonConfCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_status);

        spinnerUsageStatus      = (Spinner) findViewById(R.id.spinner_usage_status_choose);
        //buttonUsageStatus       = (Button) findViewById(R.id.button_usage_status_choose);
        linearLayoutUsageStatus = (LinearLayout) findViewById(R.id.activity_usage_status);

        /*List<String> usageStatusList = new ArrayList<String>();
        ArrayAdapter<String> usageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, usageStatusList);
        usageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsageStatus.setAdapter(usageAdapter);*/

        spinnerUsageStatus.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View clickedView) {

        switch (clickedView.getId()) {

            case R.id.button_usage_status_confirm:
                //selection confirmed.;
                int selectedValue = (int) spinnerUsageStatus.getSelectedItemId();
                spinnerUsageStatus.getSelectedItem();
                //Log.d("selected Item: ", String.valueOf(selectedValue));
                if (selectedValue == this.STATUS_RIDER) {
                    Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
                    startActivity(intent);
                } else
                if (selectedValue == this.STATUS_DRIVER) {
                    Intent intent = new Intent(getApplicationContext(), Seats.class);
                    startActivity(intent);
                }
                break;

        } // end switch stmtq

    } // end onClick()

    /**
     * <p> Callback method to be invoked when an item in this view has been selected.
     * This callback is invoked only when the newly selected position is different from the previously selected position or if there was no selected item.</p>
     * Implementers can call getItemAtPosition(position) if they need to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch ((int) parent.getItemIdAtPosition(position)) {
            case STATUS_UNSELECTED:
                //do nothing
                break;
            case STATUS_DRIVER:
                Toast.makeText(parent.getContext(), "You chose to be a : " + parent.getItemAtPosition(position).toString() + ", Pls confirm!", Toast.LENGTH_SHORT).show();
                addButtonOnSelection();
                break;
            case STATUS_RIDER:
                Toast.makeText(parent.getContext(), "You chose to be a : " + parent.getItemAtPosition(position).toString() + ", Pls confirm!", Toast.LENGTH_SHORT).show();
                addButtonOnSelection();
                break;
        } // end switch
    }

    /**
     * Callback method to be invoked when the selection disappears from this view.
     * The selection can disappear for instance when touch is activated or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Dynamically creates a confirmation button.
     */
    public void addButtonOnSelection() {

        if (!mButtonConfCreated) {
            ContextThemeWrapper btnContext = new ContextThemeWrapper(getBaseContext(), R.style.AppTheme_Button);
            Button buttonConfirmation = new Button(btnContext);

            buttonConfirmation.setId(R.id.button_usage_status_confirm);
            buttonConfirmation.setText("CONFIRM!");
            buttonConfirmation.setLayoutParams(new LinearLayout.LayoutParams(350, 90));
            buttonConfirmation.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.White));
            buttonConfirmation.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent2));
            buttonConfirmation.setGravity(Gravity.CENTER);
            //buttonConfirmation.setTextAppearance(getApplicationContext(), android.R.style.Widget_Material_Button_Colored);
            buttonConfirmation.setOnClickListener(this);

            linearLayoutUsageStatus.addView(buttonConfirmation);
            mButtonConfCreated = true;
        }

    }

} // end Activity class