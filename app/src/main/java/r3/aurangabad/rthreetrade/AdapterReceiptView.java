package r3.aurangabad.rthreetrade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterReceiptView  extends RecyclerView.Adapter<AdapterReceiptView.MyHolder>{
    private String partyname[],date[],total[];

    public AdapterReceiptView(String[] partyname, String[] date, String[] total) {
        this.partyname = partyname;
        this.date = date;
        this.total = total;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.item_receiptview,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.txtpartyname.setText(partyname[position]);
        holder.txtdate.setText(date[position]);
        holder.txttotal.setText(total[position]);


    }


    @Override
    public int getItemCount() {
        return partyname.length;
    }
    class MyHolder extends RecyclerView.ViewHolder
    {
        TextView txtpartyname,txtdate,txttotal;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            txtpartyname=(TextView) itemView.findViewById(R.id.txt_party);
            txtdate=(TextView) itemView.findViewById(R.id.txt_partydate);
            txttotal=(TextView) itemView.findViewById(R.id.txt_partytotal);
        }
    }
}
