package com.example.puyo_base_simulator.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.puyo_base_simulator.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class FieldLoadAdapter extends RecyclerView.Adapter<FieldLoadAdapter.FieldViewHolder> {
    //private String[] mDataset;
    private List<String> mDataset;

    public static class FieldViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;

        public FieldViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FieldLoadAdapter(List<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FieldViewHolder onCreateViewHolder(ViewGroup parent,
                                              int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_load, parent, false);
        FieldViewHolder vh = new FieldViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FieldViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}