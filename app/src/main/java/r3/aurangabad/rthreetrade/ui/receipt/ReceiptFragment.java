package r3.aurangabad.rthreetrade.ui.receipt;

import static com.itextpdf.text.BaseColor.BLACK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import r3.aurangabad.rthreetrade.BuildConfig;
import r3.aurangabad.rthreetrade.Database;
import r3.aurangabad.rthreetrade.EnglishNumberToWords;
import r3.aurangabad.rthreetrade.Model;
import r3.aurangabad.rthreetrade.R;
import r3.aurangabad.rthreetrade.SearchCustomer;
import r3.aurangabad.rthreetrade.UnicodeFragment;

public class ReceiptFragment extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private static final int REQUEST_READ_PHONE_STATE = 1;
    EditText searchParty,receiptNumber,receiptAmount,receiptDate;
    TextView receiptCount,txtreciptAmount;
    Button btnsubmit;
    Model model=Model.getInstance();
    Database mydb;
    Vector company,pdfdetails,customerdet;
    String recdate="",recSequence="",invnumber="",recdetdate="",currentdate="";
    int rec_count=1;
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
                            Toast.makeText(getContext(),"Printer is Connected",Toast.LENGTH_SHORT).show();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.receipt_fragment,
                container, false);
        setHasOptionsMenu(true);
        mydb=new Database(getContext());
        btnsubmit = (Button) view.findViewById(R.id.btn_submit);
        searchParty=(EditText)view.findViewById(R.id.et_searchCustomer);
        receiptDate=(EditText)view.findViewById(R.id.et_curdate);
        receiptAmount=(EditText)view.findViewById(R.id.et_receiptAmount);
        receiptNumber=(EditText)view.findViewById(R.id.et_receiptNumber);
        receiptCount=(TextView)view.findViewById(R.id.txt_receiptCount);
        txtreciptAmount=(TextView)view.findViewById(R.id.txt_receiptAmount);
        company=new Vector();
        company=mydb.getCompanyDetails();
        loadCurDateRecNumber();
        //For Open Bluetooth
        if(company.size()>0)
        {
        if(company.elementAt(7).toString().equals("YES")) {
            Log.i("Print Config","YES");
            fragMgr = getActivity().getFragmentManager();

            // lockOrientation(this);

            mSp = PreferenceManager.getDefaultSharedPreferences(getContext());
            try {
                mBtp.initService(getContext(), mHandler);
            } catch (Exception e) {
                Log.i("Error Printre",""+e.getLocalizedMessage());
                e.printStackTrace();
            }
            nm = new UnicodeFragment();
            Log.i("NM is",""+nm.toString());
        }}

        searchParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rec_count=1;
                Intent intent = new Intent(getActivity(), SearchCustomer.class);
                someActivityResultLauncher.launch(intent);
            }
        });
        btnsubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               if(!receiptAmount.getText().toString().toString().equals("")) {
                   if(! searchParty.getText().toString().trim().equals("")) {


                       saveReceiptAll();
                       saveReceipt();
                       Toast.makeText(getContext(),"Receipt Saved Sucessfully !",Toast.LENGTH_SHORT).show();
                       loadCurDateRecNumber();
                   }
                   else
                   {
                       Toast.makeText(getContext(),"Please Select Customer Name !",Toast.LENGTH_SHORT).show();
                   }
               }
               else{
                   Toast.makeText(getContext(),"Please Enter Receipt Amount !",Toast.LENGTH_SHORT).show();
               }
            }
        });
        receiptAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtreciptAmount.setText(""+receiptAmount.getText());
                setReceiptCount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
        //return inflater.inflate(R.layout.receipt_fragment, container, false);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    try {
                        Intent data = result.getData();
                        setValueForEditText(data.getStringExtra("SelectedCustomer"));
                        model = Model.getInstance();
                        model.setPartyName("" + data.getStringExtra("SelectedCustomer"));
                        model.setTodaysDate(recdate);
                        checkReceiptExistToday();
                        receiptDate.setText(recdate);
                        loadRecNumber(recdate);
                        customerdet = mydb.getCustomerDetails(data.getStringExtra("SelectedCustomer"));
                        txtreciptAmount.setText("");
                    }
                    catch (Exception ex){Log.e("Error Act Result",""+ex.getLocalizedMessage());}
                }
            });
    //  @Override
  //  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
   //     super.onActivityCreated(savedInstanceState);
   //     // TODO: Use the ViewModel
   // }
public void setValueForEditText(String custname)
{
    searchParty.setText(custname.toString());
}
  private  void setReceiptCount()
  {
      try{
          double recamount=Double.parseDouble(receiptAmount.getText().toString());
          double total=0;
          total=recamount%10000;
          if(total==0)
          {
              receiptCount.setText(""+(int)recamount/10000);
          }
          else
          {
              total=(int)recamount/10000;
              receiptCount.setText(""+(int)(total+1));
          }

      }
      catch (Exception ex){
          Log.e("Rec Count Err",""+ex.getLocalizedMessage());
      }
  }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_date:
                //showDialog(DILOG_ID);
                break;
            case R.id.action_clear_device:

                // deletes the last used printer, will avoid auto connect
                AlertDialog.Builder d = new AlertDialog.Builder(getContext());
                d.setTitle("NGX Bluetooth Printer");
                // d.setIcon(R.drawable.ic_launcher);
                d.setMessage("Are you sure you want to delete your preferred Bluetooth printer ?");
                d.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBtp.clearPreferredPrinter();
                                Toast.makeText(getContext(),
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

                            mBtp.showDeviceList(getActivity());
                        }}
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_unpair_device:
                AlertDialog.Builder u = new AlertDialog.Builder(getContext());
                u.setTitle("Bluetooth Printer unpair");
                // d.setIcon(R.drawable.ic_launcher);
                u.setMessage("Are you sure you want to unpair all Bluetooth printers ?");
                u.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (mBtp.unPairBluetoothPrinters()) {
                                    Toast.makeText(
                                            getContext(),
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
    }*/
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
     //  Toast.makeText(getContext(),"CLicked ", Toast.LENGTH_SHORT).show();
       switch (item.getItemId()) {
           case R.id.action_clear_device:

               // deletes the last used printer, will avoid auto connect
               AlertDialog.Builder d = new AlertDialog.Builder(getContext());
               d.setTitle("NGX Bluetooth Printer");
               // d.setIcon(R.drawable.ic_launcher);
               d.setMessage("Are you sure you want to delete your preferred Bluetooth printer ?");
               d.setPositiveButton(android.R.string.yes,
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               mBtp.clearPreferredPrinter();
                               Toast.makeText(getContext(),
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

                           mBtp.showDeviceList(getActivity());
                       }}
               } catch (Exception e) {
                   e.printStackTrace();
               }
               return true;
           case R.id.action_unpair_device:
               AlertDialog.Builder u = new AlertDialog.Builder(getContext());
               u.setTitle("Bluetooth Printer unpair");
               // d.setIcon(R.drawable.ic_launcher);
               u.setMessage("Are you sure you want to unpair all Bluetooth printers ?");
               u.setPositiveButton(android.R.string.yes,
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               if (mBtp.unPairBluetoothPrinters()) {
                                   Toast.makeText(
                                           getContext(),
                                           "All NGX Bluetooth printer(s) unpaired",
                                           Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
       }
       return super.onOptionsItemSelected(item);
   }

    public void loadRecNumber(String df)
    {
        try
        {
            mydb=new Database(getContext());
            model=Model.getInstance();
            String recdt="";
            recSequence=mydb.getRecSequenceByDate(""+df);
            String reccode=mydb.getSalesmanCode();
            recdt=df;
            recdt=recdt.replace("/","");
            receiptNumber.setText(""+company.elementAt(6).toString()+"/"+reccode+"/"+recdt+"/"+recSequence);
            invnumber=company.elementAt(6).toString()+"/"+reccode+"/"+recdt+"/"+recSequence;
        }
        catch (Exception ex){Log.e("Error  in recno",""+ex.getLocalizedMessage());}
    }
    public void loadCurDateRecNumber()
    {
        try
        {
            model=Model.getInstance();
            mydb=new Database(getContext());
            SimpleDateFormat Ins_Date=new SimpleDateFormat("d/MM/yyyy");
            recdate=Ins_Date.format(Calendar.getInstance().getTime());
            currentdate=recdate;
            receiptDate.setText(recdate);
            model.setTodaysDate(recdate);
            String recdt="";
            recSequence=mydb.getRecSequenceByDate(""+recdate);
            String reccode=mydb.getSalesmanCode();
            recdt=recdate;
            recdt=recdt.replace("/","");
            receiptNumber.setText(""+company.elementAt(6).toString()+"/"+reccode+"/"+recdt+"/"+recSequence);
            invnumber=company.elementAt(6).toString()+"/"+reccode+"/"+recdt+"/"+recSequence;
        }
        catch (Exception ex){Log.e("Error in recno",""+ex.getLocalizedMessage());}
    }
    public String getNextDate()
    {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, -(rec_count));
        Date tomorrow = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy");

        String todayAsString = dateFormat.format(today);
        String tomorrowAsString = dateFormat.format(tomorrow);
        model.setTodaysDate(tomorrowAsString);
        recdate=tomorrowAsString;
        return tomorrowAsString;
    }
    public void checkReceiptExistToday()
    {
        boolean recexist=true;
        while (recexist)
        {
            Vector chrec=mydb.getPartyReceipt();
             if(chrec.size()>0)
            {
                recdate=getNextDate();
                rec_count++;
                continue;
            }
            recexist=false;

        }
       // loadRecNumber(recdate);
    }
    public void saveReceiptAll()
    {
        try{
            Vector rec = new Vector();
            recdetdate=recdate;
            rec.addElement("" + invnumber);
            rec.addElement("" + recdate);
            rec.addElement("" + searchParty.getText());
            rec.addElement("" + model.getSalesman());
            rec.addElement("" + receiptAmount.getText().toString());
            rec.addElement("Cash");
            rec.addElement("No");
            rec.addElement("");
            rec.addElement("");
            if (mydb.addReceiptAll(rec)) {
            } else {
                Log.d("Rec All ", "Not Saved Properlly");
            }
        }
        catch (Exception ex){Log.e("Error in Rec All",""+ex.getLocalizedMessage());}
    }
   public void saveReceipt()
   {
       try{
           Vector pdfdet=new Vector();
           pdfdetails=new Vector();
           double recAmount=Double.parseDouble(""+receiptAmount.getText());
           while (recAmount>10000) {
              // Log.i("Date "+rec_count,""+model.getTodaysDate());
               model.setPartyName(searchParty.getText().toString());
               checkReceiptExistToday();
               Vector rec = new Vector();
               rec.addElement("" + invnumber);
               rec.addElement("" + recdate);
               rec.addElement("" + searchParty.getText());
               rec.addElement("" + model.getSalesman());
               rec.addElement("10000");
               rec.addElement("Cash");
               rec.addElement("No");
               rec.addElement(currentdate);
               rec.addElement("");
               rec.addElement("");
               Log.i("Receipt",""+rec.toString());
               if (mydb.addReceipt(rec)) {
                   Vector recdet = new Vector();
                   recdet.addElement(searchParty.getText());
                   recdet.addElement(recdetdate);
                   recdet.addElement(invnumber);
                   recdet.addElement(model.getSalesman());
                   mydb.addReceiptDetails(recdet);
               }
               mydb.deleteRecSequence(recdate);
               mydb.addReceiptSequence(Integer.parseInt(recSequence), recdate);
               pdfdet=new Vector();
               pdfdet.addElement(invnumber);
               pdfdet.addElement(customerdet.elementAt(8) + "  " + customerdet.elementAt(9));
               pdfdet.addElement(model.getSalesman());
               pdfdet.addElement("" + model.getPartyName());
               pdfdet.addElement(10000);
               pdfdet.addElement("Cash");
               pdfdet.addElement("");
               pdfdet.addElement("");
               pdfdetails.addElement(pdfdet);
               recAmount=recAmount-10000;
               recdate = getNextDate();
               rec_count++;
               loadRecNumber(recdate);
           }
           checkReceiptExistToday();
           Vector rec = new Vector();
           rec.addElement("" + invnumber);
           rec.addElement("" + recdate);
           rec.addElement("" + searchParty.getText());
           rec.addElement("" + model.getSalesman());
           rec.addElement(""+recAmount);
           rec.addElement("Cash");
           rec.addElement("No");
           rec.addElement(currentdate);
           rec.addElement("");
           rec.addElement("");
           Log.i("Receipt",""+rec.toString());
           if (mydb.addReceipt(rec)) {
               Vector recdet = new Vector();
               recdet.addElement(searchParty.getText());
               recdet.addElement(recdetdate);
               recdet.addElement(invnumber);
               recdet.addElement(model.getSalesman());
               mydb.addReceiptDetails(recdet);
           }
           mydb.deleteRecSequence(recdate);
           mydb.addReceiptSequence(Integer.parseInt(recSequence), recdate);
           rec_count=1;
           pdfdet=new Vector();
           pdfdet.addElement(invnumber);
           pdfdet.addElement(customerdet.elementAt(8) + "  " + customerdet.elementAt(9));
           pdfdet.addElement(model.getSalesman());
           pdfdet.addElement("" + model.getPartyName());
           pdfdet.addElement((int)recAmount);
           pdfdet.addElement("Cash");
           pdfdet.addElement("");
           pdfdet.addElement("");
           pdfdetails.addElement(pdfdet);
           if(company.elementAt(7).toString().equals("YES"))
           {
               for (int i=0;i<pdfdetails.size();i++)
               {
                   printEnglishBill(i);
               }
           }
           createPDFInvoice("");

       }
       catch (Exception ex){
           Log.e("Error in Rec Save",""+ex.getLocalizedMessage());
       }
   }
    public void createPDFInvoice(String str)
    {
        Document doc = new Document();
        // btnconfirm.setEnabled(true);


        try {

            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_READ_PHONE_STATE);
            }
            permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE);
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
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_person);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 , stream);
            Image myImg = Image.getInstance(stream.toByteArray());
            myImg.setAlignment(Image.MIDDLE);

            //add image to document
            // doc.add(myImg);
            Log.i("Comming Hear","YES "+ Build.VERSION.SDK_INT);
            for(int i=0;i<pdfdetails.size();i++) {
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
            Log.i("Version ","30");
            if (Build.VERSION.SDK_INT >= 30)
            {
               // uri=Uri.parse(file.getPath());
               uri=FileProvider.getUriForFile(getContext(),BuildConfig.APPLICATION_ID + ".provider", file);
              }
            else
            {
               uri= Uri.fromFile(file);
            }
           // Log.i("URI",""+uri.toString());

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
        Vector invdata=(Vector)pdfdetails.elementAt(position);
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
    private void printEnglishBill(int position) {

        Vector printdet=(Vector) pdfdetails.elementAt(position);
        try {
            if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
                Toast.makeText(getContext(), "Printer is not connected", Toast.LENGTH_SHORT).show();
                isprinterconnected=false;
                return;
            }
            isprinterconnected=true;
            mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_48MM);
            mSp.edit().putInt("PRINTER_SELECTION", 2).commit();
            String separator = "-----------------------------";
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/DroidSansMono.ttf");

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
            stringBuilder.append("In Words : "+EnglishNumberToWords.convert(Integer.parseInt(printdet.elementAt(4).toString()))+" Rupees only");
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}