package r3.aurangabad.rthreetrade.ui.serversync;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Vector;

import r3.aurangabad.rthreetrade.Database;
import r3.aurangabad.rthreetrade.MainActivity;
import r3.aurangabad.rthreetrade.Model;
import r3.aurangabad.rthreetrade.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServerSyncFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServerSyncFragment extends Fragment implements View.OnClickListener  {
    private static final int REQUEST_READ_PHONE_STATE = 1;
    Button btnupload,btndownload,btndeletedata;
    public int reloadvalue=0;
    Database mydb;
    Model model;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServerSyncFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServerSyncFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServerSyncFragment newInstance(String param1, String param2) {
        ServerSyncFragment fragment = new ServerSyncFragment();
        try {
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
        }
        catch (Exception ex){
            Log.e("Sync Error",""+ex.getLocalizedMessage());
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_sync,
                container, false);
        btnupload = (Button) view.findViewById(R.id.btn_uploadtoserver);
        btndownload = (Button) view.findViewById(R.id.btn_downloadfromserver);
        btndeletedata = (Button) view.findViewById(R.id.btn_deleteentries);
        model=Model.getInstance();
        mydb=new Database(getContext());
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Upload",Toast.LENGTH_SHORT).show();
            }
        });
        //download Data
        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              new GetLogin().execute("");
              new GetCustomer().execute("");
              new GetProduct().execute("");
                //**new GetLedgersData().execute("");
              new GetOutstanding().execute("");
              new GetRebate().execute("");
              new GetSalesmanCode().execute("");
              new GetReceiptSequence().execute("");
              new GetSchemeMaster().execute("");
              new GetCompanyDetails().execute("");
            }
        });

       return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_server_sync, container, false);
    }

    @Override
    public void onClick(View v) {
      /*  int id=v.getId();
        Log.i("ID",""+id);
        if(id==R.id.btn_uploadtoserver)
        {
            Toast.makeText(getContext(),"Upload 2",Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.btn_downloadfromserver)
        {
            Toast.makeText(getContext(),"Download 2",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getContext(),""+id,Toast.LENGTH_SHORT).show();
        }*/
    }

    private class GetLogin extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject=null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                //nameValuePairs.add(new BasicNameValuePair("username", username));
                try {
                    Model model=Model.getInstance();
                    Log.i("URL",""+model.getUrl_address());
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectLogin.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                // Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    mydb.deleteLogin();
                    String tot = "";
                    long kk = 0;
                    Vector login;
                    JSONObject json_data = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        json_data = jsonArray.getJSONObject(i);
                        login = new Vector();
                        login.addElement(json_data.getString("ID"));
                        login.addElement(json_data.getString("NAME"));
                        login.addElement(json_data.getString("password"));
                        login.addElement(json_data.getString("status"));
                        login.addElement(json_data.getString("serial"));
                        if (mydb.addLoginDetails(login)) {

                        }


                    }

                    reloadvalue ++;
                    Log.i("Download ","Login Data");
                    reloadApplication();

                } catch (Exception e) {
                    Log.e("Login Data Error ",""+e.getMessage());
                    reloadvalue ++;
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    private class GetCustomer extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating Customers ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject = null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                //nameValuePairs.add(new BasicNameValuePair("username", username));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectCustomer.php");
                    Log.i("URL",""+httppost.getURI());
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                        // Log.i("Line = ",""+line);
                        // Log.i("SB = ",""+sb);
                    }
                    is.close();
                    result = sb.toString();
                    //  Log.i("Customer Details", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                // Log.i("Result :", result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray = new JSONArray(result.trim());
                    mydb.deleteCustomer();
                    String tot = "";
                    long kk = 0;
                    Vector customer=new Vector();
                    JSONObject json_data = null;
                    if (mydb.addCustomerDetails(jsonArray)) {
                    }
                   /* for (int i = 0; i < jsonArray.length(); i++) {
                        json_data = jsonArray.getJSONObject(i);
                        Vector cust = new Vector();
                        cust.addElement(json_data.getString("ID"));
                        cust.addElement(json_data.getString("salesman"));
                        cust.addElement(json_data.getString("name"));
                        cust.addElement(json_data.getString("plevel"));
                        cust.addElement(json_data.getString("opening_balance"));
                        cust.addElement(json_data.getString("closing_balance"));
                        cust.addElement(json_data.getString("frt"));
                        cust.addElement(json_data.getString("serial"));
                        customer.addElement(cust);
                       if (mydb.addCustomer(cust)) {
                        }
                    }*/
                    /*if(mydb.addCustomerAll(customer))*/
                    reloadvalue ++;
                    Log.i("Download","Customer");
                    reloadApplication();
                } catch (Exception e) {
                    Log.e("Error in Customer ",""+e.getMessage());
                    reloadvalue ++;
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    private class GetProduct extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating Products ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject=null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                //nameValuePairs.add(new BasicNameValuePair("username", username));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectProduct.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                //Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    mydb.deleteProduct();
                    String tot = "";
                    long kk = 0;
                    Vector login;
                    JSONObject json_data = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        json_data = jsonArray.getJSONObject(i);
                        Vector product = new Vector();
                        product.addElement(json_data.getString("ID"));
                        product.addElement(json_data.getString("Ratea"));
                        product.addElement(json_data.getString("Rateb"));
                        product.addElement(json_data.getString("name"));
                        product.addElement(json_data.getString("Catagiry"));
                        product.addElement(json_data.getString("Sequence"));
                        product.addElement(json_data.getString("Priority"));
                        product.addElement(json_data.getString("Brand"));
                        product.addElement(json_data.getString("price_level"));
                        //   Log.e("Product",""+product.toString());
                        if (mydb.addProduct(product)) {

                        }
                    }
                    reloadvalue ++;
                    Log.i("Download","Product");
                    reloadApplication();


                } catch (Exception e) {
                    Log.e("Product Data Error ",""+e.getMessage());
                    reloadvalue ++;
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    private class GetOutstanding extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating Outstanding");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject=null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                //nameValuePairs.add(new BasicNameValuePair("username", username));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectOuts.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                // Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    mydb.deleteOutstanding();
                    String tot = "";
                    long kk = 0;
                    Vector outstanding=new Vector();
                    JSONObject json_data = null;
                    if (mydb.addOutstandingDetails(jsonArray)) {
                    }
                    /*for (int i = 0; i < jsonArray.length(); i++) {
                        json_data = jsonArray.getJSONObject(i);
                        Vector outs = new Vector();
                        outs.addElement(json_data.getString("billno"));
                        outs.addElement(json_data.getString("billdate"));
                        outs.addElement(json_data.getString("party"));
                        outs.addElement(json_data.getString("amount"));
                        outs.addElement(json_data.getString("salesman"));
                        outs.addElement(json_data.getString("opening"));
                        outs.addElement(json_data.getString("onaccount"));
                        if (mydb.addOutstanding(outs)) {
                        }

                    }*/
                   /* if(mydb.addOutstandingAll(outstanding))
                    {

                    }*/
                    reloadvalue ++;
                    Log.i("Download","Outstanding");
                    reloadApplication();
                    //showNotification();
                    // reloadApp();

                } catch (Exception e) {
                    Log.e("Outstanding Data Error ",""+e.getMessage());
                    reloadvalue ++;
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    private class GetRebate extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject=null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                //nameValuePairs.add(new BasicNameValuePair("username", username));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectRebet.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                //  Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    mydb.deleteRebate();
                    String tot = "";
                    long kk = 0;
                    Vector login;
                    JSONObject json_data = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        json_data = jsonArray.getJSONObject(i);
                        Vector rebate = new Vector();
                        rebate.addElement(json_data.getString("ID"));
                        rebate.addElement(json_data.getString("Party"));
                        rebate.addElement(json_data.getString("Brand_Name"));
                        rebate.addElement(json_data.getString("Cur_Date"));
                        rebate.addElement(json_data.getString("Cases"));
                        rebate.addElement(json_data.getString("Bottle"));
                        rebate.addElement(json_data.getString("Rebate"));
                        rebate.addElement(json_data.getString("Company"));
                        if (mydb.addRebate(rebate)) {
                        }

                    }
                    reloadvalue ++;
                    Log.i("Download","Rebate");
                    reloadApplication();
                } catch (Exception e) {
                    Log.e("Rebate Data Error ",""+e.getMessage());
                    reloadvalue ++;
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    private class GetSalesmanCode extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating Code");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject=null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE);
                //String imeino=getImeiNumber();
                model=Model.getInstance();
                String imeino=model.getTallyName();
                nameValuePairs.add(new BasicNameValuePair("IMEI", imeino));
                Log.i("IMRI NO-",""+imeino);
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectSalesmanCode.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                // Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    mydb.deleteSalesmanCode();
                    if (mydb.addSalesmanCode(jsonArray)) {
                    }
                    /*for (int i = 0; i < jsonArray.length(); i++) {
                        json_data = jsonArray.getJSONObject(i);
                        Vector outs = new Vector();
                        outs.addElement(json_data.getString("billno"));
                        outs.addElement(json_data.getString("billdate"));
                        outs.addElement(json_data.getString("party"));
                        outs.addElement(json_data.getString("amount"));
                        outs.addElement(json_data.getString("salesman"));
                        outs.addElement(json_data.getString("opening"));
                        outs.addElement(json_data.getString("onaccount"));
                        if (mydb.addOutstanding(outs)) {
                        }

                    }*/
                   /* if(mydb.addOutstandingAll(outstanding))
                    {

                    }*/
                    reloadvalue ++;
                    Log.i("Download","Salesman Code");
                    reloadApplication();
                    //showNotification();
                    // reloadApp();

                } catch (Exception e) {
                    reloadvalue ++;
                    Log.e("Sales Code Error ",""+e.getMessage());
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    private class GetReceiptSequence extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating Data");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject=null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
                int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE);
                String imeino=getImeiNumber();
                nameValuePairs.add(new BasicNameValuePair("IMEI", imeino));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectReceiptSequence.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    //Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                // Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    // mydb.deleteSalesmanCode();
                    String tot = "";
                    long kk = 0;
                    Vector outstanding=new Vector();
                    JSONObject json_data = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        json_data = jsonArray.getJSONObject(i);
                        Vector outs = new Vector();
                        outs.addElement(json_data.getString("ID"));
                        outs.addElement(json_data.getString("Salesman_Name"));
                        outs.addElement(json_data.getString("Rec_Date"));
                        outs.addElement(json_data.getString("Sequence"));
                        String seq=mydb.getRecSequenceByDate(json_data.getString("Rec_Date"));
                        try
                        {
                            if(!seq.equals("1"))
                            {
                                int seq1=Integer.parseInt(seq);
                                int seq2=Integer.parseInt(json_data.getString("Sequence"));
                                if(seq1>seq2)
                                {
                                    continue;
                                }
                                else
                                {
                                    if (mydb.addAllReceiptSequence(outs))
                                    {
                                    }
                                }
                            }
                            else
                            {
                                if (mydb.addAllReceiptSequence(outs))
                                {
                                }
                            }
                        }
                        catch (Exception ex){}


                    }
                   /* if(mydb.addOutstandingAll(outstanding))
                    {


                    }*/
                    reloadvalue ++;
                    Log.i("Download","Rec Seq");
                    reloadApplication();
                    //showNotification();
                    // reloadApp();

                } catch (Exception e) {
                    Log.e("Rec Seq Error ",""+e.getMessage());
                    reloadvalue ++;
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    private class GetSchemeMaster extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating Data");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject=null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectSchemeDet.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                // Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    mydb.deleteSchemeMaster();
                    if (mydb.addSchemeMaster(jsonArray)) {
                    }
                    reloadvalue ++;
                    Log.i("Download","Scm Master");
                    reloadApplication();
                    //showNotification();
                    // reloadApp();

                } catch (Exception e) {
                    Log.e("Scm Master Error ",""+e.getMessage());
                    reloadvalue ++;
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    private class GetCompanyDetails extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(getContext());


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Updating");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                JSONObject jsonObject=null;
                if (Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                //nameValuePairs.add(new BasicNameValuePair("username", username));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(model.getUrl_address() + "selectCompanyDetails.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    // Log.i("Current Order", "" + result);

                    // Toast.makeText(getApplicationContext(), "Data is "+result,
                    // Toast.LENGTH_LONG).show();

                    // Log.e("pass 2", "connection success ");
                } catch (Exception e) {
                    Log.e("Fail 2", e.toString());

                }

                //return result;


                //     String result = new JsonBilder().addUser(name, surname, username, dob, age, email, phone, mobile, gen, education, city, state, country, pass);

            } catch (Exception e) {
                Error = e.getMessage();
                cancel(true);
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.

            // Close progress dialog
            Dialog.dismiss();

            if (Error != null) {

                //  uiUpdate.setText("Output : "+Error);

            } else {
                //    Toast.makeText(Login.this, "" + result, Toast.LENGTH_SHORT).show();
                // Log.i("Result :",result);
                // if (result.trim().equals("success")) {
                // Intent i = new Intent(getApplicationContext(), Home.class);
                // startActivity(i);
                Vector company=new Vector();
                try {
                    JSONArray jsonArray=new JSONArray(result.trim());
                    mydb.deleteCompanyDetails();

                    JSONObject json_data = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        json_data = jsonArray.getJSONObject(i);
                        company = new Vector();
                        company.addElement(json_data.getString("ID"));
                        company.addElement(json_data.getString("Company_Name1"));
                        company.addElement(json_data.getString("Company_Name2"));
                        company.addElement(json_data.getString("Company_Address"));
                        company.addElement(json_data.getString("Stamp_Duty"));
                        company.addElement(json_data.getString("Stamp_Value"));
                        company.addElement(json_data.getString("Prefix"));
                        company.addElement(json_data.getString("Is_BT_Print"));
                        company.addElement(json_data.getString("Is_SMS_Send"));

                        if (mydb.addCompanyDetails(company)) {

                        }


                    }

                    reloadvalue ++;
                    Log.i("Download ","Company Data "+company.toString());
                    reloadApplication();

                } catch (Exception e) {
                    Log.e("Company Data Error ",""+e.getMessage());
                    reloadvalue ++;
                    reloadApplication();
                    //Toast.makeText(getApplicationContext(),"Added Sucessfully !",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }
    public void reloadApplication()
    {
        try
        {
            Log.i("Load Value",""+reloadvalue);
            if(reloadvalue==9)
            {
                Log.i("Load ","Complete");
                //showNotification();
                reloadApp();
            }
        }
        catch (Exception ex){}
    }
    public void reloadApp() {
        mydb = new Database(getContext());
        Vector userlogin = mydb.getUserLogin();
        Intent i = new Intent(getContext(), MainActivity.class);
        i.putExtra("UserName", "" + userlogin.elementAt(0));
        i.putExtra("TallyName", "" + userlogin.elementAt(6));
        //   Toast.makeText(getApplicationContext(), "Download Sucess . .!", Toast.LENGTH_SHORT).show();
        startActivity(i);
    }
    public String getImeiNumber() {

        String deviceid = "";
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                Log.i("7", "Permitted 1");
            }
            TelephonyManager mngr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return "" + Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
            if (android.os.Build.VERSION.RELEASE.startsWith("10")) {
                deviceid = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("1 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            } else {
                deviceid = mngr.getDeviceId().toString().trim();
                Log.i("2 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            }
            if (android.os.Build.VERSION.SDK_INT > 28) {
                deviceid = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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

}