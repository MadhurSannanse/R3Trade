package r3.aurangabad.rthreetrade;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import r3.aurangabad.rthreetrade.databinding.ActivityLoginRequestBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginRequest extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityLoginRequestBinding binding;
    EditText txtusername, txtcontact;
    ImageButton btnlogin;
    String yourname, contactnumber, tallyname;
    Vector details, login;
    Database mydb;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int INTERNET = 1;
    final Model model=Model.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        txtusername = (EditText) findViewById(R.id.et_username);
        txtcontact = (EditText) findViewById(R.id.et_contactnumber);
        btnlogin = (ImageButton) findViewById(R.id.btn_login);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_login_request);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            mydb = new Database(getApplicationContext());
            Vector userlogin = new Vector();
            try {
                userlogin = mydb.getUserLogin();
                Log.i("User", "" + userlogin.toString());
            } catch (Exception ex) {
                Log.i("Error", "" + ex.getLocalizedMessage());
            }
            if (userlogin.size() == 0) {

                Log.i("1", "New User");
                //serverRequest(new View(this));
                if (isNetworkAvailable()) {
                    Log.i("2", "Network Available");

                    int permissionCheck = ContextCompat.checkSelfPermission(LoginRequest.this, Manifest.permission.READ_PHONE_STATE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                        Log.i("3", "Permited 1");
                        downloadRetrofitValidUser();
                        //new LoadValidUser().execute("User");
                    }
                    {
                        ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                        Log.i("4", "Permited 2");
                        downloadRetrofitValidUser();
                      //  new LoadValidUser().execute("User");
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Available !", Toast.LENGTH_SHORT).show();
                }

            } else {
                String usertype = "" + userlogin.elementAt(2).toString().trim();
                if (usertype.equals("Customer")) {
                    Toast.makeText(getApplicationContext(), "Logined As Customer Welcome " + userlogin.elementAt(0), Toast.LENGTH_SHORT).show();
                } else {
                    if (userlogin.elementAt(6).equals("NO")) {
                        Toast.makeText(getApplicationContext(), "Login Confirmation Is Pending ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Logined Validation Sucess Welcome " + userlogin.elementAt(0), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), MenuNavigation.class);
                        model.setUrl_address("http://www.r3infoservices.com/Offline/"+userlogin.elementAt(7)+"/");
                        model.setUserType(""+userlogin.elementAt(2));
                        model.setTallyName(""+userlogin.elementAt(6));
                        model.setSalesman("Admin");
                      //  Toast.makeText(getApplicationContext(),"Tally name : : "+userlogin.elementAt(6),Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(i);
                    }
                }
            }


            // mydb.checkDatabase();
            details = new Vector();
            btnlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!txtusername.getText().toString().trim().equals("")) {

                        if (!txtcontact.getText().toString().trim().equals("") && txtcontact.getText().toString().length() == 10) {
                            //serverSend(view);
                            if (isNetworkAvailable()) {
                                int permissionCheck = ContextCompat.checkSelfPermission(LoginRequest.this, Manifest.permission.READ_PHONE_STATE);
                                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                                  insertRetrofitUser();
                                    //  new InsertUserRequest().execute("Request");
                                } else {
                                    ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                                    insertRetrofitUser();
                                    //  new InsertUserRequest().execute("Request");
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "No Internet Available !", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Contact Number . .!!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Username Required . .!!", Toast.LENGTH_SHORT).show();
                    }
                }

            });

        } catch (Exception ex) {
            Log.e("System Error", "" + ex.getLocalizedMessage());
            Toast.makeText(getApplicationContext(), "Not Process Contact Administration team !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
      //  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_login_request);
       // return NavigationUI.navigateUp(navController, appBarConfiguration)
         //       || super.onSupportNavigateUp();
return true;
    }

    public String getImeiNumber() {

        String deviceid = "";
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(LoginRequest.this, Manifest.permission.READ_PHONE_STATE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                Log.i("7", "Permitted 1");
            }
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return "" + Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
            if (android.os.Build.VERSION.RELEASE.startsWith("10")) {
                deviceid = Settings.Secure.getString(LoginRequest.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i("1 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            } else {
                deviceid = mngr.getDeviceId().toString().trim();
                Log.i("2 IMEI" + Build.VERSION.SDK_INT, "" + deviceid);
            }
            if (android.os.Build.VERSION.SDK_INT > 28) {
                deviceid = Settings.Secure.getString(LoginRequest.this.getContentResolver(), Settings.Secure.ANDROID_ID);
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

    public Vector loadRequestData() {
        try {
            String imeiid = "";
            imeiid = getImeiNumber();
            SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy hh:mm a");
            SimpleDateFormat Ins_Date = new SimpleDateFormat("d/MM/yyyy");
            String date = df.format(Calendar.getInstance().getTime());
            String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
            Vector req = new Vector();
            req.addElement("" + txtusername.getText());
            req.addElement("" + txtcontact.getText());
            req.addElement("Salesman");
            req.addElement("" + imeiid);
            req.addElement("" + InsDate);
            req.addElement("NO");
            ////Log.e("Req",""+req);
            //details=req;
            return req;


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error while getting data " + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private boolean isNetworkAvailable() {
        int permissionCheck = ContextCompat.checkSelfPermission(LoginRequest.this, Manifest.permission.INTERNET);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginRequest.this, new String[]{Manifest.permission.INTERNET}, INTERNET);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();


            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } else {
            //TODO
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                //Log.e("Internet 1","Available");
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();

            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            //Log.e("Internet 3","Available");
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        }

    }

    private class InsertUserRequest extends AsyncTask<String, Void, Void> {

        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(LoginRequest.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait ");
            Dialog.show();
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method
                String request = "";
                Vector req = new Vector();
                req = loadRequestData();

                for (int i = 0; i < req.size(); i++) {
                    if ("" + req.elementAt(i) == "" || "" + req.elementAt(i) == null) {
                        //m.add(i,"0");
                    }
                    request += req.elementAt(i).toString().trim();
                    if (i == req.size() - 1) {
                        // continue;
                    } else {
                        request = request + "~~~";
                    }

                }
                request = request + "^^^";
                Log.e("O Details", "" + request);
                InputStream is = null;

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //name, surname, username, dob, age, email, phone, mobile, education, city, state, country, pass
                nameValuePairs.add(new BasicNameValuePair("details", request));


                //	nameValuePairs.add(new BasicNameValuePair("ma", ma));
                //	nameValuePairs.add(new BasicNameValuePair("pass", pass));
                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://www.r3infoservices.com/Offline/universal/" + "insert_loginmanagement.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    // //Log.e("pass 1", "connection success ");
                } catch (Exception e) {
                    //Log.e("Fail 1", e.toString());
                    // Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    // Toast.LENGTH_LONG).show();
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                } catch (Exception e) {

                }


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
                try {
                    txtusername.setText("");
                    txtcontact.setText("");
                    Toast.makeText(getApplicationContext(), "Added Sucessfully Please Wait For Confirmation!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(i);


                } catch (Exception e) {
                    txtusername.setText("");
                    txtcontact.setText("");
                    Toast.makeText(getApplicationContext(), "Added Sucessfully Please Wait For Confirmation!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(i);
                }

            }
        }
    }

    private class LoadValidUser extends AsyncTask<String, Void, Void> {
        String result = "";
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(LoginRequest.this);


        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //UI Element
            //   uiUpdate.setText("Output : ");
            Dialog.setMessage("Please Wait ");
            Dialog.show();
            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.

                // Server url call by GET method


                InputStream is = null;
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                try {
                    String tot = "";
                    long kk = 0;
                    Vector login;
                    String imeiid = "";
                    // Log.i("6","Getting User Data");
                    if (Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    imeiid = getImeiNumber();
                    if (imeiid.equals("") || imeiid.equals(null) || imeiid.equals("null")) {
                    }
                    Log.i("Device ID = ", "" + imeiid);
                    nameValuePairs.add(new BasicNameValuePair("imei", imeiid));

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://www.r3infoservices.com/Offline/universal/" + "selectloginman.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                }


                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();

                } catch (Exception e) {

                }


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
           } else {
                Log.i("Result :", result);
                try {
                    JSONArray jsonArray = new JSONArray(result.trim());
                    JSONObject jsonObject = null;
                    mydb = new Database(getApplicationContext());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        login = new Vector();
                        //  login.addElement(json_data.getString("ID"));
                        login.addElement(jsonObject.getString("user_name"));
                        login.addElement(jsonObject.getString("contact_no"));
                        login.addElement(jsonObject.getString("user_type"));
                        login.addElement(jsonObject.getString("imei_no"));
                        login.addElement(jsonObject.getString("date"));
                        login.addElement(jsonObject.getString("status"));
                        login.addElement(jsonObject.getString("tally_name"));
                        login.addElement(jsonObject.getString("url_address"));
                        tallyname = "" + jsonObject.getString("tally_name");
                        Log.e("Details ", "" + login);
                        if (!login.elementAt(5).toString().equals("NO")) {
                            if (mydb.addLoginDetailsNew(login)) {
                                finish();
                               Intent intent = new Intent(LoginRequest.this, MenuNavigation.class);
                                model.setUrl_address("http://www.r3infoservices.com/Offline/"+jsonObject.getString("url_address")+"/");
                                model.setUserType(jsonObject.getString("user_type"));
                                model.setSalesman("Admin");
                                model.setTallyName(jsonObject.getString("tally_name"));
                                startActivity(intent);
                            } else {
                                //Log.e("Status", "Not Saved");
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Your Login Process Is Pending Please Wait . .!", Toast.LENGTH_LONG).show();
                        }
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Unregistered User", Toast.LENGTH_LONG).show();
                    Log.e("Error In Login", "" + e.getMessage());
                }

            }
        }

    }
    public void downloadRetrofitValidUser() {
        try {
            final ProgressDialog Dialog = new ProgressDialog(this);
            Dialog.setMessage("Loading User Details");
            Dialog.show();
            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
            }
            Model model = Model.getInstance();
            Log.i("Downloading ", "Valid User");
            //Log.i("URL ", "" + model.getUrl_address());
            model.setUrl_address("http://www.r3infoservices.com/Offline/universal/");
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(model.getUrl_address())
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            RetrofitLoginRequest api=retrofit.create(RetrofitLoginRequest.class);
            Call<List<RetrofitLoginDetails>> call=api.getValidUser(getImeiNumber());
            Log.i("My URL",""+call);
            call.enqueue(new Callback<List<RetrofitLoginDetails>>() {
                @Override
                public void onResponse(Call<List<RetrofitLoginDetails>> call, Response<List<RetrofitLoginDetails>> response) {
                    if (!response.isSuccessful()) {
                        Log.i("Res is", "" + response.code());
                    }
                    Dialog.dismiss();
                    try {
                        for (RetrofitLoginDetails logindet : response.body()) {
                            login = new Vector();
                            //  login.addElement(json_data.getString("ID"));
                            login.addElement(logindet.getUser_name());
                            login.addElement(logindet.getContact_no());
                            login.addElement(logindet.getUser_type());
                            login.addElement(logindet.getImei_no());
                            login.addElement(logindet.getDate());
                            login.addElement(logindet.getStatus());
                            login.addElement(logindet.getTally_name());
                            login.addElement(logindet.getUrl_address());
                            tallyname = "" + logindet.getTally_name();
                            // Toast.makeText(getApplicationContext(),""+jsonObject.getString("user_name")+" - "+jsonObject.getString("url_address"),Toast.LENGTH_LONG).show();
                            Log.e("Details ", "" + login);
                            if (!login.elementAt(5).toString().equals("NO")) {
                                if (mydb.addLoginDetailsNew(login)) {
                                    //Log.e("Status", "User Data Saved");
                                    finish();
                                   // model=Model.getInstance();
                                    model.setUrl_address("http://www.r3infoservices.com/Offline/"+logindet.getUrl_address()+"/");
                                    model.setUserType(logindet.getUser_type());
                                    model.setSalesman("Admin");
                                    model.setTallyName(logindet.getTally_name());
                                    Intent intent = new Intent(LoginRequest.this, MenuNavigation.class);
                                  //  Toast.makeText(getApplicationContext(),"Tally name : "+logindet.getTally_name(),Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                } else {
                                    //Log.e("Status", "Not Saved");
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Your Login Process Is Pending Please Wait . .!", Toast.LENGTH_LONG).show();
                            }
                            // }

                        }
                    }catch (Exception ex){
                        Toast.makeText(getApplicationContext(),"New User Found !",Toast.LENGTH_SHORT).show();
                    }

                    }


                @Override
                public void onFailure(Call<List<RetrofitLoginDetails>> call, Throwable t) {
                    Dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error is " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (Exception ex) {
            Log.e("Retrofit Error ", "" + ex.getLocalizedMessage());
        }
    }
    public void insertRetrofitUser() {
        try {
            final ProgressDialog Dialog = new ProgressDialog(this);
            Dialog.setMessage("Please Wait");
            Dialog.show();
            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
            }
          //  model = Model.getInstance();
            Log.i("Inserting ", "Valid User");
            //Log.i("URL ", "" + model.getUrl_address());
            model.setUrl_address("http://www.r3infoservices.com/Offline/universal/");
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(model.getUrl_address())
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            String imeiid = "";
            imeiid = getImeiNumber();
            SimpleDateFormat df = new SimpleDateFormat("d/MM/yyyy hh:mm a");
            SimpleDateFormat Ins_Date = new SimpleDateFormat("d/MM/yyyy");
            String date = df.format(Calendar.getInstance().getTime());
            String InsDate = Ins_Date.format(Calendar.getInstance().getTime());
            Vector req = new Vector(); String request = "";
            request=""+txtusername.getText().toString()+"~~~"+txtcontact.getText().toString()+"~~~"+"Salesman"+
                    "~~~"+imeiid+"~~~"+InsDate+"~~~"+"NO^^^";
            RetrofitInsertUser api=retrofit.create(RetrofitInsertUser.class);
            Call<List<InsertUserValues>> call=api.insertUser(request);
            Log.i("My URL",""+call);
            call.enqueue(new Callback<List<InsertUserValues>>() {
                @Override
                public void onResponse(Call<List<InsertUserValues>> call, Response<List<InsertUserValues>> response) {
                    if (!response.isSuccessful()) {
                        Log.i("Res is", "" + response.code());
                    }
                    Dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Added Sucessfully Please Wait For Confirmation!", Toast.LENGTH_LONG).show();


                }
      @Override
                public void onFailure(Call<List<InsertUserValues>> call, Throwable t) {
                    Dialog.dismiss();
          Toast.makeText(getApplicationContext(), "Added Sucessfully Please Wait For Confirmation!", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Error is " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        } catch (Exception ex) {
            Log.e("Retrofit Error ", "" + ex.getLocalizedMessage());
        }
    }
}