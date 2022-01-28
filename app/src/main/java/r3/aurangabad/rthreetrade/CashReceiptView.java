package r3.aurangabad.rthreetrade;


import static com.itextpdf.text.BaseColor.BLACK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ngx.BluetoothPrinter;
import com.ngx.PrinterWidth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CashReceiptView extends AppCompatActivity {
    private static final int INTERNET = 1;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    Database mydb;
    Vector company;
    Model model;
    ListView receiptlist;
    Vector receipts,customerdet,curreceipts;
    public static final String MyPREFERENCES = "myprefs";
    public static final  String value = "key";
    private String mConnectedDeviceName = "";
    public static final String title_connecting = "connecting...";
    public static final String title_connected_to = "connected: ";
    public static final String title_not_connected = "not connected";
    private static FragmentManager fragMgr;
    private UnicodeFragment nm;
    public static SharedPreferences mSp;
    public static BluetoothPrinter mBtp = BluetoothPrinter.INSTANCE;
    boolean isprinterconnected=true;
    int year_x,month_x,day_x;
    static final int DILOG_ID=0;
    int DATE_PICKER = 0;
    Calendar c;
    Vector allreceiptnumbers,getAllreceiptseqdates;
    @SuppressLint("HandlerLeak")
    private final android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothPrinter.STATE_CONNECTED:
                            Log.i("Msg 1",title_connected_to);
                            Log.i("Msg 2",mConnectedDeviceName);
                            Toast.makeText(getApplicationContext(),"Printer is Connected",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothPrinter.STATE_CONNECTING:
                            Log.i("Msg 3",title_connecting);
                            break;
                        case BluetoothPrinter.STATE_LISTEN:
                        case BluetoothPrinter.STATE_NONE:
                            Log.i("Msg 4",title_not_connected);
                            break;
                    }
                    break;
                case BluetoothPrinter.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(
                            BluetoothPrinter.DEVICE_NAME);
                    break;
                case BluetoothPrinter.MESSAGE_STATUS:
                    Log.i("Msg 5",msg.getData().getString(
                            BluetoothPrinter.STATUS_TEXT));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_receipt_view);
        receiptlist=(ListView)findViewById(R.id.lst_receiptview);
        mydb = new Database(getApplicationContext());
        model = Model.getInstance();
        c = Calendar.getInstance();
        year_x=c.get(Calendar.YEAR);
        month_x=c.get(Calendar.MONTH);
        day_x=c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        model.setTodaysDate(formattedDate);
        loadReceiptDetailListItems();
        company=new Vector();
        company=mydb.getCompanyDetails();
        if(company.elementAt(7).toString().equals("YES")) {
            Log.i("Print Config","YES");
            fragMgr = this.getFragmentManager();

            // lockOrientation(this);

            mSp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            try {
                mBtp.initService(getApplicationContext(), mHandler);
            } catch (Exception e) {
                Log.i("Error Printre",""+e.getLocalizedMessage());
                e.printStackTrace();
            }
            nm = new UnicodeFragment();
            Log.i("NM is",""+nm.toString());
        }

        receiptlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Vector data = (Vector) receipts.elementAt(position-1);
                mydb=new Database(getApplicationContext());
                model=Model.getInstance();
                AlertDialog.Builder builder = new AlertDialog.Builder(CashReceiptView.this);
                builder.setTitle(""+data.elementAt(2).toString());
                model.setPartyName(""+data.elementAt(2).toString());
                builder.setMessage("Do you want to share ..?");
                builder.setCancelable(true);

                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // createPDFInvoice("");

                    }
                });
                builder.setNegativeButton("Synchronize", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        model.setPartyName(""+data.elementAt(2).toString());
                        customerdet = mydb.getCustomerDetails(model.getPartyName());
                        if(isNetworkAvailable()) {
                            mydb=new Database(getApplicationContext());
                            SyncReceiptDetails();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"No Internet Connection Available For Sync",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNeutralButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPrinttingData();
                      //  Log.i("Cur Rec",""+curreceipts.toString());
                        createPDFInvoice("");
                    }
                });
                builder.setPositiveButton("Print", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (company.elementAt(7).toString().equals("YES")) {
                            setPrinttingData();
                            // Log.i("Rec For Print "+receipts.size(),""+receipts.toString());
                            for(int i=0;i<curreceipts.size();i++)
                            {
                                printEnglishBill(i);
                            }
                        }
                    }

                });
                builder.show();
            }
        });

        //return inflater.inflate(R.layout.view_receipt_fragment, container, fa lse);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_date:
               showDialog(DATE_PICKER);
               break;
            case R.id.action_pdf :
             //   printTodaysBillReport();
                break;
            case R.id.action_clear_device:
                // deletes the last used printer, will avoid auto connect
                AlertDialog.Builder d = new AlertDialog.Builder(this);
                d.setTitle("NGX Bluetooth Printer");
                // d.setIcon(R.drawable.ic_launcher);
                d.setMessage("Are you sure you want to delete your preferred Bluetooth printer ?");
                d.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBtp.clearPreferredPrinter();
                                Toast.makeText(getApplicationContext(),
                                        "Preferred Bluetooth printer cleared",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                d.setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });
                d.show();
                return true;
            case R.id.action_connect_device:
                // show a dialog to select from the list of available printers
                try {
                    if(company.size()>0) {
                        if (company.elementAt(7).toString().equals("YES")) {

                            mBtp.showDeviceList(this);
                        }}


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_unpair_device:
                AlertDialog.Builder u = new AlertDialog.Builder(this);
                u.setTitle("Bluetooth Printer unpair");
                // d.setIcon(R.drawable.ic_launcher);
                u.setMessage("Are you sure you want to unpair all Bluetooth printers ?");
                u.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (mBtp.unPairBluetoothPrinters()) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "All NGX Bluetooth printer(s) unpaired",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                u.setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });
                u.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }


    private void loadReceiptDetailListItems()
    {
        try
        {
            model=Model.getInstance();
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.listview_animation);
            receiptlist.setLayoutAnimation(controller);
            receipts = mydb.getTodaysReceiptAll();
            //Log.i("All Rec",""+receipts.toString());
            if(receipts.size()==0)
            {
                Toast.makeText(getApplicationContext(),"No Receipts Entered in this date ."+model.getTodaysDate(),Toast.LENGTH_SHORT).show();
            }
            String []pname=new String[receipts.size()+1];
            String []pdate=new String[receipts.size()+1];
            String []ptotal=new String[receipts.size()+1];
            String []oid=new String[receipts.size()+1];
            pname[0]="Party Name";
            pdate[0]="Date";
            ptotal[0]="Amount";
            oid[0]="0";
            // //Log.i("Total Receipt",""+receipts.toString());
            for(int i=0;i<receipts.size();i++)
            {
                Vector data=(Vector)receipts.elementAt(i);
                pname[i+1]=""+data.elementAt(2)+"\n"+"Rec No: "+data.elementAt(0);
                pdate[i+1]=""+data.elementAt(1);
                ptotal[i+1]=""+data.elementAt(4)+"\n( "+data.elementAt(5)+" )";
                oid[i+1]=""+data.elementAt(0).toString();
                // Log.e("Det"+i,""+data.toString());

            }
            //Log.e("Total Length ",""+orderdet.size());
            ViewReceiptAdapter adapter=new ViewReceiptAdapter(getApplicationContext(),pname,pdate,ptotal,oid);
            receiptlist.setAdapter(adapter);
        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(),"No Receipts Entered in this date .",Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();


            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } else {
            //TODO
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();

            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //Log.e("Internet 3","Available");
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        }

    }
    public void SyncReceiptDetails()
    {
        try
        {
            ProgressDialog Dialog = new ProgressDialog(CashReceiptView.this);
            Dialog.setMessage("Synchronising Receipt");
            Dialog.show();
            Vector locdet=new Vector();
            model=Model.getInstance();
            Vector recdetails = mydb.getReceiptDetails();
            Vector recdet=new Vector();
            Vector recsequence=new Vector();
            String allreceipt="",receiptall="",recdetall="",receiptlocation="",receiptSequence="";
            SimpleDateFormat Ins_Date=new SimpleDateFormat("d/MM/yyyy");
            String InsDate=Ins_Date.format(Calendar.getInstance().getTime());
            allreceiptnumbers=new Vector();
            getAllreceiptseqdates=new Vector();
            for (int i = 0; i < recdetails.size(); i++)
            {
                Vector curdata = (Vector) recdetails.elementAt(i);
                model.setRecNumber(curdata.elementAt(3).toString());
                // model.setPartyName(curdata.elementAt(1).toString());
                Vector rec = mydb.getReceiptByRecNo();
                recdet=mydb.getReceiptDetailsForSync();

                recsequence.addElement(mydb.getReceiptSequenceByDate(rec.elementAt(1).toString()));
                allreceiptnumbers.addElement(rec.elementAt(0));
                getAllreceiptseqdates.addElement(rec.elementAt(1));
                allreceipt += rec.elementAt(0).toString().trim() + "~~~"
                        + rec.elementAt(1).toString().trim() + "~~~"
                        + rec.elementAt(2).toString().trim() + "~~~"
                        + rec.elementAt(3).toString().trim() + "~~~"
                        + rec.elementAt(4).toString().trim() + "~~~"
                        + rec.elementAt(5).toString().trim() + "~~~"
                        + InsDate.trim() + "~~~"
                        + rec.elementAt(6).toString().trim() + "~~~"
                        + rec.elementAt(7).toString().trim() + "~~~"
                        + rec.elementAt(8).toString().trim() + "^^^";

                recdetall += recdet.elementAt(0).toString().trim() + "~~~"
                        + recdet.elementAt(1).toString().trim() + "~~~"
                        + recdet.elementAt(2).toString().trim() + "~~~"
                        + recdet.elementAt(3).toString().trim() + "~~~"
                        + recdet.elementAt(4).toString().trim() + "^^^";

            }
            try {
                if (recdetails.size() > 0) {
                    Vector data = (Vector) recdetails.elementAt(0);
                    model.setRecNumber(data.elementAt(3).toString());
                    Vector recall = mydb.getReceiptAllByRecNo();
                    receiptall += recall.elementAt(0).toString().trim() + "~~~"
                            + recall.elementAt(1).toString().trim() + "~~~"
                            + recall.elementAt(2).toString().trim() + "~~~"
                            + recall.elementAt(3).toString().trim() + "~~~"
                            + recall.elementAt(4).toString().trim() + "~~~"
                            + recall.elementAt(5).toString().trim() + "~~~"
                            + InsDate.trim() + "~~~"
                            + recall.elementAt(6).toString().trim() + "~~~"
                            + recall.elementAt(7).toString().trim() + "^^^";
                }
            }
            catch (Exception ex){Log.e("Error in Rec Det",""+ex.getLocalizedMessage());}
            try {

                for (int i = 0; i < locdet.size(); i++) {
                    Vector row = (Vector) locdet.elementAt(i);
                    Log.i("Location "+i,""+row.toString());
                    //  Log.i("Order Is",""+row.toString());
                    receiptlocation += row.elementAt(0).toString().trim() + "~~~"
                            + row.elementAt(1).toString().trim() + "~~~"
                            + row.elementAt(2).toString().trim() + "~~~"
                            + row.elementAt(3).toString().trim() + "^^^";

                }
            }
            catch (Exception ex){
                receiptlocation="";
                Log.e("Location Error",""+ex.getLocalizedMessage());}
            try {
                for (int i = 0; i < recsequence.size(); i++) {
                    Vector seqdata=(Vector)recsequence.elementAt(i);
                    receiptSequence += seqdata.elementAt(0).toString().trim() + "~~~"
                            + model.getSalesman() + "~~~"
                            + seqdata.elementAt(1).toString().trim() + "~~~"
                            + seqdata.elementAt(2).toString().trim() + "~~~"
                            + model.getTallyName() + "^^^";
                }
            }catch (Exception ex){Log.e("Sequence Error",""+ex.getLocalizedMessage());}


           OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(model.getUrl_address())
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
           /* Log.i("allreceipt",""+allreceipt);
            Log.i("receiptall",""+receiptall);
            Log.i("recdetall",""+recdetall);
            Log.i("model.getSalesman()",""+model.getSalesman());
            Log.i("InsDate",""+model.getPartyName());
            Log.i("receiptlocation",""+receiptlocation);
            Log.i("receiptSequence",""+receiptSequence);*/

            // RetrofitReceiptSync api=retrofit.create(RetrofitReceiptSync.class);
            //Call<ResponseBody> call=api.insertReceipt(allreceipt,receiptall,recdetall,model.getSalesman(),InsDate,receiptlocation,"YES",receiptSequence);
            RetrofitReceiptSync api=retrofit.create(RetrofitReceiptSync.class);
            Call<ResponseBody> call=api.insertReceipt(allreceipt,receiptall,recdetall,model.getSalesman(),InsDate,receiptlocation,"YES",receiptSequence);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.i("Response",""+response.message());
                    Toast.makeText(getApplicationContext(),"Receipt Sync Sucessfully",Toast.LENGTH_SHORT).show();
                    mydb.updateRecSequenceUpload(getAllreceiptseqdates);
                    mydb.updateReceiptUpload(allreceiptnumbers);
                    mydb.updateReceiptUploadAll(allreceiptnumbers);
                    mydb.updateReceiptUploadDetails(allreceiptnumbers);
                    Dialog.dismiss();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Receipt Not Sync Sucessfully "+t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex){

            Log.e("Rec Sync Error",""+ex.getLocalizedMessage());
        }

    }
    public String getImeiNumber() {

        String deviceid = "";
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                Log.i("7", "Permitted 1");
            }
            TelephonyManager mngr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return "" + Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
            if (android.os.Build.VERSION.RELEASE.startsWith("10")) {
                deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("1 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            } else {
                deviceid = mngr.getDeviceId().toString().trim();
                Log.i("2 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            }
            if (android.os.Build.VERSION.SDK_INT > 28) {
                deviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("3 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            } else {
                deviceid = mngr.getDeviceId().toString().trim();
                Log.i("4 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            }

            return deviceid;


        } catch (Exception ex) {
            Log.e("Error IMEI", "" + ex.getLocalizedMessage());
            return deviceid;
        }
    }
 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //  Toast.makeText(getApplicationContext(),"CLicked ", Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.action_clear_device:

                // deletes the last used printer, will avoid auto connect
                AlertDialog.Builder d = new AlertDialog.Builder(getApplicationContext());
                d.setTitle("NGX Bluetooth Printer");
                // d.setIcon(R.drawable.ic_launcher);
                d.setMessage("Are you sure you want to delete your preferred Bluetooth printer ?");
                d.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBtp.clearPreferredPrinter();
                                Toast.makeText(getApplicationContext(),
                                        "Preferred Bluetooth printer cleared",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                d.setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });
                d.show();
                return true;
            case R.id.action_connect_device:
                Log.i("Printer ","Connecton Checking");
                // show a dialog to select from the list of available printers
                try {
                    if(company.size()>0) {
                        if (company.elementAt(7).toString().equals("YES")) {

                            mBtp.showDeviceList(this);
                        }}
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_date:
                loadDateDialog();
                return true;


            case R.id.action_unpair_device:
                AlertDialog.Builder u = new AlertDialog.Builder(getApplicationContext());
                u.setTitle("Bluetooth Printer unpair");
                // d.setIcon(R.drawable.ic_launcher);
                u.setMessage("Are you sure you want to unpair all Bluetooth printers ?");
                u.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (mBtp.unPairBluetoothPrinters()) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "All NGX Bluetooth printer(s) unpaired",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        }
        return super.onOptionsItemSelected(item);
    }
*/
    private void printEnglishBill(int position) {

        Vector printdet=(Vector) curreceipts.elementAt(position);
        try {
            if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
                Toast.makeText(getApplicationContext(), "Printer is not connected", Toast.LENGTH_SHORT).show();
                isprinterconnected=false;
                return;
            }
            isprinterconnected=true;
            mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_48MM);
            mSp.edit().putInt("PRINTER_SELECTION", 2).commit();
            String separator = "-----------------------------";
            Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/DroidSansMono.ttf");

            TextPaint tp = new TextPaint();
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(40);
            tp.setColor(Color.BLACK);
            mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_48MM);
            mBtp.addText(""+company.elementAt(1).toString(), Layout.Alignment.ALIGN_CENTER, tp);
            mBtp.addText(""+company.elementAt(2).toString(), Layout.Alignment.ALIGN_CENTER, tp);
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(27);
            tp.setColor(Color.BLACK);
            mBtp.addText(""+company.elementAt(3).toString(), Layout.Alignment.ALIGN_CENTER, tp);

            StringBuilder stringBuilder = new StringBuilder();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy");
            String formattedDate = df.format(c.getTime());
            stringBuilder.append("\n"+"Rec No: "+printdet.elementAt(0)+"\n");
            stringBuilder.append("Lic No.:"+customerdet.elementAt(8)+"  "+customerdet.elementAt(9)+"\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("Name: "+model.getPartyName()+"\n");
            stringBuilder.append("From: "+model.getSalesman()+"\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("Particulars            Amount\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("Cash                  " + printdet.elementAt(4) + " \n");
            stringBuilder.append(" \n\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("Total Amount          "+printdet.elementAt(4)+"\n");
            stringBuilder.append("\n");
            stringBuilder.append("                -------------");
            tp.setTextSize(22);
            tp.setTypeface(tf);
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);

            stringBuilder.setLength(0);
            stringBuilder.append("In Words : "+ EnglishNumberToWords.convert(Integer.parseInt(printdet.elementAt(4).toString()))+" Rupees only");
            tp.setTextSize(28);
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);


            stringBuilder.setLength(0);
            stringBuilder.append("\n\n         Authorised signatory\n\n");
            stringBuilder.append(""+separator);
            stringBuilder.append("\n          Thank You.           ");
            stringBuilder.append("\n"+separator+"\n\n\n");

            tp.setTextSize(20);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Layout.Alignment.ALIGN_NORMAL, tp);
            mBtp.print();
        }
        catch (Exception ex)
        {
            Log.e("Error print",""+ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }
    public void setPrinttingData()
    {
      //  ProgressDialog Dialog = new ProgressDialog(getApplicationContext());
        try {

        //    Dialog.setMessage("Printing  Receipt");
        //    Dialog.show();
            model=Model.getInstance();
            mydb=new Database(getApplicationContext());
            Vector recdetails = mydb.getReceiptDetails();
            Log.i("Rec Det",""+recdetails.toString());
            curreceipts=new Vector();
            customerdet=mydb.getCustomerDetails(model.getPartyName());
            Collections.replaceAll(customerdet, null, "");
            for (int j = 0; j < recdetails.size(); j++)
            {
                Vector curdata = (Vector) recdetails.elementAt(j);
                model.setRecNumber(curdata.elementAt(3).toString());
                // model.setPartyName(curdata.elementAt(1).toString());
                Vector rec = mydb.getReceiptByRecNo();
              //  Log.i("Rec IS ",""+rec.toString());
                Vector printdet=new Vector();
                printdet.addElement(rec.elementAt(0).toString().trim());
                printdet.addElement(customerdet.elementAt(8) + "  " + customerdet.elementAt(9));
                printdet.addElement(rec.elementAt(3).toString().trim());
                printdet.addElement("" + rec.elementAt(2).toString().trim());
                try{
                    printdet.addElement(Integer.parseInt(rec.elementAt(4).toString().trim()));
                }
                catch (Exception ex){printdet.addElement(rec.elementAt(4).toString().trim());}
                printdet.addElement("Cash");
                printdet.addElement("");
                printdet.addElement("");
                curreceipts.addElement(printdet);

            }

           // Dialog.dismiss();
        }
        catch (Exception ex){
            Log.e("Error in set Data",""+ex.getLocalizedMessage());
            //Dialog.dismiss();
        }
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
            String filename=""+model.getPartyName()+" "+InsDate+" R.pdf";
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
            Log.i("Comming Hear","YES "+ Build.VERSION.SDK_INT);
            for(int i=0;i<curreceipts.size();i++) {
                addTitlePage(doc,i);
                //Log.i("Comming Hear","YES 2 "+(i+1));
            }
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
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Receipt Details.");
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
    public void addTitlePage(Document document,int position) throws DocumentException
    {
// Font Style for Document
        if(position!=0)
        {
            document.newPage();
        }
        Vector invdata=(Vector)curreceipts.elementAt(position);
        //Log.i("Pos",""+invdata.toString());
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD
                | Font.UNDERLINE, BaseColor.GRAY);
        final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

// Start New Paragraph
        Paragraph prHead = new Paragraph();
// Set Font in this Paragraph
        prHead.setFont(titleFont);
// Add item into Paragraph
        prHead.add(""+company.elementAt(1).toString()+" "+company.elementAt(2).toString()+"\n");

// Create Table into Document with 1 Row


        prHead.setFont(catFont);
        // prHead.add("\n"+txtname.getText()+"\n\n");
        // prHead.add("\n Proforma Invoice \n\n");
        prHead.setAlignment(Element.ALIGN_CENTER);



        document.add(prHead);

        Paragraph orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_CENTER);
        orddet.add("\n "+company.elementAt(3).toString());
        orddet.setFont(smallBold);
        document.add(orddet);
        orddet=new Paragraph();
        orddet.setFont(smallBold);


        document.add(orddet);
        orddet = new Paragraph();
        orddet.setFont(smallBold);
        orddet.setAlignment(Element.ALIGN_CENTER);
        orddet.add("\n\nReceipt Voucher");
        document.add(orddet);
        orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_LEFT);
        orddet.add("\nReceipt No. :  "+invdata.elementAt(0));
        document.add(orddet);
        orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_RIGHT);
        orddet.add("Lic No. :  "+invdata.elementAt(1));
        document.add(orddet);
        orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_LEFT);
        orddet.add("Through  :  "+invdata.elementAt(2));
        document.add(orddet);
        orddet = new Paragraph();
        orddet.setAlignment(Element.ALIGN_LEFT);
        orddet.add("\n");
        document.add(orddet);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100.0f);
        table.setWidths(new int[]{6,3});
        PdfPCell cell,cell1;
        BaseFont bf=null;
        try {
            bf = BaseFont.createFont(
                    BaseFont.TIMES_ROMAN,
                    BaseFont.CP1252,
                    BaseFont.EMBEDDED);

        }
        catch (Exception ex){}
        Font font = new Font(bf, 14);
        Font ft;
        ft=new Font(bf,14,Font.BOLD,BLACK);
        cell = new PdfPCell(new Phrase(""));
        cell1 = new PdfPCell(new Phrase(""));
        //cell.setRowspan(2);
        // cell.setBorder(Rectangle.BOX);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPHeaderCell headerCell=new PdfPHeaderCell();
        table.getDefaultCell().setBorderWidth(2f);
        table.addCell("Particulars\n");
        table.addCell("  Amount\n");
        table.getDefaultCell().setBorderWidth(0f);
        // cell.setBorder(Rectangle.NO_BORDER);
        // cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        String sgref="",agref="",account="";
        try
        {
            if(invdata.elementAt(5).toString().equals("Cash"))
            {
                account= "   Cash ";
            }
            else
            {
                account= "   Cheque No."+invdata.elementAt(6)+" Dated :"+invdata.elementAt(7);
            }
        }
        catch (Exception ex){Log.e("Acc Err",""+ex.getLocalizedMessage());}
        try
        {
            agref = agref + "\n  "+account + "       " +invdata.elementAt(4) + " Cr";
        }catch (Exception ex){Log.e("Ref error",""+ex.getMessage());}
        sgref=""+"\nAccount :\n\n       "+invdata.elementAt(3)+"\n";
        table.addCell(sgref+agref+"\n\n");

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("\n"+invdata.elementAt(4));

        try {
            //  cell.setBorder(Rectangle.NO_BORDER);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            String amount_word =   EnglishNumberToWords.convert(Long.parseLong(""+invdata.elementAt(4)));
            table.addCell("\n\n\n\nAmount (in words)\n      "+amount_word+" INR Only.\n\n");
            table.addCell("");
            // cell.setBorder(Rectangle.BOX);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell("");
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBorder(Rectangle.BOX);
            table.getDefaultCell().setBorderWidth(2f);

            table.addCell(""+getResources().getString(R.string.Rs) + " "+invdata.elementAt(4));

            document.add(table);

            Paragraph parabot = new Paragraph();
            parabot.setAlignment(Element.ALIGN_LEFT);
            parabot.add("\n");
            document.add(parabot);

            parabot=new Paragraph();
            parabot.setAlignment(Element.ALIGN_RIGHT);
            parabot.add("\n");
            document.add(parabot);

            parabot=new Paragraph();
            parabot.setAlignment(Element.ALIGN_RIGHT);
            parabot.add("Authorised Signatory \n\n\n\n");
            document.add(parabot);
            parabot=new Paragraph();
            parabot.setAlignment(Element.ALIGN_CENTER);
            //  parabot.add("-----------------------------------------------------------------\n");
            // parabot.add("*****************************************************************\n");
            //parabot.add("-----------------------------------------------------------------\n\n\n\n");
            parabot.add("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            document.add(parabot);

        }
        catch (Exception ex){Log.e("Receipt Export Error",""+ex.getMessage());}
        // Toast.makeText(getApplicationContext(),"File Exported Sucessfully",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected Dialog onCreateDialog(int id)
    {
        try {
            if (id == DATE_PICKER) {

                return new DatePickerDialog(this, dpickerlistener, year_x, month_x, day_x);

            }

            return null;
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Error in show Dialog 1"+ex.getMessage(),Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    protected DatePickerDialog.OnDateSetListener dpickerlistener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            try
            {
                //  //Log.i("Date","Only");
                String strmon="",strday="";
                Model model=Model.getInstance();
                year_x = year;
                month_x = monthOfYear + 1;
                day_x = dayOfMonth;
                if(month_x < 10){

                    strmon = "0" + month_x;
                }
                else
                {
                    strmon = "" + month_x;
                }
                if(day_x < 10){

                    strday  = "" + day_x ;
                }
                else
                {
                    strday = "" + day_x;
                }
                model.setTodaysDate(""+strday+"/"+strmon+"/"+year_x);
                    loadReceiptDetailListItems();

                ////Log.i("Date",""+day_x + "/" + month_x + "/" + year_x);

            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(),"Error in show date"+ex.getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    };
}