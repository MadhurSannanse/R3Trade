package r3.aurangabad.rthreetrade.ui.salesmanoutstanding;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

import r3.aurangabad.rthreetrade.AdapterSalesmanOutstanding;
import r3.aurangabad.rthreetrade.Database;
import r3.aurangabad.rthreetrade.Model;
import r3.aurangabad.rthreetrade.R;
import r3.aurangabad.rthreetrade.ui.adminoutstanding.AdminOutstandingFragment;

public class SalesmanOutstandingFragment extends Fragment {
    Model model;
    RecyclerView recyclerViewOutstanding;
    TextView txttotal;
    Database mydb;
    Vector adminouts;

    public static SalesmanOutstandingFragment newInstance() {
        return new SalesmanOutstandingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.salesman_outstanding_fragment
                ,
                container, false);
        setHasOptionsMenu(true);
        model=Model.getInstance();
        mydb=new Database(getContext());
        recyclerViewOutstanding=(RecyclerView) view.findViewById(R.id.recyler_salesmanoutstanding);
        txttotal=(TextView)view.findViewById(R.id.txt_salesmantotaloutstanding);
     //   loadOutstanding();
        return view;
    }
    void loadOutstanding()
    {
        try{
            double totalamount=0;
            String total="";
            mydb=new Database(getContext());
            model=Model.getInstance();
            adminouts=mydb.getAllOutstanding();
            String name[]=new String[adminouts.size()];
            String details[]=new String[adminouts.size()];
            String amount[]=new String[adminouts.size()];
            for(int i=0;i<adminouts.size();i++){
                Vector data=(Vector) adminouts.elementAt(i);
                name[i]=""+data.elementAt(0);
                details[i]="Total Bills :"+data.elementAt(1);
                total="0";
                total=mydb.getSalesmanOutstandingTotal(""+data.elementAt(0));
                amount[i]=""+getResources().getString(R.string.Rs)+" "+Double.valueOf(total).intValue();
                totalamount+=Double.valueOf(total).intValue();
            }
            txttotal.setText(""+Double.valueOf(totalamount).intValue());
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewOutstanding.setLayoutManager(llm);
            AdapterSalesmanOutstanding adapter = new AdapterSalesmanOutstanding(name,details,amount);
            recyclerViewOutstanding.setLayoutManager(llm);
            recyclerViewOutstanding.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.HORIZONTAL));
            recyclerViewOutstanding.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL));
            recyclerViewOutstanding.setAdapter(adapter);


        }
        catch (Exception ex){
            Log.e("Error 1",""+ex.getLocalizedMessage());
        }
    }
    public void loadNextPage(String Salesman)
    {
        try
        {
            Fragment fragment = new AdminOutstandingFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_fragment, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

        }
        catch (Exception ex){

        }
    }

}