package vinithedev.tutuphoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StreetPoleTwo extends AppCompatActivity {

    MyManager mm = new MyManager(this);

    Button buttonClean, buttonNext;
    EditText editTextId, editTextNumber, editTextEquipmentInstalation, editTextAntennaInstalation, editTextConnection, editTextObservation;

    //Called when the activity is starting. This is where most initialization should go.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_pole_two);
        setTitle(R.string.street_pole_two);

        //Button NEXT(Start Camera)
        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Open camera -> Take picture -> Save picture -> Create a copy of it -> Draw square and text on the first file -> Scan both files so ir shows on gallery
                dispatchPictureTakerAction();
            }

        });

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
                //You dont display last item. It is used as hint
                return super.getCount() - 1;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Primary");
        adapter.add("Secondary");
        adapter.add("Primary and Secondary");
        adapter.add("Network"); //HINT

        spinner.setAdapter(adapter);

        //Set the hint the default selection so it appears on launch
        spinner.setSelection(adapter.getCount());

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

    //Called when user requests to take picture
    private void dispatchPictureTakerAction() {

        //An intent is an abstract description of an operation to be performed.
        Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Creates the file
        if (takePic.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();

            if (photoFile != null) {
                mm.pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(mm.context, "vinithedev.tutuphoto", photoFile);
                takePic.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                //Launch an activity for which you would like a result when it finished.
                //When this activity exits, your onActivityResult() method will be called with the given requestCode.
                startActivityForResult(takePic, mm.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //Creates picture file
    private File createPhotoFile() {

        //Couldn't save image outside of DCIM
        mm.imageFile = new File(mm.tutuDCIMDir + mm.getFirstImageName());

        //Initializes copy file
        mm.imageFileOriginal = new File(mm.tutuDCIMDir + mm.firstImageNameOriginal);

        //Return only the first image. The copy will be created later.
        return mm.imageFile;
    }

    //Is called after startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == mm.REQUEST_IMAGE_CAPTURE){

            //Copy the picture before drawing, so that we can have a backup
            try {
                mm.copy(mm.imageFile, mm.imageFileOriginal);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Scan image so that it shows on gallery
            mm.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mm.imageFile)));
            mm.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mm.imageFileOriginal)));

            //Write text on picture
            String filePath = mm.imageFile.getPath();
            Bitmap firstbm = BitmapFactory.decodeFile(filePath);

            Bitmap bmp = mm.addTextToImage(firstbm, editTextId.getText().toString(), editTextNumber.getText().toString(), Color.BLACK, 255, false);
            File f = new File(mm.DCIMDir.getPath() + File.separator + "/Tutu/" + mm.firstImageName);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
    }






}