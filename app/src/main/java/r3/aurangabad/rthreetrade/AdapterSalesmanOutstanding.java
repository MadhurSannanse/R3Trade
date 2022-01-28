package r3.aurangabad.rthreetrade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterSalesmanOutstanding extends RecyclerView.Adapter<AdapterSalesmanOutstanding.MyHolder>{
    private String name[],details[],amount[];

    public AdapterSalesmanOutstanding(String[] name, String[] details, String[] amount) {
        this.name = name;
        this.details = details;
        this.amount = amount;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.item_outstanding,parent,false);
        return new AdapterSalesmanOutstanding.MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.txtpartyname.setText(name[position]);
        holder.txtdetails.setText(details[position]);
        holder.txtamount.setText(amount[position]);
        //holder.txtpartyname.setId(position+1);
        holder.txtpartyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(v.getContext(), ""+holder.txtpartyname.getText(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView txtpartyname,txtdetails,txtamount;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            txtpartyname=(TextView) itemView.findViewById(R.id.txt_partyname);
            txtdetails=(TextView) itemView.findViewById(R.id.txt_totalbills);
            txtamount=(TextView) itemView.findViewById(R.id.txt_outstandingamount);
        }
    }

}
