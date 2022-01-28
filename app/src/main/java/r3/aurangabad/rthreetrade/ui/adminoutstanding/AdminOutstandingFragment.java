package r3.aurangabad.rthreetrade.ui.adminoutstanding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

import r3.aurangabad.rthreetrade.AdapterOutstanding;
import r3.aurangabad.rthreetrade.Database;
import r3.aurangabad.rthreetrade.MenuNavigation;
import r3.aurangabad.rthreetrade.Model;
import r3.aurangabad.rthreetrade.R;
import r3.aurangabad.rthreetrade.SearchCustomer;

public class AdminOutstandingFragment extends Fragment implements View.OnClickListener {
    Model model;
    RecyclerView recyclerViewOutstanding;
    TextView txttotal;
    Database mydb;
    Vector adminouts;
    FragmentActivity mActivity;
MenuNavigation m;
    public interface  adminOutstanding{
        public void changefragment();
    }
    public static AdminOutstandingFragment newInstance() {
        return new AdminOutstandingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_outstanding_fragment,
                container, false);
        setHasOptionsMenu(true);
        model = Model.getInstance();
        mydb = new Database(getContext());
        recyclerViewOutstanding = (RecyclerView) view.findViewById(R.id.recyler_adminoutstanding);
        txttotal = (TextView) view.findViewById(R.id.txt_admintotaloutstanding);
        loadOutstanding();
        return view;
        //   return inflater.inflate(R.layout.admin_outstanding_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    void loadOutstanding() {
        try {
            double totalamount = 0;
            String total = "";
            mydb = new Database(getContext());
            model = Model.getInstance();
            adminouts = mydb.getAllOutstanding();
            String name[] = new String[adminouts.size()];
            String details[] = new String[adminouts.size()];
            String amount[] = new String[adminouts.size()];
            for (int i = 0; i < adminouts.size(); i++) {
                Vector data = (Vector) adminouts.elementAt(i);
                name[i] = "" + data.elementAt(0);
                details[i] = "Total Bills :" + data.elementAt(1);
                total = "0";
                total = mydb.getSalesmanOutstandingTotal("" + data.elementAt(0));
                amount[i] = "" + getResources().getString(R.string.Rs) + " " + Double.valueOf(total).intValue();
                totalamount += Double.valueOf(total).intValue();
            }
            txttotal.setText("" + Double.valueOf(totalamount).intValue());
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerViewOutstanding.setLayoutManager(llm);
            AdapterOutstanding adapter = new AdapterOutstanding(name, details, amount,getContext());
            recyclerViewOutstanding.setLayoutManager(llm);
            recyclerViewOutstanding.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.HORIZONTAL));
            recyclerViewOutstanding.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL));
            recyclerViewOutstanding.setAdapter(adapter);
        } catch (Exception ex) {
            Log.e("Error 1", "" + ex.getLocalizedMessage());
        }
    }

    public void loadNextPage(String Salesman) {
        try {

          /*  Fragment someFragment = new PartyOutstandingFragment();
            Fragment productDetailFragment = someFragment.requireParentFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.admin_fragment, productDetailFragment).commit();
           */
            //FragmentTransaction transaction = getChildFragmentManager().beginTransaction();



//       //     FragmentTransaction transaction = this.getChildFragmentManager().beginTransaction();
//            transaction.replace(R.id.admin_fragment, someFragment); // give your fragment container id in first parameter
//            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
//            transaction.commit();
//            m=new MenuNavigation();
//            m.changefragment();
          /*  FragmentManager fragmentManager=null;
            Fragment someFragment = new PartyOutstandingFragment();
            fragmentManager=someFragment.getChildFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.admin_fragment,someFragment,"Hello");
            fragmentTransaction.commit();*/

            Intent intent=new Intent(getContext(), SearchCustomer.class);
            startActivity(intent);


        } catch (Exception ex) {
            Log.e("Calling Error", "" + ex.getLocalizedMessage());
        }
    }
    public FragmentManager getHostFragmentManager() {
        FragmentManager fm = getFragmentManager();

            fm = ((AppCompatActivity)getActivity()).getSupportFragmentManager();

        return fm;
    }

    @Override
    public void onClick(View v) {
    }
}