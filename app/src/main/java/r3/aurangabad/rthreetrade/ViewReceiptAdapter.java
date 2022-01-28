package r3.aurangabad.rthreetrade;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by user on 9/3/2016.
 */
public class ViewReceiptAdapter extends ArrayAdapter<String> {
    String[] name = {};
    String[] total = {};
    String[] odate = {};
    String[] oid = {};
   // ViewOrder vieworder;

    Context context;
    LayoutInflater layoutInflater;

    public ViewReceiptAdapter(Context context, String[] name, String[] odate, String[] total, String[] oid) {
        super(context, R.layout.item_receiptview, name);

        this.context = context;
        this.name = name;
        this.total = total;
        this.oid = oid;
        this.odate = odate;
      //  vieworder = new ViewOrder();


    }

    public class ViewHolder {
        TextView txtparty, txtdate, txttotal;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_receiptview, null);

        }
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.txtparty = convertView.findViewById(R.id.txt_party);
        viewHolder.txtdate = convertView.findViewById(R.id.txt_partydate);
        viewHolder.txttotal = convertView.findViewById(R.id.txt_partytotal);

        viewHolder.txtparty.setText(name[position]);
        viewHolder.txtdate.setText(odate[position]);
        viewHolder.txttotal.setText(total[position]);


        try {
            convertView.setId(Integer.parseInt("" + oid[position].trim()));

        } catch (Exception ex) {
        }
        if (position == 0) {
            viewHolder.txtparty.setTextColor(Color.BLUE);
            viewHolder.txtdate.setTextColor(Color.BLUE);
            viewHolder.txttotal.setTextColor(Color.BLUE);
            viewHolder.txtparty.setTextSize(17);
            viewHolder.txtdate.setTextSize(17);
            viewHolder.txttotal.setTextSize(17);

        }
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_left);
        convertView.startAnimation(animation);
        return convertView;


    }

}
