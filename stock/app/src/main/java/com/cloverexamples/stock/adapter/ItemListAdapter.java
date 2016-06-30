package com.cloverexamples.stock.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cloverexamples.stock.R;
import com.cloverexamples.stock.activity.MainActivity;
import com.cloverexamples.stock.entry.ItemEntry;

import java.util.List;

/**
 * Created by dewei.kung on 6/28/16.
 */
public class ItemListAdapter extends BaseAdapter {
    private static final String TAG = ItemListAdapter.class.getSimpleName();
    private MainActivity mMainActivity;
    private List<ItemEntry> mItemEntries;


//    private LayoutInflater mInflater;

//    public ItemListAdapter(Context context) {
//        super(context, android.R.layout.simple_list_item_2);
//        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }

    public ItemListAdapter(MainActivity activity, List<ItemEntry> itemEntries) {
        mMainActivity = activity;
        mItemEntries = itemEntries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mMainActivity);
            convertView = inflater.inflate(R.layout.item_entry, parent, false);
        }

        ItemEntry itemEntry = mItemEntries.get(position);
        TextView nameTxv = (TextView) convertView.findViewById(R.id.txv_item_name);
        nameTxv.setText(itemEntry.getItemName());
        TextView quantityTxv = (TextView) convertView.findViewById(R.id.txv_item_quantity);
        quantityTxv.setText(String.valueOf(itemEntry.getQuantity()));
        Button editBtn = (Button) convertView.findViewById(R.id.btn_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        TODO: pop out alert dialog, then make post request to update quantity

            }
        });

        return convertView;
    }

    public void setData(List<ItemEntry> data) {
        mItemEntries.clear();
        if (data != null) {
//            for (int i = 0; i < data.size(); i++) {
//                mItemEntries.add(data.get(i));
//            }
            mItemEntries.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItemEntries.size();
    }

    @Override
    public ItemEntry getItem(int position) {
        return mItemEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
