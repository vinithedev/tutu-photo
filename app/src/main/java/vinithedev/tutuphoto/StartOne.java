package vinithedev.tutuphoto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartOne extends AppCompatActivity {

    private Button buttonStartInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_one);

        buttonStartInput = findViewById(R.id.buttonStartInput);
        buttonStartInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityStreetPoleTwo();
            }
        });

    }

    public void openActivityStreetPoleTwo(){
        Intent intent = new Intent(this, StreetPoleTwo.class);
        startActivity(intent);
    }

}
