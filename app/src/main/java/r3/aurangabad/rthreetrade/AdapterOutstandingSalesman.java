package r3.aurangabad.rthreetrade;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterOutstandingSalesman extends RecyclerView.Adapter<AdapterOutstandingSalesman.MyHolder>{
private String name[],details[],amount[];
private Context context;
public AdapterOutstandingSalesman(String[] name, String[] details, String[] amount, Context context) {
        this.name = name;
        this.details = details;
        this.amount = amount;
        this.context=context;

        }

@NonNull
@Override
public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.item_outstanding,parent,false);
        return new MyHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.txtpartyname.setText(name[position]);
        holder.txtdetails.setText(details[position]);
        holder.txtamount.setText(context.getResources().getString(R.string.Rs)+" "+amount[position]);
        //holder.txtpartyname.setId(position+1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        Intent intent=new Intent(context,OutstandingDetails.class);
        intent.putExtra("Customer",""+holder.txtpartyname.getText());
        intent.putExtra("Closing",""+amount[position]);
        context.startActivity(intent);
        }
        });


        }

@Override
public int getItemCount() {
        return name.length;
        }

class MyHolder extends RecyclerView.ViewHolder {
    TextView txtpartyname, txtdetails, txtamount;
    AdapterView.OnItemClickListener onItemClickListener;

    public MyHolder(@NonNull View itemView) {
        super(itemView);
        txtpartyname = (TextView) itemView.findViewById(R.id.txt_partyname);
        txtdetails = (TextView) itemView.findViewById(R.id.txt_totalbills);
        txtamount = (TextView) itemView.findViewById(R.id.txt_outstandingamount);
    }

}

}


