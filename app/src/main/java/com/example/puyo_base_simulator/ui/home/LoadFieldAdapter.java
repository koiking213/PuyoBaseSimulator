package com.example.puyo_base_simulator.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.puyo_base_simulator.R;

import java.util.List;

public class LoadFieldAdapter extends RecyclerView.Adapter<LoadFieldAdapter.FieldViewHolder> {

    private List<FieldPreview> mFields;
    private FieldSelectedListener mFieldSelectedListener;

    public LoadFieldAdapter(List<FieldPreview> fields){
        super();
        this.mFields = fields;
    }

    public void setFieldSelectedListener(FieldSelectedListener fieldSelectedListener) {
        this.mFieldSelectedListener = fieldSelectedListener;
    }

    @Override
    public FieldViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FieldViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_field, parent, false));
    }

    @Override
    public void onBindViewHolder(FieldViewHolder holder, final int position) {
        final FieldPreview fieldPreview = mFields.get(position);
        holder.icon.setImageResource(fieldPreview.iconRes);
        holder.fieldID.setText(fieldPreview.content);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFieldSelectedListener != null){
                    mFieldSelectedListener.onFieldSelected(position, fieldPreview);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFields.size();
    }

    static class FieldViewHolder extends RecyclerView.ViewHolder{
        TextView fieldID;
        ImageView icon;

        public FieldViewHolder(View itemView) {
            super(itemView);
            fieldID = itemView.findViewById(R.id.fieldID);
            icon = itemView.findViewById(R.id.icon);
        }
    }

    interface FieldSelectedListener {
        void onFieldSelected(int position, FieldPreview fieldPreview);
    }
}
