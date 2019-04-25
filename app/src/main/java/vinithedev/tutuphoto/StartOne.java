package vinithedev.tutuphoto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartOne extends AppCompatActivity {

    StreetPoleTwo spt = new StreetPoleTwo();
    private Button buttonStartInput, buttonStartDirection;
    MyManager mm = new MyManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_one);
        setTitle(R.string.start_one);

        buttonStartDirection = findViewById(R.id.buttonStartDirection);
        buttonStartDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mm.setPoleOrDirection("Direction");
                openActivityStreetPoleTwo();
            }
        });

        buttonStartInput = findViewById(R.id.buttonStartInput);
        buttonStartInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mm.setPoleOrDirection("Pole");
                openActivityStreetPoleTwo();
            }
        });
        mm.hasPermissions();
        mm.checkDir();
        mm.fileExists();
        mm.readDocx();
        mm.appendImage();
    }

    public void openActivityStreetPoleTwo(){
        Intent intent = new Intent(this, StreetPoleTwo.class);
        startActivity(intent);
    }

}