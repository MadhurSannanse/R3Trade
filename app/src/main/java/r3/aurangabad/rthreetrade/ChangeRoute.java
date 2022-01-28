package r3.aurangabad.rthreetrade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeRoute extends AppCompatActivity {
    private ArrayAdapter adapter;
    String arrcustlist[];
    ListView lstroutes;
    TextView txtresult;
    Model model=Model.getInstance();
    Database mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_route);
        lstroutes=(ListView)findViewById(R.id.lst_routes);
        txtresult=(TextView)findViewById(R.id.txt_noroutemessage);
        try {
            mydb=new Database(getApplicationContext());
            model=Model.getInstance();
            arrcustlist=mydb.getAllLogin(model.getTallyName().toString());
            Log.i("Salesman List",""+ model.getTallyName());
            model=Model.getInstance();
            adapter=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1,arrcustlist);
            lstroutes.setAdapter(adapter);
            lstroutes.setEmptyView(txtresult);
        }catch (Exception ex){
            Log.e("Error 1",""+ex.getLocalizedMessage());}
        try {

            lstroutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent resultIntent = new Intent();
                    //resultIntent.putExtra("SelectedCustomer", "" + parent.getItemAtPosition(position).toString());
                    setResult(Activity.RESULT_OK, resultIntent);
                    model.setSalesman(parent.getItemAtPosition(position).toString());
                    Toast.makeText(getApplicationContext(), "Selected Salesman "+model.getSalesman(), Toast.LENGTH_SHORT).show();
                    finish();
                    //Toast.makeText(getApplicationContext(),""+,Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex){Log.e("Search Cust Error",""+ex.getLocalizedMessage());}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("Menu det",""+menu.toString());
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_customre, menu);
        MenuItem menuItem=(MenuItem) menu.findItem(R.id.nav_searchcustomer);
        SearchView searchView= (SearchView) menuItem.getActionView();

        searchView.setQueryHint("Search Route");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}