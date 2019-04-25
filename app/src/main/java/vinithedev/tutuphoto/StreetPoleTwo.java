package vinithedev.tutuphoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreetPoleTwo extends AppCompatActivity {

    MyManager mm = new MyManager(this);

    Button buttonClean, buttonNext;
    EditText editTextId, editTextNumber, editTextEquipmentInstalation, editTextAntennaInstalation, editTextConnection, editTextObservation;
    ImageView imageViewId, imageViewNumber, imageViewNetwork,imageViewEquipmentInstalation, imageViewAntennaInstalation, imageViewConnection, imageViewObservation;
    Spinner spinner;
//    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    //Called when the activity is starting. This is where most initialization should go.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_pole_two);
        setTitle(R.string.street_pole_two);

        editTextId = (EditText) findViewById(R.id.editTextId);
        editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        spinner = (Spinner) findViewById(R.id.spinnerNetwork);
        editTextEquipmentInstalation = (EditText) findViewById(R.id.editTextEquipmentInstalation);
        editTextAntennaInstalation = (EditText) findViewById(R.id.editTextAntennaInstalation);
        editTextConnection = (EditText) findViewById(R.id.editTextConnection);
        editTextObservation = (EditText) findViewById(R.id.editTextObservation);

        imageViewId = (ImageView) findViewById(R.id.imageViewId);
        imageViewNumber = (ImageView) findViewById(R.id.imageViewNumber);
        imageViewNetwork = (ImageView) findViewById(R.id.imageViewNetwork);
        imageViewEquipmentInstalation = (ImageView) findViewById(R.id.imageViewEquipmentInstalation);
        imageViewAntennaInstalation = (ImageView) findViewById(R.id.imageViewAntennaInstalation);
        imageViewConnection = (ImageView) findViewById(R.id.imageViewConnection);
        imageViewObservation = (ImageView) findViewById(R.id.imageViewObservation);

        if(mm.getPoleOrDirection() == "Direction") {
//            editTextId.setVisibility(View.GONE);
//            editTextNumber.setVisibility(View.GONE);
//            spinner.setVisibility(View.GONE);
            editTextEquipmentInstalation.setVisibility(View.GONE);
            editTextAntennaInstalation.setVisibility(View.GONE);
            editTextConnection.setVisibility(View.GONE);
            editTextObservation.setVisibility(View.GONE);

//            imageViewId.setVisibility(View.GONE);
//            imageViewNumber.setVisibility(View.GONE);
//            imageViewNetwork.setVisibility(View.GONE);
            imageViewEquipmentInstalation.setVisibility(View.GONE);
            imageViewAntennaInstalation.setVisibility(View.GONE);
            imageViewConnection.setVisibility(View.GONE);
            imageViewObservation.setVisibility(View.GONE);
        }

        //Button NEXT(Start Camera)
        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mm.editTextId = String.valueOf(editTextId.getText());
                mm.editTextNumber = String.valueOf(editTextNumber.getText());
                mm.editTextEquipmentInstalation = String.valueOf(editTextEquipmentInstalation.getText());
                mm.editTextAntennaInstalation = String.valueOf(editTextAntennaInstalation.getText());
                mm.editTextConnection = String.valueOf(editTextConnection.getText());
                mm.editTextObservation = String.valueOf(editTextObservation.getText());

                mm.spinnerNetwork = spinner.getSelectedItem().toString();

                //Check docx last append
                mm.readDocx();

                //Open camera -> Take picture -> Save picture -> Create a copy of it -> Draw square and text on the first file -> Scan both files so ir shows on gallery
                dispatchPictureTakerAction();
            }

        });

        //Spinner's HINT
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

        if(mm.getPoleOrDirection() == "Direction") {
            adapter.add("Norte");
            adapter.add("Sul");
            adapter.add("Leste");
            adapter.add("Oeste");
            adapter.add("Ponto Cardeal"); //HINT
        }else {
            adapter.add("Primária");
            adapter.add("Secundária");
            adapter.add("Primária e Secundária");
            adapter.add("Rede"); //HINT
        }

        spinner.setAdapter(adapter);

        //Set the hint the default selection so it appears on launch
        spinner.setSelection(adapter.getCount());

        buttonClean = (Button) findViewById(R.id.buttonClean);

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

        mm.firstImgFinalName = mm.getFirstImageName();

        //Couldn't save image outside of DCIM
        mm.imageFile = new File(mm.tutuDCIMDir + mm.firstImgFinalName);

        //Initializes copy file
        mm.imageFileOriginal = new File(mm.tutuDCIMDir + mm.firstImageNameOriginal);

        //Return only the first image. The copy will be created later.
        return mm.imageFile;
    }

    //Is called after startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mm.longitude = location.getLongitude();
        mm.latitude = location.getLatitude();

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
        mm.appendImage();
    }








}