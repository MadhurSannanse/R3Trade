package r3.aurangabad.rthreetrade;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

public class OutstandingSalesman extends AppCompatActivity {
    Model model;
    RecyclerView recyclerViewOutstanding;
    TextView txttotal;
    Database mydb;
    Vector salesmanouts;
    String salesman="",Closing="";
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstanding_salesman);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        try {
            salesman = b.getString("Salesman");
            Closing = b.getString("Closing");
        }
        catch (Exception ex){Log.e("Error Get values",""+ex.getLocalizedMessage());}
        model = Model.getInstance();
        mydb = new Database(getApplicationContext());
        recyclerViewOutstanding = (RecyclerView) findViewById(R.id.recyler_salesmanoutstanding);
        txttotal = (TextView) findViewById(R.id.txt_salesmantotaloutstanding);
        barChart = (BarChart) findViewById(R.id.barchart_salesmanoutstanding);
        loadOutstanding();
        getBarEntries();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_date:
                //   showDialog(DATE_PICKER);
                break;
            case R.id.action_pdf :
              //  createPDFInvoice(Cutomername);
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
    void loadOutstanding() {
        try {
            double totalamount = 0;
            String total = "";
            BigDecimal tot=new BigDecimal(0);
            mydb = new Database(getApplicationContext());
            model = Model.getInstance();
            salesmanouts = mydb.getSalesmanOutstanding(salesman);
            String name[] = new String[salesmanouts.size()];
            String details[] = new String[salesmanouts.size()];
            String amount[] = new String[salesmanouts.size()];
            for (int i = 0; i < salesmanouts.size(); i++) {
                Vector data = (Vector) salesmanouts.elementAt(i);
                name[i] = "" + data.elementAt(0);
                details[i] = "Total Bills :" + data.elementAt(1);
                total = "0";
                try {
                    total ="" + data.elementAt(2);
                }catch (Exception ex){tot=new BigDecimal(0);}
             amount[i] = ""  + Math.abs(Double.valueOf(total).intValue());
                totalamount += Math.abs(Double.valueOf(total).intValue());
            }
            txttotal.setText("" + Double.valueOf(totalamount).intValue());
            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewOutstanding.setLayoutManager(llm);
            AdapterOutstandingSalesman adapter = new AdapterOutstandingSalesman(name, details, amount,OutstandingSalesman.this);
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
        Vector data=mydb.getSalesmanOutstandingMonthCount(salesman);
        for (int i=0;i<data.size();i++) {
            barEntriesArrayList.add(new BarEntry((i+1), Float.parseFloat(""+data.elementAt(i))));
        }


        barDataSet = new BarDataSet(barEntriesArrayList, "Total Outstanding  Monthwise");

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