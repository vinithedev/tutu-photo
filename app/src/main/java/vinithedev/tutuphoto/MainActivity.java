package vinithedev.tutuphoto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    Button buttonClean, buttonNext;
    EditText editTextId, editTextNumber, editTextEquipmentInstalation, editTextAntennaInstalation, editTextConnection, editTextObservation;
    Context context = MainActivity.this;
    String pathToFile, fileName, dirString, dirStringOriginal;
    File image, imageOriginal, DCIMDir = null;
    File docPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

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

        //Fixes XWPFDocument's error
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

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

                Log.v("MyTAG", docPath.getAbsolutePath());
                    createDocx(docPath, "oi");

            }
        });
    }

    //Is called after startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE){

            //Copy the picture before drawing, so that we can have a backup
            try {
                copy(image, imageOriginal);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Scan image so that it shows on gallery
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image)));
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageOriginal)));

            //Write text on picture
            String filePath = image.getPath();
            Bitmap firstbm = BitmapFactory.decodeFile(filePath);

            Bitmap bmp = addTextToImage(firstbm, editTextId.getText().toString(), editTextNumber.getText().toString(), Color.BLACK, 255, false);
            File f = new File(DCIMDir.getPath() + File.separator + "/Tutu/" + "TUTU_" + fileName + ".jpg");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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

    public Bitmap addTextToImage(Bitmap source, String txtId, String txtNumber, int color, int alpha, boolean underline) {

        //Define dimensions
        int w = source.getWidth();
        int h = source.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, source.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(source, 0, 0, null);
        Paint paint = new Paint();

        //Define position
        int rectLeft = 1;
        int rectTop = h-(h/4);
        int rectRight = w/3;
        int rectBottom = h-1;

        Rect r = new Rect(rectLeft, rectTop, rectRight, rectBottom);

        //Draw white rect
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(r, paint);

        int xCenter = r.centerX();
        int yCenter = r.centerY();

        //Draw black edge
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(r, paint);

        //Text settings
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        //Opacity(0~255)
        paint.setAlpha(alpha);

        paint.setTextSize(w/24);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        paint.setTextAlign(Paint.Align.CENTER);

        //Id and Number drawings
        canvas.drawText(txtId, rectRight/2, yCenter-(h-yCenter)/3, paint);
        canvas.drawText(txtNumber, rectRight/2, yCenter, paint);

        return result;
    }

    private File createPhotoFile() {

        //File name format
        fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //Declares storage's directory
        DCIMDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        dirString = DCIMDir.getAbsolutePath() + "/Tutu/";

        //If folder and/or subfolder doesn't exists, create it
        File directory = new File(dirString);
        if (! directory.exists()){
            directory.mkdirs();
        }

        //Set final file directory and name, create it and return.
        dirString = DCIMDir.getAbsolutePath() + "/Tutu/" + "TUTU_" + fileName + ".jpg";

        //Copy file path, but with slightly different name
        dirStringOriginal = DCIMDir.getAbsolutePath() + "/Tutu/" + "TUTU_O_" + fileName + ".jpg";;

        image = new File(dirString);

        //Initializes copy file
        imageOriginal = new File(dirStringOriginal);

        //Return only the first image. The copy will be created later.
        return image;
    }

    //Copies a file
    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
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

    private void createDocx(File path, String message){
        try {
            XWPFDocument document = new XWPFDocument();

            FileOutputStream outputStream = new FileOutputStream(new File(path, "/poi.docx"));

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(message);

            document.write(outputStream);
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}