package com.example.puyo_base_simulator.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.puyo_base_simulator.R
import com.example.puyo_base_simulator.ui.home.LoadFieldAdapter.FieldViewHolder

class LoadFieldAdapter(private val mFields: List<FieldPreview>) : RecyclerView.Adapter<FieldViewHolder>() {
    private lateinit var mFieldSelectedListener: (Int, FieldPreview) -> Unit
    fun setFieldSelectedListener(fieldSelectedListener:(Int, FieldPreview) -> Unit) {
        mFieldSelectedListener = fieldSelectedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        return FieldViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_field, parent, false))
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        val fieldPreview = mFields[position]
        holder.fieldID.text = "seed: " + Integer.toString(fieldPreview.seed)
        holder.fieldCanvas.setField(fieldPreview.content)
        holder.itemView.setOnClickListener {
            if (mFieldSelectedListener != null) {
                mFieldSelectedListener!!.invoke(position, fieldPreview)
            }
        }
    }

    override fun getItemCount(): Int {
        return mFields.size
    }

    class FieldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fieldID: TextView
        var fieldCanvas: FieldPreviewCanvas

        init {
            fieldID = itemView.findViewById(R.id.fieldID)
            fieldCanvas = itemView.findViewById(R.id.fieldCanvas)
        }
    }

    interface FieldSelectedListener {
        fun onFieldSelected(position: Int, fieldPreview: FieldPreview?)
    }
}