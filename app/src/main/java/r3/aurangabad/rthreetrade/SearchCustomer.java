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

import androidx.appcompat.app.AppCompatActivity;

public class SearchCustomer extends AppCompatActivity {
  private ArrayAdapter adapter;
  String arrcustlist[];
  ListView lstcustomers;
  TextView txtresult;
    Model model=Model.getInstance();
    Database mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_customer);
       // Log.i("Size",""+arrcustlist.length);
        lstcustomers=(ListView)findViewById(R.id.lst_customers);
        txtresult=(TextView)findViewById(R.id.txt_noresultmessage);
        try {
            mydb=new Database(getApplicationContext());
            arrcustlist=mydb.getAllCustomer();
            model=Model.getInstance();
            adapter=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1,arrcustlist);
            lstcustomers.setAdapter(adapter);
            lstcustomers.setEmptyView(txtresult);
        }catch (Exception ex){Log.e("Error 1",""+ex.getLocalizedMessage());}
        // Log.i("User",""+model.getUserType());
       /* arrcustlist[0]="Beer and wines";
        arrcustlist[1]="First Wine Shop";
        arrcustlist[2]="Second Wine Shop";
        arrcustlist[3]="Third Wine Shop";
        arrcustlist[4]="Fourth Wine Shop";
        arrcustlist[5]="Fifth Wine Shop";
        arrcustlist[6]="Six Wine Shop";
        arrcustlist[7]="Seven Wine Shop";
        arrcustlist[8]="Eight Wine Shop";
        arrcustlist[9]="Nine Wine Shop";
        arrcustlist[10]="Ten Wine Shop";
        arrcustlist[11]="First Wine Shop";
        arrcustlist[12]="Second Wine Shop";
        arrcustlist[13]="Third Wine Shop";
        arrcustlist[14]="Fourth Wine Shop";
        arrcustlist[15]="Fifth Wine Shop";
        arrcustlist[16]="Six Wine Shop";
        arrcustlist[17]="Seven Wine Shop";
        arrcustlist[18]="Eight Wine Shop";
        arrcustlist[19]="Nine Wine Shop";*/
     //
     try {

            lstcustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SelectedCustomer", "" + parent.getItemAtPosition(position).toString());
                    setResult(Activity.RESULT_OK, resultIntent);
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

        searchView.setQueryHint("Search Customer");

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