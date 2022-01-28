package r3.aurangabad.rthreetrade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.math.BigDecimal;

import r3.aurangabad.rthreetrade.databinding.ActivityMenuNavigationBinding;
import r3.aurangabad.rthreetrade.ui.adminoutstanding.AdminOutstandingFragment;

public class MenuNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdminOutstandingFragment.adminOutstanding {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuNavigationBinding binding;
    Model model;
    RecyclerView recyclerViewHome;
    Database mydb;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerViewHome = (RecyclerView) findViewById(R.id.home_recycler);
        model = Model.getInstance();
        mydb = new Database(getApplicationContext());
       // setSupportActionBar(binding.appBarMenuNavigation.toolbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        binding.appBarMenuNavigation.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangeRoute.class);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
            }
        });
        drawer = findViewById(R.id.drawer_layout);
       /* DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
   // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_receipt,R.id.nav_updown,R.id.nav_receiptview,R.id.nav_outstanding,R.id.nav_neworder)
                .setOpenableLayout(drawer)
                .build();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        binding.navView.bringToFront();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); loadListItems(this);
        loadListItems(this);
        Log.i("Server",""+model.getUrl_address());
        model=Model.getInstance();
        mydb=new Database(getApplicationContext());
        if(model.getUserType().equals("Salesman"))
        {
            if(model.getSalesman().equals("Admin"))
            {
                Log.i("Tally Name",""+model.getTallyName());
                Intent intent = new Intent(getApplicationContext(), ChangeRoute.class);
                startActivity(intent);
            }
        }
       /* ArrayList<String> tst = mydb.getCount();
        if (tst.size() == 0) {
            Intent i = new Intent(getApplicationContext(), ServerSyncFragment.class);
            startActivity(i);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
        // return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void loadListItems(MenuNavigation menuNavigation) {
        try {
            model = Model.getInstance();
            Log.i("Calling", "Function");
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.listview_animation);
            recyclerViewHome.setLayoutAnimation(controller);

            String[] values = new String[10];
            String[] lable = new String[10];
            int[] ico = new int[10];

            Log.i("Calling", "Function 1");
            values[0] = " " + getResources().getString(R.string.Rs) + " " + mydb.getTodaysSaleTotal();
            lable[0] = "Sales - Credit Note(Gross)";
            //icons[0]=getResources().getDrawable(R.drawable.ic_sale_bar);
            ico[0] = R.drawable.ic_sale_bar;

            Log.i("Calling", "Function 2");
            values[1] = " " + getResources().getString(R.string.Rs) + " 0";
            lable[1] = "Purchase - Debit Note(Gross)";
            ico[1] = R.drawable.ic_cart;

            Log.i("Calling", "Function 3");
            values[2] = " " + getResources().getString(R.string.Rs) + " " + mydb.getTodaysReceiptTotal();
            lable[2] = "Receipt";
            ico[2] = R.drawable.ic_sale_bar;

            Log.i("Calling", "Function 4");
            values[3] = " " + getResources().getString(R.string.Rs) + " 0";
            lable[3] = "Payment";
            ico[3] = R.drawable.ic_sale_bar;

            //    String resval=mydb.getGroupOutstandingTotal(model.getSalesman());
            //  Log.i("Outs1",""+new BigDecimal(mydb.getGroupOutstandingTotal(model.getSalesman())).setScale(2,BigDecimal.ROUND_UP));
            //Log.i("Outs2",""+new BigDecimal(mydb.getGroupOutstandingTotal(model.getSalesman())).setScale());

            Log.i("Calling", "Function 5");
            values[4] = " " + getResources().getString(R.string.Rs) + " " + (new BigDecimal(mydb.getGroupOutstandingTotal(model.getSalesman()))).abs();
            lable[4] = "Outstanding Receivable";
            ico[4] = R.drawable.ic_outsrecv;

            Log.i("Calling", "Function 6");
            values[5] = " " + getResources().getString(R.string.Rs) + " 0";
            lable[5] = "Outstanding Payble";
            ico[5] = R.drawable.ic_outspayble;

            Log.i("Calling", "Function 7");
            values[6] = " " + getResources().getString(R.string.Rs) + " 0";
            lable[6] = "Sales Order";
            ico[6] = R.drawable.ic_sale_bar;

            Log.i("Calling", "Function 8");
            values[7] = " " + getResources().getString(R.string.Rs) + " 0";
            lable[7] = "Pauchase Order";
            ico[7] = R.drawable.ic_sale_bar;

            Log.i("Calling", "Function 9");
            values[8] = " " + getResources().getString(R.string.Rs) + " 0";
            lable[8] = "Delivery Note";
            ico[8] = R.drawable.ic_sale_bar;

            Log.i("Calling", "Function 10");
            values[9] = " " + getResources().getString(R.string.Rs) + " 0";
            lable[9] = "Receipt Note";
            ico[9] = R.drawable.ic_sale_bar;
            //={"Maharashtra","Himachal Pradesh","Kerala","Karnataka","Gujarat","Chhattisgarh","Jharkhand","Jammu and Kashmir","Haryana","Nagaland","Odisha","Punjab","Rajasthan","Sikkim"};
            // Integer[] propicid={R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps,R.drawable.gps};

            //   Log.i("Image",""+ico.length);
            //   Log.i("Label",""+ Arrays.toString(lable));
            //   Log.i("Value",""+Arrays.toString(values));

            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewHome.setLayoutManager(llm);
            AdapterHome adapter = new AdapterHome(ico, lable, values);
            recyclerViewHome.setLayoutManager(llm);
            recyclerViewHome.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.HORIZONTAL));
            recyclerViewHome.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
            // recyclerViewHome.setLayoutManager(new GridLayoutManager(this,2));

            recyclerViewHome.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //atv.setAdapter(adapter);*/

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Fail to view Dashboard " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Log.i("ID",""+id);

       if(id==R.id.nav_receipt)
        {
           Intent intent=new Intent(getApplicationContext(),CashReceipt.class);
           startActivity(intent);
        }
        if(id==R.id.nav_receiptview)
        {
            Intent intent=new Intent(getApplicationContext(),CashReceiptView.class);
            startActivity(intent);
        }
        if(id==R.id.nav_updown)
        {
            Intent intent=new Intent(getApplicationContext(), Synchronization.class);
            startActivity(intent);
        }
        if(id==R.id.nav_outstanding)
        {
            if(model.getUserType().equals("Admin")) {
                Intent intent = new Intent(getApplicationContext(), OutstandingAdmin.class);
                startActivity(intent);
            }
            if(model.getUserType().equals("Salesman"))
            {
                Intent intent = new Intent(getApplicationContext(), OutstandingSalesman.class);
                intent.putExtra("Salesman",""+model.getSalesman());
                startActivity(intent);
            }
        }
        if(id==R.id.nav_rebate)
        {
            if(model.getUserType().equals("Admin")) {
                Intent intent = new Intent(getApplicationContext(), RebateAdmin.class);
                startActivity(intent);
            }
            if(model.getUserType().equals("Salesman"))
            {
                Intent intent = new Intent(getApplicationContext(), RebateSalesman.class);
                intent.putExtra("Salesman",""+model.getSalesman());
                startActivity(intent);
            }
        }
        return true;
    }




    @Override
    protected void onStart() {
        // super.onResume();
        super.onStart();
        loadListItems(this);
        Log.i("Called 1 ","Start");
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        loadListItems(this);
        Log.i("Called 2 ","Pause");
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        loadListItems(this);
        Log.i("Called 3 ","Stop");

    }
    @Override
    protected void  onDestroy()
    {
        super.onDestroy();
        loadListItems(this);
        Log.i("Called 4 ","Distory");
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        loadListItems(this);
        Log.i("Called 5","On Resume");
    }


    @Override
    public void changefragment() {
        Log.i("Called 15","From Outstanding.");
      //  Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
    /*    Fragment someFragment = new PartyOutstandingFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction =fragmentManager.beginTransaction();
        Log.i("Pass","1");
            transaction.replace(R.id.ll, someFragment); // give your fragment container id in first parameter
        Log.i("Pass","2");
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        Log.i("Pass","3");
        transaction.commit();
        Log.i("Pass","4");*/
//        FragmentManager fragmentManager=getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.nav_host_fragment_content_menu_navigation, fragment, tag);
//        fragmentTransaction.addToBackStack(tag);
//        fragmentTransaction.commitAllowingStateLoss();

//        Fragment fragment = new PartyOutstandingFragment();
//        FragmentManager fm = getSupportFragmentManager();
//// create a FragmentTransaction to begin the transaction and replace the Fragment
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//// replace the FrameLayout with new Fragment
//        fragmentTransaction.add(R.id.nav_host_fragment_content_menu_navigation, fragment);
//        fragmentTransaction.commit(); // save the changes



    }
}

