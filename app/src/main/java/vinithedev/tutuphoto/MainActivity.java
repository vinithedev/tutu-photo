package vinithedev.tutuphoto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    Button buttonClean, buttonNext;
    EditText editTextId, editTextNumber, editTextEquipmentInstalation, editTextAntennaInstalation, editTextConnection, editTextObservation;
    ImageView imageView;
    Context context = MainActivity.this;
    String pathToFile, fileName, dirString;
    Intent mediaScanIntent;
    Uri contentUri;
    File image = null;

    static final int REQUEST_PERMISSIONS = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    //Called when the activity is starting. This is where most initialization should go.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Handle permissions
        //
        //Beginning with Android 6.0 (API level 23), users can revoke permissions from any app at any time,
        //even if the app targets a lower API level. So even if the app used the camera yesterday,
        //it can't assume it still has that permission today.

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS);
        }

        //Button NEXT(Start Camera)
        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Open camera -> Save picture in determined location, with determined file name -> Scan file, so gallery will show it
                dispatchPictureTakerAction();
            }

        });
        imageView = findViewById(R.id.imageView);

        //Spinner's HINT
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerNetwork);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; //You dont display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Primary");
        adapter.add("Secondary");
        adapter.add("Primary and Secondary");
        adapter.add("Network"); //HINT

        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount()); //Set the hint the default selection so it appears on launch.

        buttonClean = (Button) findViewById(R.id.buttonClean);

        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        editTextEquipmentInstalation = (EditText) findViewById(R.id.editTextEquipmentInstalation);
        editTextAntennaInstalation = (EditText) findViewById(R.id.editTextAntennaInstalation);
        editTextConnection = (EditText) findViewById(R.id.editTextConnection);
        editTextObservation = (EditText) findViewById(R.id.editTextObservation);

        //Button CLEAN clicked
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
    }

    //Runs after some event is completed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image)));
        }
    }

    private void dispatchPictureTakerAction() {

        //An intent is an abstract description of an operation to be performed.
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Creates the file
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(context, "vinithedev.tutuphoto", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                //Launch an activity for which you would like a result when it finished.
                //When this activity exits, your onActivityResult() method will be called with the given requestCode.
                startActivityForResult(takePic, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createPhotoFile() {

        //File name format
        fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //Declares storage's directory
        File DCIMDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        dirString = DCIMDir.getAbsolutePath() + "/Tutu/";

        //If folder doesn't exists, create it
        File directory = new File(dirString);
        if (! directory.exists()){
            directory.mkdirs();
        }

        //Set final file directory and name, create it and return.
        dirString = DCIMDir.getAbsolutePath() + "/Tutu/" + "TUTU_" + fileName + ".jpg";
        image = new File(dirString);
        return image;
    }

    //Checks multiple permissions
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}