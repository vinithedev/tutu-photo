package vinithedev.tutuphoto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button buttonClean, buttonNext;
    EditText editTextId, editTextNumber, editTextEquipmentInstalation, editTextAntennaInstalation, editTextConnection, editTextObservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Spinner's HINT
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerNetwork);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Primary");
        adapter.add("Secondary");
        adapter.add("Primary and Secondary");
        adapter.add("Network"); //HINT

        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount()); //set the hint the default selection so it appears on launch.
//        spinner.setOnItemSelectedListener(this);

        buttonClean = (Button) findViewById(R.id.buttonClean);
        buttonNext = (Button) findViewById(R.id.buttonNext);

        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        editTextEquipmentInstalation = (EditText) findViewById(R.id.editTextEquipmentInstalation);
        editTextAntennaInstalation = (EditText) findViewById(R.id.editTextAntennaInstalation);
        editTextConnection = (EditText) findViewById(R.id.editTextConnection);
        editTextObservation = (EditText) findViewById(R.id.editTextObservation);

        buttonClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextId.setText("");
                editTextNumber.setText("");
                editTextEquipmentInstalation.setText("");
                editTextAntennaInstalation.setText("");
                editTextConnection.setText("");
                editTextObservation.setText("");
                spinner.setSelection(adapter.getCount());
            }
        });










    }//End of onCreate
}