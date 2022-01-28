package r3.aurangabad.rthreetrade;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Vector;

public class RebateDetails extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE = 1;
    Model model;
    RecyclerView recyclerViewRebate;
    TextView txttotal;
    Database mydb;
    Vector partyRebate,company;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;
    String Cutomername="";
    int year_x,month_x,day_x;
    static final int DILOG_ID=0;
    int DATE_PICKER = 0;
    Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebate_details);
        model = Model.getInstance();
        mydb = new Database(getApplicationContext());
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        try {
            Cutomername = b.getString("Customer");
        } catch (Exception ex) {
            Log.e("Error Get values", "" + ex.getLocalizedMessage());
        }
        company = new Vector();
        try {company = mydb.getCompanyDetails(); }catch (Exception ex){}
        recyclerViewRebate = (RecyclerView) findViewById(R.id.recyler_customerrebate);
        txttotal = (TextView)findViewById(R.id.txt_totalrebate);
        barChart = (BarChart) findViewById(R.id.barchart_detailrebate);
        partyRebate=mydb.getPartyRebate(Cutomername);
        loadRebate();
        //getBarEntries();
    }
    void loadRebate() {
        try {
            double totalamount = 0;
            String total = "";
            mydb = new Database(getApplicationContext());
            model = Model.getInstance();
            //partyRebate = mydb.getPartyRebate(Cutomername);

            String name[] = new String[partyRebate.size()];
            String details[] = new String[partyRebate.size()];
            String amount[] = new String[partyRebate.size()];
            for (int i = 0; i < partyRebate.size(); i++) {
                Vector data = (Vector) partyRebate.elementAt(i);
                name[i] = "" + data.elementAt(0);
                details[i] = "Total Cs:" + data.elementAt(2)+"  Total Btl:" + data.elementAt(3);
                total = "0";
                total = "" + data.elementAt(1);
                amount[i] = ""+ Double.valueOf(total).intValue();
                totalamount += Double.valueOf(total).intValue();
            }
            txttotal.setText("" + Double.valueOf(totalamount).intValue());
            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewRebate.setLayoutManager(llm);
            AdapterRebateDetails adapter = new AdapterRebateDetails(name, details, amount,RebateDetails.this);
            recyclerViewRebate.setLayoutManager(llm);
            recyclerViewRebate.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                    DividerItemDecoration.HORIZONTAL));
            recyclerViewRebate.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                    DividerItemDecoration.VERTICAL));
            recyclerViewRebate.setAdapter(adapter);
            // rebatemonth=mydb.getAllOutstandingMonthCount();
        } catch (Exception ex) {
            Log.e("Error 1", "" + ex.getLocalizedMessage());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_date:
                loadMonthSelector();
                break;
            case R.id.action_pdf :
                createPDFInvoice(Cutomername);
                break;
            case R.id.action_search:
                //redirectSearchScreen();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
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
                        String Cutomername=""+data.getStringExtra("SelectedCustomer");
                        model.setPartyName("" + data.getStringExtra("SelectedCustomer"));
                        String closing=mydb.getPartyClosingBalance(Cutomername);
                        Intent intent=new Intent(getApplicationContext(),OutstandingDetails.class);
                        intent.putExtra("Customer",""+Cutomername);
                        startActivity(intent);
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
        Vector bardata=mydb.getPartyRebateMonthCount(Cutomername);
        for (int i=0;i<bardata.size();i++) {
            Vector data= (Vector) bardata.elementAt(i);
            barEntriesArrayList.add(new BarEntry((i+1), Float.parseFloat(""+data.elementAt(1))));
        }
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


        barDataSet = new BarDataSet(barEntriesArrayList, "Outstanding Bill Count Of Monthwise");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
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
          //  Log.i("URI",""+uri.toString());

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
        orddet.add("\nParty Lifting Report (Rebate)");
        document.add(orddet);
        orddet=new Paragraph();
        orddet.setFont(normal);
        orddet.add("\nParty Name : "+ Cutomername +"\n\n");
        orddet.setFont(smallBold);
        document.add(orddet);

        Vector rebate=mydb.getAllRebateByPartyName(Cutomername);
        //Log.i("Total Rebt",""+rebate.size());
        //Vector customer=mydb.getCustomerDetails(partyname);
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100.0f);
        table.setWidths(new int[]{1,1,2,2,1,1,1});
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(""));
        cell.setRowspan(6);
        cell.setBorder(Rectangle.BOX);
        table.addCell("Bill No");
        table.addCell("Date");
        table.addCell("Brand");
        table.addCell("Company");
        table.addCell("Cases");
        table.addCell("Bottle");
        table.addCell("Amount");
        double total=0,cs=0,btl=0,paidamount=0;
        for(int i=0;i<rebate.size();i++) {
            Vector data=(Vector)rebate.elementAt(i);
            // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            try
            {
                try
                {
                    paidamount = Double.parseDouble(data.elementAt(6)
                            .toString().trim());
                    total+=paidamount;
                }
                catch (Exception ex)
                {paidamount=0;}
                try
                {
                    cs+= Double.parseDouble(data.elementAt(4)
                            .toString().trim());
                }
                catch (Exception ex)
                {}
                try
                {
                    btl+= Double.parseDouble(data.elementAt(5)
                            .toString().trim());
                }
                catch (Exception ex)
                {}
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

                String company=""+data.elementAt(7);
                company=company.toLowerCase(Locale.ROOT);
                try {

                    if (company.contains("gift")) {
                        continue;
                    }
                }catch (Exception ex){}
                table.addCell(""+(data.elementAt(0)));
                table.addCell(""+data.elementAt(3));
                table.addCell(""+data.elementAt(2).toString());
                table.addCell("" + data.elementAt(7));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(""+data.elementAt(4));
                table.addCell(""+data.elementAt(5));
                table.addCell(""+(Double.valueOf(paidamount).intValue()));


            }
            catch (Exception ex)
            {
                Log.e("Export error",""+ex.getLocalizedMessage());
                Toast.makeText(getApplicationContext(),"Rebate Export Error In PDF",Toast.LENGTH_SHORT).show();
            }


        }
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("Total");
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(""+(int)cs);
        table.addCell(""+(int)btl);
        table.addCell(""+Double.valueOf(Math.abs(total)).intValue());
        try
        {
             document.add(table);

        }
        catch (Exception ex){}

        // Toast.makeText(getApplicationContext(),"File Exported Sucessfully",Toast.LENGTH_SHORT).show();
    }


    public void loadMonthSelector() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LinearLayout someLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.month_picker_dialog, null);
            final Spinner spmonth = (Spinner) someLayout.findViewById(R.id.sp_selectmonth);

            AlertDialog.Builder title = builder.setTitle("Select Month");
            title.setMessage("");
            Resources res = getResources();
            ArrayList<String> arrlst=new ArrayList<>(12);
            arrlst.add("January");
            arrlst.add("February ");
            arrlst.add("March ");
            arrlst.add("April");
            arrlst.add("May");
            arrlst.add("June");
            arrlst.add("July");
            arrlst.add("August");
            arrlst.add("September");
            arrlst.add("October");
            arrlst.add("November");
            arrlst.add("December");
            ArrayAdapter ard = new ArrayAdapter(this, R.layout.spinner_item, arrlst);
            ard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spmonth.setAdapter(ard);
            title.setView(someLayout);
            title.setPositiveButton("Save ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  String strmonth="";

                   if(spmonth.getSelectedItem().toString().equals("January"))
                   {
                       strmonth="Jan";
                   }
                    if(spmonth.getSelectedItem().toString().equals("February"))
                    {
                        strmonth="Feb";
                    }
                    if(spmonth.getSelectedItem().toString().equals("March"))
                    {
                        strmonth="Mar";
                    }
                    if(spmonth.getSelectedItem().toString().equals("April"))
                    {
                        strmonth="Apr";
                    }
                    if(spmonth.getSelectedItem().toString().equals("May"))
                    {
                        strmonth="May";
                    }
                    if(spmonth.getSelectedItem().toString().equals("June"))
                    {
                        strmonth="Jun";
                    }
                    if(spmonth.getSelectedItem().toString().equals("July"))
                    {
                        strmonth="Jul";
                    }
                    if(spmonth.getSelectedItem().toString().equals("August"))
                    {
                        strmonth="Aug";
                    }
                    if(spmonth.getSelectedItem().toString().equals("September"))
                    {
                        strmonth="Sept";
                    }
                    if(spmonth.getSelectedItem().toString().equals("October"))
                    {
                        strmonth="Oct";
                    }
                    if(spmonth.getSelectedItem().toString().equals("November"))
                    {
                        strmonth="Nov";
                    }
                    if(spmonth.getSelectedItem().toString().equals("December"))
                    {
                        strmonth="Dec";
                    }
                    Log.i("Month IS",""+strmonth);

                    partyRebate=mydb.getPartyRebateByMonth(""+Cutomername,strmonth);
                    loadRebate();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //InputMethodManager inputMethodManager = (InputMethodManager) getSystemService("input_method");
                }
            });
            builder.show();
        }catch (Exception ex){Log.e("Error In List Click",""+ex.getLocalizedMessage());}

    }

}