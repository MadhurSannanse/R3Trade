package r3.aurangabad.rthreetrade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.MyHolder>{
     int images[];
     String label[],value[];


    public AdapterHome(int[] images, String[] label, String[] value) {
        this.images = images;
        this.label = label;
        this.value = value;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.layout_home,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
     //   Log.i("Binder","Called "+position);
        holder.imglabel.setImageResource(images[position]);
        holder.txtlabel.setText(label[position]);
        holder.txtvalue.setText(value[position]);
    }

    @Override
    public int getItemCount() {
       // return 8;
        return label.length;
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView imglabel;
        TextView txtlabel,txtvalue;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imglabel=(ImageView) itemView.findViewById(R.id.img_label);
            txtlabel=(TextView) itemView.findViewById(R.id.txt_lable);
            txtvalue=(TextView) itemView.findViewById(R.id.txt_value);
        }
    }


}
