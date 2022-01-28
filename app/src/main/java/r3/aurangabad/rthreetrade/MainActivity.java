package r3.aurangabad.rthreetrade;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    ImageButton imgflash;
    static final int REQUEST_CODE = 123;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    Animation animation = null;
    Model model;
    TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgflash=(ImageButton) findViewById(R.id.img_flash);
        animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade);
        version=(TextView)findViewById(R.id.txt_version);
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            version.setText("Version : "+pInfo.versionName+"   ");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Model model = Model.getInstance();
        getAllPermissions();
        this.animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                checkDatabase();
                finish();
                Intent i=new Intent(getApplicationContext(), LoginRequest.class);
                startActivity(i);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

    }
    private void getAllPermissions() {
        try {
            boolean value=false;
            if (Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                // value=true;
            }

             int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);
            int permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET}, REQUEST_READ_PHONE_STATE);
                Log.i("1", "Phone State");

                // value=true;
            }
            permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE);
                Log.i("1", "Phone State");

                // value=true;
            }
            permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE);
                Log.i("1", "Phone State");

                // value=true;
            }
            permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_READ_PHONE_STATE);
                Log.i("1", "Phone State");

                // value=true;
            }

          /*  permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL);
                Log.i("1", "Storage");

                // value=true;
            }
            permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                Log.i("1", "Storage");

                // value=true;
            }*/
       /*   permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_READ_PHONE_STATE);
                Log.i("1", "Internet");

                // value=true;
            }*/
      /*      permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_READ_PHONE_STATE);
                Log.i("1", "Location");
            //  imgflash.startAnimation(animation);

                // value=true;
            }

            permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
                Log.i("1", "Location");
                //  imgflash.startAnimation(animation);

                // value=true;
            }*/

            //TODO
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                               imgflash.startAnimation(animation);
                   }else{Log.i("Permission *4"," WriteExternal");}
            }else{Log.i("Permission *5"," Read PhoneState");}
        } catch (Exception e) {
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("Enter In","Permission "+grantResults.length);
        if (requestCode != 123) {
            return;
        }
        if (grantResults.length >= 2) {
            imgflash.startAnimation(this.animation);
        } else {
           getAllPermissions();
        }
    }
    private void checkDatabase()
    {
        try
        {
            SQLiteDatabase sampledb=this.openOrCreateDatabase("main",MODE_PRIVATE,null);
            Log.i("Path Of DB",""+sampledb.getPath());
            Database db=new Database(getApplicationContext());
            db.createTables(sampledb);
            //checkDate();
        }
        catch(Exception ex)
        {
            Log.e("Exception is",""+ex.getMessage());
        }}
}
