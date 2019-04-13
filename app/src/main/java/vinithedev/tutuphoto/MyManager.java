package vinithedev.tutuphoto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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

    int firstImgNum;

    float latitude, longitude;

    String id, number, network, equipmentInstalation, antennaInstalation, connection, observation, firstImgName;

    public boolean wordEmpty;
    Toast toast;
    File docDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    String tutuDocDir = docDir.getAbsolutePath() + "/Tutu/";
    String dateString = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String firstImageName = "TUTU_" + dateString + ".jpg";

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

        File directory = new File(tutuDocDir);
        if (!directory.exists()) {
            directory.mkdirs();

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
//                        run.setText("The List:");

//                        String cTAbstractNumBulletXML =
//                                "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"0\">"
//                              + "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
//                              + "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"bullet\"/><w:lvlText w:val=\"\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Symbol\" w:hAnsi=\"Symbol\" w:hint=\"default\"/></w:rPr></w:lvl>"
//                              + "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"bullet\"/><w:lvlText w:val=\"o\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Courier New\" w:hAnsi=\"Courier New\" w:cs=\"Courier New\" w:hint=\"default\"/></w:rPr></w:lvl>"
//                              + "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"bullet\"/><w:lvlText w:val=\"\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr><w:rPr><w:rFonts w:ascii=\"Wingdings\" w:hAnsi=\"Wingdings\" w:hint=\"default\"/></w:rPr></w:lvl>"
//                              + "</w:abstractNum>";

                        //                            CTNumbering cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML);
//                            CTAbstractNum cTAbstractNum = cTNumbering.getAbstractNumArray(0);
//                            XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
//                            XWPFNumbering numbering = document.createNumbering();
//                            BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
//                            BigInteger numID = numbering.addNum(abstractNumID);

                        paragraph = document.createParagraph();
                        paragraph.setAlignment(ParagraphAlignment.LEFT);
//                            paragraph.setNumID(numID);
                        run = paragraph.createRun();
                        run.setBold(true);
                        run.setFontFamily("Trebuchet MS");
                        run.setFontSize(11);
                        run.setText("4. Site Survey");
                        run.addBreak();
                        run.addBreak();
                        run.setText("   4.1. Concentradores");
                        run.addBreak();
                        run.addBreak();
                        run.setText("      4.1.1. Concentrador 1");
                        run.addBreak();
                        run.addBreak();
                        run.addBreak();
                        //First Image

//                        FileInputStream image_fis = new FileInputStream(tutuDocDir + "tututest.jpg");
//
//                        paragraphImg.setAlignment(ParagraphAlignment.CENTER);
//                        try {
//                            runImg.addPicture(image_fis, XWPFDocument.PICTURE_TYPE_JPEG, "", Units.toEMU(210), Units.toEMU(304)); // 200x200 pixels
//                        } catch (InvalidFormatException e) {
//                            e.printStackTrace();
//                        }
//                        image_fis.close();


//                        paragraph = document.createParagraph();
//                            paragraph.setNumID(numID);
//                            paragraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(1));
//                            run.setText("Sub list item " + " a");
//                        paragraph.setSpacingAfter(0);

//                        paragraph = document.createParagraph();
//                        run = paragraph.createRun();
//                        run.setText("Paragraph after the list.");

                        FileOutputStream fileOut = new FileOutputStream(new File(tutuDocDir, FILENAMES[0]));
                        document.write(fileOut);
                        fileOut.close();

                        wordEmpty = true;

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
            runLast.setText("Figura " + firstImgNum + " - Poste do " + id + " " + number);

            FileOutputStream fos = new FileOutputStream(tutuDocDir + FILENAMES[0]);
            document.write(fos);

        } catch (Exception e) { e.printStackTrace(); }

    }

    //Read .docx file. Return last line's number.
    public void readDocx(){






    }


















}
