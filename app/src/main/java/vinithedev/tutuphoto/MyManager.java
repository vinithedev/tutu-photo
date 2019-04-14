package vinithedev.tutuphoto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MyManager {

    int firstImgH = 304;
    int firstImgW = 210;
    int otherImgH = 236;
    int otherImgW = 312;

    int imgNum;

    float latitude, longitude;

    String id, number, network, equipmentInstalation, antennaInstalation, connection, observation, firstImgName;
    String firstImageName, firstImageNameOriginal, pathToFile;
    File imageFile, imageFileOriginal = null;

    File docDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    File DCIMDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    String tutuDocDir = docDir.getAbsolutePath() + "/Tutu/";
    String tutuDCIMDir = DCIMDir.getAbsolutePath() + "/Tutu/";
    String dateString = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    public String getFirstImageName() {
        dateString = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        firstImageName = "TUTU_" + dateString + ".jpg";
        firstImageNameOriginal = "TUTU_O_" + dateString + ".jpg";
        return firstImageName;
    }

    String[] FILENAMES = {
            "Site Survey.docx",
            "Concentradores.xlsx",
            "Repetidores.xlsx",
            "Tabelas Resumo.xlsx"
    };

    static final int REQUEST_PERMISSIONS = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    //Handle context
    Context context;
    public MyManager(Context context){
        this.context = context;
    }

    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    //If folder and/or subfolder doesn't exists, create it
    public void checkDir() {

        File docDirectory = new File(tutuDocDir);
        if (!docDirectory.exists()) {
            docDirectory.mkdirs();
        }

        File DCIMDirectory = new File(tutuDCIMDir);
        if (!DCIMDirectory.exists()) {
            DCIMDirectory.mkdirs();
        }

    }

    //Handle multiple permissions
    public void hasPermissions() {

        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, REQUEST_PERMISSIONS);
                }
            }
        }
    }

    //Create output files
    public void fileExists(){

        for (String filename : FILENAMES){
            File filePath = new File(tutuDocDir + filename);

            if(!filePath.exists()){

                try {

                    if(filename == FILENAMES[0]){

                        //Fixes Apache POI error
                        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
                        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
                        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

                        XWPFDocument document = new XWPFDocument();
                        XWPFParagraph paragraph = document.createParagraph();
                        XWPFRun run = paragraph.createRun();

                        paragraph = document.createParagraph();
                        paragraph.setAlignment(ParagraphAlignment.LEFT);
                        run = paragraph.createRun();
                        run.setBold(true);
                        run.setFontFamily("Trebuchet MS");
                        run.setFontSize(11);
                        run.setText("4. Site Survey");

                        FileOutputStream fileOut = new FileOutputStream(new File(tutuDocDir, FILENAMES[0]));
                        document.write(fileOut);
                        fileOut.close();

                    }
                    else if(filename == FILENAMES[1]){
                        Workbook workbook = new HSSFWorkbook();
                        workbook.createSheet("Concentradores");
                        FileOutputStream fileOut = new FileOutputStream(new File(tutuDocDir, FILENAMES[1]));
                        workbook.write(fileOut);
                        fileOut.close();
                    }
                    else if(filename == FILENAMES[2]){
                        Workbook workbook = new HSSFWorkbook();
                        workbook.createSheet("Repetidores");
                        FileOutputStream fileOut = new FileOutputStream(new File(tutuDocDir, FILENAMES[2]));
                        workbook.write(fileOut);
                        fileOut.close();
                    }
                    else if(filename == FILENAMES[3]){
                        Workbook workbook = new HSSFWorkbook();
                        workbook.createSheet("Concentrador");
                        workbook.createSheet("Repetidor");
                        FileOutputStream fileOut = new FileOutputStream(new File(tutuDocDir, FILENAMES[3]));
                        workbook.write(fileOut);
                        fileOut.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    //Append first image into the .docx file
    public void appendFirstImage(){

        //Fixes Apache POI error
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        try {

            XWPFDocument document = new XWPFDocument(new FileInputStream(tutuDocDir + FILENAMES[0]));
            List<XWPFParagraph>  paragraphs = document.getParagraphs();
            XWPFParagraph paragraph =  paragraphs.get(paragraphs.size() - 1);
            XWPFRun run = paragraph.createRun();

            XWPFParagraph paragraphImg = document.createParagraph();
            XWPFRun runImg = paragraphImg.createRun();

            FileInputStream image_fis = new FileInputStream(tutuDocDir + firstImgName);
            paragraphImg.setAlignment(ParagraphAlignment.CENTER);

            try {
                runImg.addPicture(image_fis, XWPFDocument.PICTURE_TYPE_JPEG, "", Units.toEMU(firstImgW), Units.toEMU(firstImgH));
            } catch (InvalidFormatException e) { e.printStackTrace(); }

            XWPFParagraph paragraphLast =  paragraphs.get(paragraphs.size() - 1);
            XWPFRun runLast = paragraphLast.createRun();
            runLast.addBreak();
            runLast.setItalic(true);
            runLast.setFontFamily("Trebuchet MS");
            runLast.setFontSize(11);
            runLast.setText("Figura " + imgNum + " - Poste do " + id + " " + number);

            FileOutputStream fos = new FileOutputStream(tutuDocDir + FILENAMES[0]);
            document.write(fos);

        } catch (Exception e) { e.printStackTrace(); }

    }

    //Read .docx file. Return last line's number.
    public void readDocx(){

        //Fixes Apache POI error
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        try {

            FileInputStream fis = new FileInputStream(tutuDocDir + FILENAMES[0]);
            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);

            String docString = extractor.getText();
            String[] lines = docString.split("\n");
            String lastLine = lines[lines.length - 1];

            if(lastLine.contains("4. Site Survey")){

            }

        } catch (Exception e) { e.printStackTrace(); }

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

    //Add rect and text to picture
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





}
