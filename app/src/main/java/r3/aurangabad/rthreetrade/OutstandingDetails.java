package r3.aurangabad.rthreetrade;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

public class OutstandingDetails extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE = 1;
    Model model;
    RecyclerView recyclerViewOutstanding;
    TextView txttotal;
    Database mydb;
    Vector partyouts;
    String Cutomername = "",closing="";
    Vector company;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstanding_details);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        try {
            Cutomername = b.getString("Customer");
            closing = b.getString("Closing");
        } catch (Exception ex) {
            Log.e("Error Get values", "" + ex.getLocalizedMessage());
        }
        company = new Vector();

        model = Model.getInstance();
        mydb = new Database(getApplicationContext());
        recyclerViewOutstanding = (RecyclerView) findViewById(R.id.recyler_customeroutstanding);
        txttotal = (TextView) findViewById(R.id.txt_totaloutstanding);
        barChart = (BarChart) findViewById(R.id.barchart_detailoutstanding);
        try {company = mydb.getCompanyDetails(); }catch (Exception ex){}
        loadOutstanding();
        getBarEntries();
    }

    void loadOutstanding() {
        try {
            int tot=0,paidamount=0;
            double bal=0,onacc=0;
            String overdue="";
            double totalamount = 0;
            String total = "";
             mydb = new Database(getApplicationContext());
            model = Model.getInstance();
            partyouts = mydb.getPartyOutstanding(Cutomername);
            String name[] = new String[partyouts.size()];
            String details[] = new String[partyouts.size()];
            String amount[] = new String[partyouts.size()];
            for (int i = 0; i < partyouts.size(); i++) {
                Vector data = (Vector) partyouts.elementAt(i);
                try {
                    paidamount = Integer.parseInt(data.elementAt(5)
                            .toString().trim())
                            - Integer.parseInt(data.elementAt(3).toString()
                            .trim());
                }catch (Exception ex){paidamount=0;}
                overdue=new Database(getApplicationContext()).getDays(""+data.elementAt(1))+" Days";
                try {
                    bal = (Double.parseDouble("" + data.elementAt(3)));
                }catch (Exception ex){bal=0;Log.e("Bal is "+(i+1),""+ex.getLocalizedMessage());}

                name[i] = "Bill No. :" + data.elementAt(0);
                details[i] = "" + data.elementAt(1)+" | "+overdue+" | "+(Math.abs(Double.valueOf(paidamount).intValue()));
                amount[i] = "" + getResources().getString(R.string.Rs) + " " + Math.abs(Double.valueOf(bal).intValue());
                totalamount += Math.abs(Double.valueOf(bal).intValue());
                try {
                    if(onacc==0) {
                        onacc = (Double.parseDouble("" + data.elementAt(6)));
                    }
                }catch (Exception ex){onacc=0;}
            }
            txttotal.setText(""+Cutomername+" " + Double.valueOf(totalamount).intValue());
            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewOutstanding.setLayoutManager(llm);
            AdapterOutstandingDetails adapter = new AdapterOutstandingDetails(name, details, amount, OutstandingDetails.this);
            recyclerViewOutstanding.setLayoutManager(llm);
            recyclerViewOutstanding.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                    DividerItemDecoration.HORIZONTAL));
            recyclerViewOutstanding.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                    DividerItemDecoration.VERTICAL));
            recyclerViewOutstanding.setAdapter(adapter);
        } catch (Exception ex) {
            Log.e("Error 1", "" + ex.getLocalizedMessage());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_date:
                //   showDialog(DATE_PICKER);
                break;
            case R.id.action_pdf :
                 createPDFInvoice(Cutomername);
                break;
            case R.id.action_search:
               redirectSearchScreen();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }
    public void createPDFInvoice(String str)
    {
        Document doc = new Document();
        // btnconfirm.setEnabled(true);


        try {

            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_READ_PHONE_STATE);
            }
            permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE);
            }
            String path="";
            File dir=null;

            String extStorageState = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            }
            extStorageState = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            }
            try {
                //Log.i("Version",""+android.os.Build.VERSION.SDK_INT);
                if(android.os.Build.VERSION.SDK_INT < 29) {
                    path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfData";
                    //Log.i("path 1", "" + path);
                    dir = new File(path);
                    if (!dir.exists())
                        dir.mkdirs();
                }
                else
                {
                    try {
                        //path = Environment.getExternalStoragePublicDirectory(null).getAbsolutePath() + "/PdfData";
                        path = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/PdfData";
                        //Log.i("path 2", "" + path);
                        dir = new File(path);
                        if (!dir.exists())
                            dir.mkdirs();
                    }catch (Exception ex){Log.e("Version error",""+ex.getLocalizedMessage());}
                }
            }
            catch (Exception ex)
            {

                path=Environment.getExternalStoragePublicDirectory(null).getAbsolutePath()+"/PdfData";
                //Log.i("path 2",""+path);
                dir = new File(path);
                if (!dir.exists())
                    dir.mkdirs();
                //path = Environment.getExternalFilesDir().getAbsolutePath() + "/PdfData";
            }
            Log.d("PDFCreator", "PDF Path: " + path);
            SimpleDateFormat Ins_Date = new SimpleDateFormat("d-MM-yyyy ss");
            String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
            String filename=""+Cutomername+" "+InsDate+" R.pdf";
            Log.i("File Name",""+filename);
            filename=filename.replace("/","");
            filename=filename.replace("\"","");
            filename=filename.replace(":","");
            filename=filename.replace("*","");
            filename=filename.replace("?","");
            filename=filename.replace("<","");
            filename=filename.replace(">","");
            filename=filename.replace("|","");

            File file = new File(dir, filename);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_person);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);

            //add image to document
            // doc.add(myImg);
            addTitlePage(doc);
            Log.i("File",""+file.toString());
            Log.i("Doc",""+doc.toString());
          StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PdfData";
            File outputFile = new File(path, filename);
            Uri uri = Uri.fromFile(file);

            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            if (Build.VERSION.SDK_INT >= 30)
            {
                Log.i("Version ","30");
                // uri=Uri.parse(file.getPath());
                uri= FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            }
            else
            {
                uri= Uri.fromFile(file);
            }
            Log.i("URI",""+uri.toString());

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                // shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Outstanding Details.");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("application/pdf");
                startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
            catch (Exception ex){Log.e("Sharing Eror",""+ex.getLocalizedMessage());}

        } catch (DocumentException de) {
            Log.e("Error 1", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("Error 2", "ioException:" + e);
        }
        catch (Exception ex){Log.e("Error 3",""+ex.getMessage());}
        finally
        {
            doc.close();
        }


    }
    public void addTitlePage(Document document) throws DocumentException
    {
// Font Style for Document
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD
                | Font.UNDERLINE, BaseColor.GRAY);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

// Start New Paragraph
        Paragraph prHead = new Paragraph();
// Set Font in this Paragraph
        prHead.setFont(titleFont);
// Add item into Paragraph
        prHead.add(""+company.elementAt(1).toString()+"  "+company.elementAt(2).toString()+"\n");

// Create Table into Document with 1 Row


        prHead.setFont(catFont);
        // prHead.add("\n"+txtname.getText()+"\n\n");
        // prHead.add("\n Proforma Invoice \n\n");
        prHead.setAlignment(Element.ALIGN_CENTER);



        document.add(prHead);

        Paragraph orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_CENTER);
        orddet.add("\n"+company.elementAt(3).toString());

        orddet.setFont(smallBold);
        document.add(orddet);
        orddet=new Paragraph();
        orddet.setFont(smallBold);
        orddet.setAlignment(Element.ALIGN_RIGHT);
        // orddet.add("\nDate :  1-4-2019");
        //  orddet.add("\nTo  :  31-3-2020");
        Calendar c=Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        String formattedDate = df.format(c.getTime());

        orddet.add("\n Date : "+formattedDate);
        orddet.add("\n");
        document.add(orddet);
        orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_CENTER);
        orddet.setFont(catFont);
        orddet.add("\nOutstanding Details");
        document.add(orddet);
        orddet=new Paragraph();
        orddet.setFont(normal);
        orddet.add("\n"+Cutomername+"\n");
        orddet.setFont(smallBold);
        document.add(orddet);
        orddet = new Paragraph();
        String value="";
        try {
            //-//  value=""+customer.elementAt(3);
            if(value.equals("null")||value.equals("NULL")||value.trim().equals(""))
            {
                //Log.i("0","is null");
            }
            else
            {
                //-//  orddet.add("\n" + customer.get(3)+"\n\n");
            }
        }
        catch (Exception ex){Log.e("Location ","0");}

        document.add(orddet);
        orddet=new Paragraph();
        orddet.setFont(normal);
        orddet.add("\nBillwise Detail\n\n");
        orddet.setFont(smallBold);
        document.add(orddet);


        //document.add(prPersinalInfo);
        //document.add(myTable);
        //document.add(myTable);
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100.0f);
        table.setWidths(new int[]{2,2,2,2,2});
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(""));
        cell.setRowspan(5);
        cell.setBorder(Rectangle.BOX);
        table.addCell("Date");
        table.addCell("Bill No");
        table.addCell("Bill Amount");
        table.addCell("Paid Amount");
        table.addCell("Balance");
        double total=0,credit=0,bal=0,paidamount=0,onacc=0;
        Log.i("Party Outs PDF",""+partyouts.toString());
        for(int i=0;i<partyouts.size();i++) {
            bal=0;
            Vector data=(Vector)partyouts.elementAt(i);
            Log.e("Cur Data",""+data.toString());
            // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            try
            {
                bal=Double.parseDouble(""+data.elementAt(3));
                try
                {
                    paidamount = Double.parseDouble(data.elementAt(5)
                            .toString().trim())
                            - Double.parseDouble(data.elementAt(3).toString()
                            .trim());
                }
                catch (Exception ex)
                {
                    Log.e("Paid Amt",""+paidamount);
                    paidamount=0;
                }
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(""+(data.elementAt(1)));
                table.addCell(""+data.elementAt(0) );
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                try {
                    table.addCell("" + Math.abs(Double.parseDouble(""+data.elementAt(5))));
                }
                catch (Exception ex){table.addCell("" + data.elementAt(5));}
                table.addCell(""+(Math.abs(Double.valueOf(paidamount).intValue())));
                table.addCell("" + Math.abs(Double.valueOf(bal).intValue()));
                total=total+bal;
                onacc=Double.parseDouble(""+data.elementAt(6));

            }
            catch (Exception ex)
            {
                Log.i("Error in ","Generate PDF Data "+ex.getLocalizedMessage());
                //Toast.makeText(getApplicationContext(),"Outstanding Export Error In PDF",Toast.LENGTH_SHORT).show();
            }
        }


        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell("Total");
        table.addCell(""+(Math.abs(total)));
        try
        {
            // double op=Double.parseDouble(""+customer.get(4));
            //  double cl=Double.parseDouble(""+customer.get(5));
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("On Account ");
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(""+ (Math.abs(onacc)));

            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("Closing Bal.");
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(""+(Math.abs(Double.parseDouble(""+closing))));
            document.add(table);
        }
        catch (Exception ex){ Log.e("Error in","Finalise PDF "+ex.getLocalizedMessage());}

        // Toast.makeText(getApplicationContext(),"File Exported Sucessfully",Toast.LENGTH_SHORT).show();
    }
    public void redirectSearchScreen()
    {
        Intent intent = new Intent(getApplicationContext(), SearchCustomer.class);
        someActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    try {
                        Intent data = result.getData();
                        model = Model.getInstance();
                        mydb=new Database(getApplicationContext());
                        Cutomername=""+data.getStringExtra("SelectedCustomer");
                        model.setPartyName("" + data.getStringExtra("SelectedCustomer"));
                        closing=mydb.getPartyClosingBalance(Cutomername);
                        loadOutstanding();
                        } catch (Exception ex) {
                        Log.e("Error Act Result", "" + ex.getLocalizedMessage());
                    }
                }
            });
    private void getBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        Vector data=mydb.getPartyOutstandingMonthCount(Cutomername);
        for (int i=0;i<data.size();i++) {
            barEntriesArrayList.add(new BarEntry((i+1), Float.parseFloat(""+data.elementAt(i))));
        }


        barDataSet = new BarDataSet(barEntriesArrayList, "Outstanding Bill Count Of Monthwise");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);
        barChart.zoomIn();barDataSet = new BarDataSet(barEntriesArrayList, "Total Outstanding  Monthwise");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);
        barChart.zoomIn();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Jan");
        xAxisLabel.add("Feb");
        xAxisLabel.add("Mar");
        xAxisLabel.add("Apr");
        xAxisLabel.add("May");
        xAxisLabel.add("Jun");
        xAxisLabel.add("Jul");
        xAxisLabel.add("Aug");
        xAxisLabel.add("Sep");
        xAxisLabel.add("Oct");
        xAxisLabel.add("Nov");
        xAxisLabel.add("Dec");
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        ValueFormatter formatter=null;

        formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                value = value - 1;
                try
                {
                    return xAxisLabel.get((int) value);
                }
                catch (Exception ex)
                {
                    Log.e("Exce Occer",""+ex.getLocalizedMessage());
                    value=value+1;
                    return xAxisLabel.get((int) value);
                }

            }

        };

        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
        //  barChart.zoomIn();

        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
    }
}
