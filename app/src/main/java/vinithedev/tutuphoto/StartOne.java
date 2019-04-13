package vinithedev.tutuphoto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartOne extends AppCompatActivity {

    StreetPoleTwo spt = new StreetPoleTwo();
    private Button buttonStartInput;
    MyManager mm = new MyManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_one);
        setTitle(R.string.start_one);

        buttonStartInput = findViewById(R.id.buttonStartInput);
        buttonStartInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityStreetPoleTwo();
            }
        });
        mm.hasPermissions();
        mm.checkDir();
        mm.fileExists();
    }

    public void openActivityStreetPoleTwo(){
        Intent intent = new Intent(this, StreetPoleTwo.class);
        startActivity(intent);
    }

}