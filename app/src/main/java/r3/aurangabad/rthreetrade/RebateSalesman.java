package r3.aurangabad.rthreetrade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.Vector;

public class RebateSalesman extends AppCompatActivity {
    Model model;
    RecyclerView recyclerViewRebate;
    TextView txttotal;
    Database mydb;
    Vector salesmanrebate;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;
    String salesman="",Closing="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebate_salesman);
        model = Model.getInstance();
        mydb = new Database(getApplicationContext());
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        try {
            salesman = b.getString("Salesman");
          //  Closing = b.getString("Closing");
        }
        catch (Exception ex){Log.e("Error Get values",""+ex.getLocalizedMessage());}

        recyclerViewRebate = (RecyclerView) findViewById(R.id.recyler_salesmanrebate);
        txttotal = (TextView)findViewById(R.id.txt_salesmantotalrebate);
        barChart = (BarChart) findViewById(R.id.barchart_salesmanrebate);
        loadRebate();
       // getBarEntries();
    }
    void loadRebate() {
        try {
            double totalamount = 0;
            String total = "";
            mydb = new Database(getApplicationContext());
            model = Model.getInstance();
            salesmanrebate = mydb.getSalesmanRebate(salesman);

            String name[] = new String[salesmanrebate.size()];
            String details[] = new String[salesmanrebate.size()];
            String amount[] = new String[salesmanrebate.size()];
            for (int i = 0; i < salesmanrebate.size(); i++) {
                Vector data = (Vector) salesmanrebate.elementAt(i);
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
            AdapterRebateSalesman adapter = new AdapterRebateSalesman(name, details, amount,RebateSalesman.this);
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
                loadMonthSelector(); //   showDialog(DATE_PICKER);
                break;
            case R.id.action_pdf :
                //   createPDFInvoice(Cutomername);
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
                        intent.putExtra("Closing",""+closing);
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
        Vector bardata=mydb.getSalesmanOutstandingMonthCount(salesman);
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

                    salesmanrebate=mydb.getAllRebateByMonth(strmonth);
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